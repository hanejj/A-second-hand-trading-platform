package com.gajimarket.Gajimarket.product;

import java.time.LocalDateTime;

public class Product {
    private int productIdx;
    private String title;
    private String content;
    private int price;
    private String location;
    private LocalDateTime createdAt;

    // Getters and Setters
    public int getProductIdx() {
        return productIdx;
    }

    public void setProductIdx(int productIdx) {
        this.productIdx = productIdx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

