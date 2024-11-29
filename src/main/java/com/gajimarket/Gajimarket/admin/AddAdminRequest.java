package com.gajimarket.Gajimarket.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAdminRequest {
    private String id;       // 관리자 아이디
    private String passwd;   // 관리자 비밀번호
    private String name;     // 관리자 이름

    public AddAdminRequest(String id, String passwd, String name) {
        this.id = id;
        this.passwd = passwd;
        this.name = name;
    }
}
