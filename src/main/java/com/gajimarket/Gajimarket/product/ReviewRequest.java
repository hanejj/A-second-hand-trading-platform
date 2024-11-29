package com.gajimarket.Gajimarket.product;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 클라이언트에서 작성한 데이터베이스에 업로드해야하는 리뷰
@Getter
@Setter
public class ReviewRequest {
    private String review;           // 리뷰 내용
    private String image;            // 이미지 URL
    private int sellerIndex;         // 판매자 user_idx
    private int buyerIndex;          // 구매자 user_idx
    private String reviewScore;      // 거래 평가 (좋음/나쁨)
    private LocalDateTime createdAt; // 생성일 (서버에서 자동 처리)
}
