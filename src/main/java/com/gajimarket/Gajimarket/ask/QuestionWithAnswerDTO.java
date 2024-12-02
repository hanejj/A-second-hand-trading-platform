package com.gajimarket.Gajimarket.ask;

import lombok.Data;

import java.time.LocalDateTime;

// 게시판 글 전체 가져올 때
@Data
public class QuestionWithAnswerDTO {
    private int questionIdx;
    private String questionTitle;
    private String questionContent;
    private LocalDateTime questionCreatedAt;
    private int questionUserIdx;
    private boolean questionPublic;
    private String questionImage;
    private String nickname;
    private AnswerDTO answer; // 답글
}