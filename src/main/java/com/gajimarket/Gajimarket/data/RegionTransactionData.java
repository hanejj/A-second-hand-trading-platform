package com.gajimarket.Gajimarket.data;

import lombok.Getter;
import lombok.Setter;

//주요 도시에서의 거래 비율 결과 데이터를 담을 DTO
@Getter
@Setter
public class RegionTransactionData {
    private String region;           // 지역
    private String selling;          // 거래 타입 ('sell' 또는 'get')
    private int transactionCount;    // 거래 횟수

    // Constructor
    public RegionTransactionData(String region, String selling, int transactionCount) {
        this.region = region;
        this.selling = selling;
        this.transactionCount = transactionCount;
    }
}
