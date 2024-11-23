// UserController.java
package com.gajimarket.Gajimarket.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.nio.file.StandardCopyOption;

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

    // 특정 사용자의 정보 조회 (이메일로 user_idx 조회)
    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 데이터베이스 연결
            String url = "jdbc:mysql://localhost:3306/gajimarket";
            String username = "root";
            String password = "1234";
            connection = DriverManager.getConnection(url, username, password);

            // 사용자 정보 조회 쿼리 실행
            String query = "SELECT user_idx, id, name, message, manner_point FROM user WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUserIdx(resultSet.getInt("user_idx"));
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setMessage(resultSet.getString("message"));
                user.setMannerPoint(resultSet.getInt("manner_point"));
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보를 가져오는 중 오류 발생");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 특정 사용자의 판매 상품 조회 (이메일로 판매 상품 조회)
    @GetMapping("/{email}/selling")
    public ResponseEntity<?> getUserSellingProducts(@PathVariable String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 데이터베이스 연결
            String url = "jdbc:mysql://localhost:3306/gajimarket";
            String username = "root";
            String password = "1234";
            connection = DriverManager.getConnection(url, username, password);

            // 판매 상품 조회 쿼리 실행
            String query = "SELECT product_idx, title, price, location, heart_num, chat_num, image FROM product WHERE writer_idx = (SELECT user_idx FROM user WHERE id = ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            List<Map<String, Object>> products = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("product_idx", resultSet.getInt("product_idx"));
                product.put("title", resultSet.getString("title"));
                product.put("price", resultSet.getInt("price"));
                product.put("location", resultSet.getString("location"));
                product.put("heart_num", resultSet.getInt("heart_num"));
                product.put("chat_num", resultSet.getInt("chat_num"));
                product.put("image", resultSet.getString("image"));
                products.add(product);
            }

            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("판매 상품 정보를 가져오는 중 오류 발생");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 포인트 업데이트 API 추가
    @PostMapping("/{email}/point/update")
    public ResponseEntity<?> updateUserPoint(@PathVariable String email, @RequestBody Map<String, Integer> requestBody) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 데이터베이스 연결
            String url = "jdbc:mysql://localhost:3306/gajimarket";
            String username = "root";
            String password = "1234";
            connection = DriverManager.getConnection(url, username, password);

            // 포인트 업데이트 쿼리 실행
            String updateQuery = "INSERT INTO point (user_idx, point) VALUES ((SELECT user_idx FROM user WHERE id = ?), ?) "
                    + "ON DUPLICATE KEY UPDATE point = point + ?";
            preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, requestBody.get("amount"));
            preparedStatement.setInt(3, requestBody.get("amount"));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return ResponseEntity.ok("포인트가 성공적으로 업데이트되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("포인트 업데이트 중 오류 발생");
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 특정 사용자의 포인트 조회
    @GetMapping("/{email}/point")
    public ResponseEntity<?> getUserPoint(@PathVariable String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 데이터베이스 연결
            // 데이터베이스 연결
            String url = "jdbc:mysql://localhost:3306/gajimarket";
            String username = "root";
            String password = "1234";
            connection = DriverManager.getConnection(url, username, password);

            // 포인트 조회 쿼리 실행
            String query = "SELECT point FROM Point WHERE user_idx = (SELECT user_idx FROM User WHERE id = ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            int point = 0;
            if (resultSet.next()) {
                point = resultSet.getInt("point");
            }

            Map<String, Integer> response = new HashMap<>();
            response.put("point", point);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("포인트 정보를 가져오는 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("포인트 정보를 가져오는 중 오류 발생");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Wishlist 조회 API 추가
    @GetMapping("/{email}/get/wishlist")
    public ResponseEntity<?> getUserWishlist(@PathVariable String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 데이터베이스 연결
            String url = "jdbc:mysql://localhost:3306/gajimarket";
            String username = "root";
            String password = "1234";
            connection = DriverManager.getConnection(url, username, password);

            // 찜 목록 조회 쿼리 실행
            String query = "SELECT p.product_idx, p.title, p.price, p.location, p.heart_num, p.chat_num, p.image " +
                    "FROM Wishlist w " +
                    "JOIN Product p ON w.product_idx = p.product_idx " +
                    "WHERE w.user_idx = (SELECT user_idx FROM User WHERE id = ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            List<Map<String, Object>> wishlist = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("product_idx", resultSet.getInt("product_idx"));
                product.put("title", resultSet.getString("title"));
                product.put("price", resultSet.getInt("price"));
                product.put("location", resultSet.getString("location"));
                product.put("heart_num", resultSet.getInt("heart_num"));
                product.put("chat_num", resultSet.getInt("chat_num"));
                product.put("image", resultSet.getString("image"));
                wishlist.add(product);
            }

            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("찜 목록 정보를 가져오는 중 오류 발생");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 비밀번호 인증 API
    @PostMapping("/{email}/auth")
    public ResponseEntity<?> authenticateUser(@PathVariable String email, @RequestBody Map<String, String> requestBody) {
        String password = requestBody.get("password");
        boolean authenticated = userService.authenticate(email, password);

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", authenticated);

        if (authenticated) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PutMapping("/{email}/edit")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable String email,
            @RequestPart("user") User updatedUser,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            // 이미지 파일이 있는 경우 파일을 저장하고 사용자 데이터에 반영
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = saveImage(imageFile);
                updatedUser.setImage(imagePath);
            }

            User user = userService.updateUserDetails(email, updatedUser);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보를 수정하는 중 오류 발생");
        }
    }

    private String saveImage(MultipartFile file) throws IOException {
        // 이미지 파일 저장 로직을 작성합니다.
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path imagePath = Paths.get("images/" + fileName);
        Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        return imagePath.toString();
    }

    // 기존 사용자 정보 조회 API
    @GetMapping("/{email}/get")
    public ResponseEntity<User> getUserDetails(@PathVariable String email) {
        try {
            User user = userService.findUserById(email);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error("Error during getting user details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}