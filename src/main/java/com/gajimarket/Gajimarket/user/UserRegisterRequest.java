package com.gajimarket.Gajimarket.user;

public class UserRegisterRequest {

    private String id;
    private String passwd;
    private String name;
    private String birth;
    private String sex;
    private String phone;
    private String nickname;
    private String location;
    private String image;
    private String message;

    // Constructor
    public UserRegisterRequest(String id, String passwd, String name, String birth, String sex, String phone,
                               String nickname, String location, String image, String message) {
        this.id = id;
        this.passwd = passwd;
        this.name = name;
        this.birth = birth;
        this.sex = sex;
        this.phone = phone;
        this.nickname = nickname;
        this.location = location;
        this.image = image;
        this.message = message;
    }

    // 기본 생성자
    public UserRegisterRequest() {}

    // Getters and Setters
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
