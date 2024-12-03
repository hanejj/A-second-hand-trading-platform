package com.gajimarket.Gajimarket.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// 카테고리별 상품 수 카운트
@Getter
@Setter
public class CategoryCount {
    private String category;
    private int productCount;

    public CategoryCount(String category, int productCount) {
        this.category=category;
        this.productCount=productCount;
    }
}
