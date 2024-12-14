package com.gajimarket.Gajimarket.ask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 문의글 작성
public class AskWriteRequest {
    private String title;
    private String content;
    private String image;  // 이미지 경로
    private String publicStatus;  // 공개 여부: "y" 또는 "n"
    private int user_idx;

    public AskWriteRequest(String title, String content, String image, String publicStatus, int user_idx) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.publicStatus = publicStatus;
        this.user_idx = user_idx;
    }

}
