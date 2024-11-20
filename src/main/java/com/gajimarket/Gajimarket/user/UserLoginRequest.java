package com.gajimarket.Gajimarket.user;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String id;       // 입력받은 아이디
    private String passwd;   // 입력받은 비밀번호

    // Getter와 Setter 추가
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
