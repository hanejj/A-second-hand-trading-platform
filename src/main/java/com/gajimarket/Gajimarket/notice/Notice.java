package com.gajimarket.Gajimarket.notice;

import java.time.LocalDateTime;

public class Notice {
    private int noticeId; // 공지사항 고유 ID
    private int adminId; // 관리자 ID
    private String noticeTitle; // 공지사항 제목
    private String noticeContent; // 공지사항 내용
    private LocalDateTime noticeCreatedAt; // 작성 일자
    private String noticeImage; // 이미지 경로
    private String adminName; // 작성자 이름

    // Getters and Setters
    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public LocalDateTime getNoticeCreatedAt() {
        return noticeCreatedAt;
    }

    public void setNoticeCreatedAt(LocalDateTime noticeCreatedAt) {
        this.noticeCreatedAt = noticeCreatedAt;
    }

    public String getNoticeImage() {
        return noticeImage;
    }

    public void setNoticeImage(String noticeImage) {
        this.noticeImage = noticeImage;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
