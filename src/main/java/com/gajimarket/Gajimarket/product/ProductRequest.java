package com.gajimarket.Gajimarket.product;

import lombok.Getter;
import lombok.Setter;

// "/product" api 요청 시 오는 request body
@Getter
@Setter
public class ProductRequest {
    private String selling;  //팔아요/구해요
    private String category; //카테고리
    private String order; //정렬기준
}
