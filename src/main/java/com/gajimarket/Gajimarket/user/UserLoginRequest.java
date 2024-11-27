package com.gajimarket.Gajimarket.user;

public class UserLoginRequest {
    private String id;       // 입력받은 아이디
    private String passwd;   // 입력받은 비밀번호

    // Constructor
    public UserLoginRequest(String id, String passwd) {
        this.id = id;
        this.passwd = passwd;
    }

    // Getter와 Setter
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
