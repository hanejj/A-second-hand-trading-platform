// UserController.java
package com.gajimarket.Gajimarket.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private static final String SECRET_KEY = "123321";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signin(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        logger.info("로그인 요청: 아이디 = {}", userLoginRequest.getId());

        // 인증 처리
        boolean isAuthenticated = userService.authenticate(userLoginRequest.getId(), userLoginRequest.getPasswd());

        Map<String, String> responseBody = new HashMap<>();
        if (isAuthenticated) {
            logger.info("로그인 성공: 아이디 = {}", userLoginRequest.getId());
            responseBody.put("message", "로그인 성공");

            // JWT 생성
            String token = Jwts.builder()
                    .setSubject(userLoginRequest.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간 유효
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();

            // Authorization 헤더와 CORS 관련 헤더 설정
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            response.setHeader("Authorization", "Bearer " + token);

            return ResponseEntity.ok(responseBody);
        } else {
            logger.warn("로그인 실패: 아이디 = {}", userLoginRequest.getId());
            responseBody.put("message", "아이디나 비밀번호가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }
    }


    // 사용자 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }

        try {
            String jwtToken = token.substring(7);
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(jwtToken)
                    .getBody();

            String userId = claims.getSubject();
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }

    // 로그아웃 처리 (토큰 기반 로그아웃은 클라이언트 측에서 토큰을 삭제하는 것으로 처리)
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        logger.info("로그아웃 요청");
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "로그아웃 성공");
        return ResponseEntity.ok(responseBody);
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        try {
            userService.signUp(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 처리 중 오류 발생");
        }
    }
}