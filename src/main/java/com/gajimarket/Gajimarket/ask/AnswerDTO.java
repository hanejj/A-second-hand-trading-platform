package com.gajimarket.Gajimarket.ask;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerDTO {
    private int answerIdx;
    private String answerTitle;
    private String answerContent;
    private LocalDateTime answerCreatedAt;
    private String AnswerPublic;
    private int answerAdminIndex;
    private String answerImage;
    private int questionIdx;
    private String questionTitle;
}