package com.gajimarket.Gajimarket.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ReportDetail {
    private int reportIdx;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private int reportingUserIdx; // 신고한 유저 인덱스
    private String reportingUserId;
    private String reportingUserNickname;
    private int reportedUserIdx; // 신고당한 유저 인덱스
    private String reportedUserId;
    private String reportedUserNickname;
    private Integer reportedProductIdx; // 신고당한 상품 인덱스 (nullable)
    private String reportedProductTitle;
    private String reportedProductContent;

    // 생성자
    public ReportDetail(int reportIdx, String title, String content, String status, LocalDateTime createdAt,
                        int reportingUserIdx, String reportingUserId, String reportingUserNickname,
                        int reportedUserIdx, String reportedUserId, String reportedUserNickname,
                        Integer reportedProductIdx, String reportedProductTitle, String reportedProductContent) {
        this.reportIdx = reportIdx;
        this.title = title;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.reportingUserIdx = reportingUserIdx;
        this.reportingUserId = reportingUserId;
        this.reportingUserNickname = reportingUserNickname;
        this.reportedUserIdx = reportedUserIdx;
        this.reportedUserId = reportedUserId;
        this.reportedUserNickname = reportedUserNickname;
        this.reportedProductIdx = reportedProductIdx;
        this.reportedProductTitle = reportedProductTitle;
        this.reportedProductContent = reportedProductContent;
    }
}
