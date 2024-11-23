package com.gajimarket.Gajimarket.product;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductPageResponse {
    private Product product; // 상세 정보
    private List<Product> recommendedProducts; // 추천 상품 리스트

    public ProductPageResponse(Product product, List<Product> recommendedProducts) {
        this.product = product;
        this.recommendedProducts = recommendedProducts;
    }
}
