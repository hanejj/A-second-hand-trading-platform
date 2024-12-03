package com.gajimarket.Gajimarket.data;

import lombok.Getter;
import lombok.Setter;
import java.util.*;
//최신 일주일 포인트 충전량
@Getter
@Setter
public class ChargeData {
    private Date date;
    private int totalCharge;

    // 생성자
    public ChargeData(Date date, int totalCharge) {
        this.date = date;
        this.totalCharge = totalCharge;
    }
}
