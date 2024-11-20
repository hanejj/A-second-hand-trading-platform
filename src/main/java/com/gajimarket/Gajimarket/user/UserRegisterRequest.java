package com.gajimarket.Gajimarket.user;

public class UserRegisterRequest {

    private String Id;
    private String passwd;
    private String name;
    private String email;
    private String phone;
    private String nickname;
    private String region;

    // Constructor
    public UserRegisterRequest(String Id, String passwd, String name, String email, String phone, String nickname, String region) {
        this.Id = Id;
        this.passwd = passwd;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.region = region;
    }

    // Getters and setters
    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getpasswd() {
        return passwd;
    }

    public void setpasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
