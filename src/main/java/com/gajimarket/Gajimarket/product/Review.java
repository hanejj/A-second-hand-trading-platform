package com.gajimarket.Gajimarket.product;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 데이터베이스에서 가져오는 리뷰 정보
@Getter
@Setter
public class Review {
    private int reviewIdx; // 리뷰 ID
    private int productIdx; // 상품 ID
    private String review; // 리뷰 내용
    private String image; // 리뷰 이미지
    private int sellerIdx; // 판매자 ID
    private int buyerIdx; // 구매자 ID
    private LocalDateTime createdAt; // 리뷰 작성일

    public Review(int reviewIdx, int productIdx, String review, String image, int sellerIndex, int buyerIndex, LocalDateTime createdAt) {
        this.reviewIdx=reviewIdx;
        this.productIdx=productIdx;
        this.review=review;
        this.image=image;
        this.sellerIdx=sellerIndex;
        this.buyerIdx=buyerIndex;
        this.createdAt=createdAt;
    }
}
