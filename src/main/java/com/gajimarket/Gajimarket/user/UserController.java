package com.gajimarket.Gajimarket.user;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map; // Map 인터페이스 임포트
import java.util.HashMap; // HashMap 클래스 임포트

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signin(@RequestBody UserLoginRequest userLoginRequest, HttpSession session) {
        logger.info("로그인 요청: 아이디 = {}", userLoginRequest.getId());

        boolean isAuthenticated = userService.authenticate(userLoginRequest.getId(), userLoginRequest.getPasswd());

        Map<String, String> response = new HashMap<>();
        if (isAuthenticated) {
            // 세션에 사용자 정보 저장 (예: userIdx, nickname 등)
            session.setAttribute("id", userLoginRequest.getId()); // 로그인한 아이디 저장
            //session.setAttribute("name", userLoginRequest.getName()); // 이름 저장
            logger.info("로그인 성공: 아이디 = {}", userLoginRequest.getId());
            response.put("message", "로그인 성공");
            return ResponseEntity.ok(response); // JSON 형식으로 반환
        } else {
            logger.warn("로그인 실패: 아이디 = {}", userLoginRequest.getId());
            response.put("message", "아이디나 비밀번호가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

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

    /*
    @PostMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestParam("image") MultipartFile image,
                                                @RequestParam("message") String message,
                                                HttpSession session) {
        try {
            // 세션에서 userIdx 가져오기
            Int user_idx = (Long) session.getAttribute("userIdx");
            if (userIdx == null) {
                return ResponseEntity.status(401).body("사용자 인증 실패");
            }

            // 이미지 파일 저장
            String imagePath = "uploads/" + image.getOriginalFilename();
            File saveFile = new File(imagePath);
            image.transferTo(saveFile);

            // 데이터베이스에 message와 imagePath 저장
            userService.updateProfile(userIdx, message, imagePath);

            return ResponseEntity.ok("프로필 업데이트 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("프로필 업데이트 실패");
        }
    }
    */

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();  // 세션 종료
        return ResponseEntity.ok().body("로그아웃 성공");
    }

    // 세션 상태 확인 API
    @GetMapping("/session")
    public ResponseEntity<?> getSessionInfo(HttpSession session) {
        Object userIdx = session.getAttribute("userIdx");
        if (userIdx != null) {
            return ResponseEntity.ok().body("세션 유효, 사용자 ID: " + userIdx);
        } else {
            return ResponseEntity.status(401).body("세션 없음");
        }
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        logger.info("사용자 정보 조회: 아이디 = {}", id); // 디버깅 로그 추가
        return userService.findUserById(id);
    }
}
