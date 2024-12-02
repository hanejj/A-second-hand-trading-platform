package com.gajimarket.Gajimarket.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@RestController
@CrossOrigin
public class ImageController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private DataSource dataSource;

    //프론트에서 이미지 파일을 요청하는 경로
    @GetMapping("/image")
    public ResponseEntity<?> returnImage(@RequestParam String image){
        String path="src/main/resources/static";
        System.out.println("/image request: "+path+image);
        Resource resource=new FileSystemResource(path+image);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    // 이미지 업로드 경로
    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("id") String email) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어있습니다.");
        }

        try {
            // 업로드 디렉토리 생성
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 고유 파일명 생성
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 저장된 파일 경로
            String imagePath = "/uploads/" + fileName;

            // 사용자 테이블 업데이트
            updateUserImage(email, imagePath);

            // 성공 응답
            return ResponseEntity.ok().body(new UploadResponse(1000, "이미지 업로드 성공", imagePath));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패");
        }
    }

    private void updateUserImage(String email, String imagePath) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "UPDATE user SET image = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, imagePath);
            preparedStatement.setString(2, email);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("사용자 이미지 경로 업데이트 성공: " + imagePath);
            } else {
                System.out.println("사용자 이미지 경로 업데이트 실패: 사용자 없음");
            }
        }
    }

    private static class UploadResponse {
        private int code;
        private String message;
        private String imagePath;

        public UploadResponse(int code, String message, String imagePath) {
            this.code = code;
            this.message = message;
            this.imagePath = imagePath;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    @DeleteMapping("/remove/image")
    public ResponseEntity<Map<String, Object>> removeImage(@RequestParam("image") String image, @RequestParam("id") String id) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            // 파일 삭제
            Path filePath = Paths.get("src/main/resources/static" + image);
            Files.deleteIfExists(filePath);

            // 기본 이미지 경로 설정
            String defaultImagePath = "/uploads/user.png";

            // 데이터베이스 업데이트
            updateUserImage(id, defaultImagePath);

            // 성공 응답
            responseBody.put("code", 1000);
            responseBody.put("message", "이미지 삭제 후 기본 이미지로 설정되었습니다.");
            responseBody.put("defaultImage", defaultImagePath);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            // 실패 응답
            e.printStackTrace();
            responseBody.put("code", 0);
            responseBody.put("message", "이미지 삭제 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }
}
