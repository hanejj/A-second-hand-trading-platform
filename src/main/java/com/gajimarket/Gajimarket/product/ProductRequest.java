package com.gajimarket.Gajimarket.product;

import lombok.Getter;
import lombok.Setter;

// "/product" api 요청 시 조회 조건
@Getter
@Setter
public class ProductRequest {
    private String selling;  //팔아요/구해요
    private String category; //카테고리
    private String order; //정렬기준

    public ProductRequest(String selling, String category, String order) {
        this.selling = selling;
        this.category = category;
        this.order = order;
    }
}
