package com.gajimarket.Gajimarket.product;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 사용자가 특정 상품을 찜한 경우 데이터베이스에 올라갈 정보
@Getter
@Setter
public class WishRequest {
    private int userIdx;
    private LocalDateTime createdAt;
    private int productIdx;
}
