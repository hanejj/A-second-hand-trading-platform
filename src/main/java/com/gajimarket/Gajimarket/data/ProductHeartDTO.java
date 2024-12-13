package com.gajimarket.Gajimarket.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//최근 한 달간 찜수를 많이 받은 상위 5개 상품을 결과로 내보내기 위한 dto
@Getter
@Setter
public class ProductHeartDTO {
    private String title;
    private String category;
    private int heartNum;
    private LocalDateTime createdAt;

    // Getter, Setter, Constructor
    public ProductHeartDTO(String title, String category, int heartNum, LocalDateTime createdAt) {
        this.title = title;
        this.category = category;
        this.heartNum = heartNum;
        this.createdAt = createdAt;
    }
}
