package com.gajimarket.Gajimarket.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 검색 API 추가
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String title) {
        List<Product> products = productService.searchProductsByTitle(title);
        return ResponseEntity.ok(products);
    }
}
