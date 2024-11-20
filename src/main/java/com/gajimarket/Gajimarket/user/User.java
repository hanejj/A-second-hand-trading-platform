package com.gajimarket.Gajimarket.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Data;
import jakarta.persistence.Column;


@Entity
@Getter  // 클래스에 @Getter 어노테이션을 적용하여 모든 필드에 대해 getter 메소드 자동 생성
@Data
public class User {
    public String getPasswd() {
        return passwd;
    }
    // getId() 메서드 정의
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // getNickname() 메서드 정의
    public String getNickname() {
        return nickname;
    }

    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private int user_idx; // user_idx에 매핑

    @Id
    @Column(nullable = false, unique = true, length = 50)
    private String id; // id에 매핑

    @Column(nullable = false, length = 50)
    private String passwd; // passwd에 매핑

    @Column(nullable = false, length = 50)
    private String name; // name에 매핑

    @Column(nullable = false)
    private String birth; // birth에 매핑

    @Column(nullable = false, length = 1)
    private String sex; // sex에 매핑 (ENUM('M', 'F'))

    @Column(nullable = false, length = 50)
    private String phone; // phone에 매핑

    @Column(nullable = false, unique = true, length = 50)
    private String nickname; // nickname에 매핑

    @Column(nullable = false, length = 50)
    private String location; // location에 매핑

    @Column(length = 255)
    private String image; // image에 매핑 (NULL 가능)

    @Column(length = 50)
    private String message; // message에 매핑 (NULL 가능)

    private Integer mannerPoint; // manner_point에 매핑 (NULL 가능)

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
