package com.gajimarket.Gajimarket.admin;

import lombok.Getter;
import lombok.Setter;

// 전체 회원 정보 조회 시 사용
@Getter
@Setter
public class UserInfo {
    private int idx;
    private String id;
    private String name;
    private int manner_point;

    public UserInfo(int idx, String id, String name, int manner_point){
        this.idx=idx;
        this.id=id;
        this.name=name;
        this.manner_point=manner_point;
    }
}
