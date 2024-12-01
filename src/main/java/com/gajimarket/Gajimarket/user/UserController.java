package com.gajimarket.Gajimarket.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gajimarket.Gajimarket.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final DataSource dataSource;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, DataSource dataSource) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.dataSource = dataSource;
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signin(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        logger.info("로그인 요청: 아이디 = {}", userLoginRequest.getId());
        Map<String, Object> responseBody = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            String userQuery = "SELECT * FROM user WHERE id = ? AND passwd = ?";
            PreparedStatement userStmt = connection.prepareStatement(userQuery);
            userStmt.setString(1, userLoginRequest.getId());
            userStmt.setString(2, userLoginRequest.getPasswd());
            ResultSet userResult = userStmt.executeQuery();

            if (userResult.next()) {
                String token = jwtTokenProvider.generateToken(userLoginRequest.getId());
                responseBody.put("code", 1000);
                responseBody.put("message", "로그인 성공");
                responseBody.put("token", token);
                responseBody.put("isAdmin", false);
                return ResponseEntity.ok(responseBody);
            }

            String adminQuery = "SELECT * FROM admin WHERE id = ? AND passwd = ?";
            PreparedStatement adminStmt = connection.prepareStatement(adminQuery);
            adminStmt.setString(1, userLoginRequest.getId());
            adminStmt.setString(2, userLoginRequest.getPasswd());
            ResultSet adminResult = adminStmt.executeQuery();

            if (adminResult.next()) {
                String token = jwtTokenProvider.generateToken(userLoginRequest.getId());
                responseBody.put("code", 1000);
                responseBody.put("message", "로그인 성공");
                responseBody.put("token", token);
                responseBody.put("isAdmin", true);
                return ResponseEntity.ok(responseBody);
            }

            responseBody.put("code", 0);
            responseBody.put("message", "아이디나 비밀번호가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        } catch (Exception e) {
            logger.error("로그인 중 오류 발생: ", e);
            responseBody.put("code", 0);
            responseBody.put("message", "로그인 처리 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, Object> responseBody = new HashMap<>();

        if (token == null || !token.startsWith("Bearer ")) {
            responseBody.put("code", 0);
            responseBody.put("message", "토큰이 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            responseBody.put("code", 0);
            responseBody.put("message", "유효하지 않은 토큰입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

        String userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userService.findUserById(userId);

        if (user == null) {
            responseBody.put("code", 0);
            responseBody.put("message", "사용자를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }

        responseBody.put("code", 1000);
        responseBody.put("user", user);
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        logger.info("로그아웃 요청");
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", 1000);
        responseBody.put("message", "로그아웃 성공");
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody UserRegisterRequest userRegisterRequest) {
        Map<String, Object> responseBody = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            String checkUserQuery = "SELECT * FROM user WHERE id = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
            checkUserStmt.setString(1, userRegisterRequest.getId());
            ResultSet userResult = checkUserStmt.executeQuery();

            if (userResult.next()) {
                responseBody.put("code", 0);
                responseBody.put("message", "이미 존재하는 사용자입니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
            }

            String checkAdminQuery = "SELECT * FROM admin WHERE id = ?";
            PreparedStatement checkAdminStmt = connection.prepareStatement(checkAdminQuery);
            checkAdminStmt.setString(1, userRegisterRequest.getId());
            ResultSet adminResult = checkAdminStmt.executeQuery();

            if (adminResult.next()) {
                responseBody.put("code", 0);
                responseBody.put("message", "이미 존재하는 관리자입니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
            }

            User user = new User();
            user.setId(userRegisterRequest.getId());
            user.setPasswd(userRegisterRequest.getPasswd());
            user.setName(userRegisterRequest.getName());
            user.setBirth(userRegisterRequest.getBirth());
            user.setSex(userRegisterRequest.getSex());
            user.setPhone(userRegisterRequest.getPhone());
            user.setNickname(userRegisterRequest.getNickname());
            user.setLocation(userRegisterRequest.getLocation());

            userService.signUp(user);
            responseBody.put("code", 1000);
            responseBody.put("message", "회원가입 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (Exception e) {
            logger.error("회원가입 처리 중 오류 발생: ", e);
            responseBody.put("code", 0);
            responseBody.put("message", "회원가입 처리 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        Map<String, Object> responseBody = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT user_idx, id, name, message, manner_point FROM user WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUserIdx(resultSet.getInt("user_idx"));
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setMessage(resultSet.getString("message"));
                user.setMannerPoint(resultSet.getInt("manner_point"));
                responseBody.put("code", 1000);
                responseBody.put("user", user);
                return ResponseEntity.ok(responseBody);
            } else {
                responseBody.put("code", 0);
                responseBody.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }
        } catch (Exception e) {
            responseBody.put("code", 0);
            responseBody.put("message", "사용자 정보를 가져오는 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    // 특정 사용자의 판매 상품 조회 (이메일로 판매 상품 조회)
    @GetMapping("/{email}/selling")
    public ResponseEntity<Map<String, Object>> getUserSellingProducts(@PathVariable String email) {
        Map<String, Object> responseBody = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT product_idx, title, price, location, heart_num, chat_num, image FROM product WHERE writer_idx = (SELECT user_idx FROM user WHERE id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

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

            responseBody.put("code", 1000);
            responseBody.put("products", products);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            responseBody.put("code", 0);
            responseBody.put("message", "판매 상품 정보를 가져오는 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    // 포인트 업데이트 API
    @PostMapping("/{email}/point/update")
    public ResponseEntity<Map<String, Object>> updateUserPoint(@PathVariable String email, @RequestBody Map<String, Integer> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            String updateQuery = "INSERT INTO point (user_idx, point) VALUES ((SELECT user_idx FROM user WHERE id = ?), ?) "
                    + "ON DUPLICATE KEY UPDATE point = point + ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, requestBody.get("amount"));
            preparedStatement.setInt(3, requestBody.get("amount"));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                responseBody.put("code", 1000);
                responseBody.put("message", "포인트가 성공적으로 업데이트되었습니다.");
                return ResponseEntity.ok(responseBody);
            } else {
                responseBody.put("code", 0);
                responseBody.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }
        } catch (Exception e) {
            responseBody.put("code", 0);
            responseBody.put("message", "포인트 업데이트 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    // 특정 사용자의 포인트 조회
    @GetMapping("/{email}/point")
    public ResponseEntity<Map<String, Object>> getUserPoint(@PathVariable String email) {
        Map<String, Object> responseBody = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT point FROM point WHERE user_idx = (SELECT user_idx FROM user WHERE id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                responseBody.put("code", 1000);
                responseBody.put("point", resultSet.getInt("point"));
            } else {
                responseBody.put("code", 1000);
                responseBody.put("point", 0);
            }
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            responseBody.put("code", 0);
            responseBody.put("message", "포인트 정보를 가져오는 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    // Wishlist 조회 API
    @GetMapping("/{email}/get/wishlist")
    public ResponseEntity<Map<String, Object>> getUserWishlist(@PathVariable String email) {
        Map<String, Object> responseBody = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT p.product_idx, p.title, p.price, p.location, p.heart_num, p.chat_num, p.image " +
                    "FROM Wishlist w " +
                    "JOIN Product p ON w.product_idx = p.product_idx " +
                    "WHERE w.user_idx = (SELECT user_idx FROM User WHERE id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

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

            responseBody.put("code", 1000);
            responseBody.put("wishlist", wishlist);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            responseBody.put("code", 0);
            responseBody.put("message", "찜 목록 정보를 가져오는 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    // 비밀번호 인증 API
    @PostMapping("/{email}/auth")
    public ResponseEntity<Map<String, Object>> authenticateUser(@PathVariable String email, @RequestBody Map<String, String> requestBody) {
        String password = requestBody.get("password");
        boolean authenticated = userService.authenticate(email, password);
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("authenticated", authenticated);
        if (authenticated) {
            responseBody.put("code", 1000);
            responseBody.put("message", "비밀번호 인증 성공");
            return ResponseEntity.ok(responseBody);
        } else {
            responseBody.put("code", 0);
            responseBody.put("message", "비밀번호 인증 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }
    }

    @PutMapping("/{email}/edit")
    public ResponseEntity<Map<String, Object>> updateUserDetails(
            @PathVariable String email,
            @RequestPart("user") String userJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            // JSON 데이터를 User 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            User updatedUser = objectMapper.readValue(userJson, User.class);

            // 이미지 파일 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = saveImage(imageFile); // 이미지 저장 메서드 호출
                updatedUser.setImage(imagePath);
            }

            // 사용자 정보 업데이트
            User user = userService.updateUserDetails(email, updatedUser);
            if (user != null) {
                responseBody.put("code", 1000);
                responseBody.put("message", "사용자 정보 수정 성공");
                responseBody.put("user", user);
                return ResponseEntity.ok(responseBody);
            } else {
                responseBody.put("code", 0);
                responseBody.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.put("code", 0);
            responseBody.put("message", "사용자 정보를 수정하는 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    //파일 시스템에 이미지 저장
    private String saveImage(MultipartFile image) {
        try {
            String uploadDir = "src/main/resources/static/uploads/";
            String originalFileName = image.getOriginalFilename();
            String extension = ""; // 파일 확장자

            // 파일 확장자 추출
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // 고유 파일명 생성: UUID 기반
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            Path filePath = Paths.get(uploadDir + uniqueFileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + uniqueFileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 저장 실패");
        }
    }

    // 기존 사용자 정보 조회 API
    @GetMapping("/{email}/get")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String email) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            User user = userService.findUserById(email);
            if (user != null) {
                responseBody.put("code", 1000);
                responseBody.put("user", user);
                return ResponseEntity.ok(responseBody);
            } else {
                responseBody.put("code", 0);
                responseBody.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }
        } catch (Exception e) {
            responseBody.put("code", 0);
            responseBody.put("message", "사용자 정보를 가져오는 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @PatchMapping("/{user_idx}/mannerpoint/update")
    public ResponseEntity<Map<String, Object>> updateUserMannerPoint(
            @PathVariable("user_idx") int userIdx, // user_idx로 변경
            @RequestBody Map<String, Integer> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            // 사용자 매너 지수 업데이트 쿼리
            String updateQuery = "UPDATE user SET manner_point = ? WHERE user_idx = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, requestBody.get("mannerPoint"));
            preparedStatement.setInt(2, userIdx);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                responseBody.put("code", 1000);
                responseBody.put("message", "매너 지수가 성공적으로 업데이트되었습니다.");
                return ResponseEntity.ok(responseBody);
            } else {
                responseBody.put("code", 0);
                responseBody.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }
        } catch (Exception e) {
            logger.error("매너 지수 업데이트 중 오류 발생: ", e);
            responseBody.put("code", 0);
            responseBody.put("message", "매너 지수 업데이트 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }
}

