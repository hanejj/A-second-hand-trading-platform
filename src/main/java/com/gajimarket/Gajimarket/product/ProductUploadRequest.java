package com.gajimarket.Gajimarket.product;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

// "/product/upload"로 상품 게시글 등록할 때 쓰는 모델
// 클라이언트에서 가져온 정보
@Getter
@Setter
public class ProductUploadRequest {
    private String title; // 글 제목
    private String content; // 글 내용
    private String image; // 이미지 경로
    private int price; // 가격
    private String category; //카테고리
    private String location; //위치
    private LocalDateTime createdAt; //작성시간
    private int user_idx; //작성자의 인덱스
    private String nickname; //작성자의 닉네임
    private List<String> keyword; // 키워드 리스트
    private String sell; //팔아요/구해요


    public <T> ProductUploadRequest(String title, String content, String imagePath, int price, String category, String location, LocalDateTime createdAt, int userIdx, String nickname, List<String> list, String sellType) {
        this.title=title;
        this.content=content;
        this.image=imagePath;
        this.price=price;
        this.category=category;
        this.location=location;
        this.createdAt=createdAt;
        this.user_idx=userIdx;
        this.nickname=nickname;
        this.keyword=list;
        this.sell=sellType;
    }
}
