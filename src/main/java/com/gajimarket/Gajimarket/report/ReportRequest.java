package com.gajimarket.Gajimarket.report;

import lombok.Getter;
import lombok.Setter;

// 신고 dto
@Getter
@Setter
public class ReportRequest {
    private String title;
    private String content;

}
