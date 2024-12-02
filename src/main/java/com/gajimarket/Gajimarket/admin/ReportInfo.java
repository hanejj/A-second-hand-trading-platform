package com.gajimarket.Gajimarket.admin;

import java.sql.Timestamp;

public class ReportInfo {
    private int reportIdx;         // 신고 고유 ID
    private String title;          // 신고 제목
    private String content;        // 신고 내용
    private int reportingIdx;      // 신고한 유저 ID
    private String status;         // 신고 상태 (pending, resolved, rejected)
    private int reportedUser;      // 신고 당한 유저 ID
    private Integer reportedProduct; // 신고 당한 상품 ID (nullable)
    private Timestamp createdAt;   // 신고 일자

    // 생성자
    public ReportInfo(int reportIdx, String title, String content, int reportingIdx, String status, int reportedUser, Integer reportedProduct, Timestamp createdAt) {
        this.reportIdx = reportIdx;
        this.title = title;
        this.content = content;
        this.reportingIdx = reportingIdx;
        this.status = status;
        this.reportedUser = reportedUser;
        this.reportedProduct = reportedProduct;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getReportIdx() {
        return reportIdx;
    }

    public void setReportIdx(int reportIdx) {
        this.reportIdx = reportIdx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReportingIdx() {
        return reportingIdx;
    }

    public void setReportingIdx(int reportingIdx) {
        this.reportingIdx = reportingIdx;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(int reportedUser) {
        this.reportedUser = reportedUser;
    }

    public Integer getReportedProduct() {
        return reportedProduct;
    }

    public void setReportedProduct(Integer reportedProduct) {
        this.reportedProduct = reportedProduct;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
