package com.gajimarket.Gajimarket.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeDAO noticeDAO;

    //모든 공지사항 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotices() {
        List<Notice> notices = noticeDAO.getAllNotices();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1000);
        response.put("data", notices);
        return ResponseEntity.ok(response);
    }

    //특정 공지사항 상세 조회
    @GetMapping("/{notice_idx}")
    public ResponseEntity<Map<String, Object>> getNoticeById(@PathVariable("notice_idx") int noticeIdx) {
        Notice notice = noticeDAO.getNoticeById(noticeIdx);
        Map<String, Object> response = new HashMap<>();
        if (notice != null) {
            response.put("code", 1000);
            response.put("data", notice);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 0);
            response.put("message", "공지사항을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //특정 공지사항 삭제
    @DeleteMapping("/delete/{notice_idx}")
    public ResponseEntity<Map<String, Object>> deleteNotice(@PathVariable("notice_idx") int noticeIdx) {
        boolean isDeleted = noticeDAO.deleteNoticeById(noticeIdx);
        Map<String, Object> response = new HashMap<>();
        if (isDeleted) {
            response.put("code", 1000);
            response.put("message", "공지사항이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 0);
            response.put("message", "공지사항 삭제 실패.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //공지사항 작성
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("adminId") int adminId, // 클라이언트에서 전달받은 관리자 ID
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            String imagePath = null;
            if (image != null && !image.isEmpty()) {
                imagePath = saveImage(image); // 이미지 저장
            }

            Notice notice = new Notice();
            notice.setNoticeTitle(title);
            notice.setNoticeContent(content);
            notice.setAdminId(adminId); // 전달받은 adminId 설정
            notice.setNoticeImage(imagePath);

            noticeDAO.createNotice(notice);
            response.put("code", 1000);
            response.put("message", "공지사항이 작성되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "공지사항 작성 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //공지사항 수정
    @PutMapping("/edit/{notice_idx}")
    public ResponseEntity<Map<String, Object>> updateNotice(
            @PathVariable("notice_idx") int noticeIdx,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        Map<String, Object> response = new HashMap<>();
        try {
            String imagePath = null;
            if (image != null && !image.isEmpty()) {
                imagePath = saveImage(image);
            }

            Notice notice = new Notice();
            notice.setNoticeId(noticeIdx);
            notice.setNoticeTitle(title);
            notice.setNoticeContent(content);
            notice.setNoticeImage(imagePath);

            boolean isUpdated = noticeDAO.updateNotice(notice);
            if (isUpdated) {
                response.put("code", 1000);
                response.put("message", "공지사항이 수정되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 0);
                response.put("message", "공지사항 수정 실패.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "공지사항 수정 중 오류 발생.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //이미지 저장 로직
    private String saveImage(MultipartFile imageFile) throws IOException {
        String uploadDir = "uploads/"; // 실제 경로로 변경 필요
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, imageFile.getBytes());
        return fileName;
    }
}
