package com.gajimarket.Gajimarket.user;

public class User {

    private String id; // id 필드
    private String passwd; // passwd 필드
    private String name; // name 필드
    private String birth; // birth 필드
    private String sex; // sex 필드 (ENUM('M', 'F'))
    private String phone; // phone 필드
    private String nickname; // nickname 필드
    private String location; // location 필드
    private String image; // image 필드 (NULL 가능)
    private String message; // message 필드 (NULL 가능)
    private Integer mannerPoint; // manner_point 필드 (NULL 가능)

    // 생성자
    public User(String id, String passwd, String name, String birth, String sex, String phone,
                String nickname, String location, String image, String message, Integer mannerPoint) {
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
        this.mannerPoint = mannerPoint;
    }

    // 기본 생성자
    public User() {}

    // Getter와 Setter 메소드
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

    public Integer getMannerPoint() {
        return mannerPoint;
    }

    public void setMannerPoint(Integer mannerPoint) {
        this.mannerPoint = mannerPoint;
    }
}
