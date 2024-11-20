package com.gajimarket.Gajimarket.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;  // Optional import
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);  // 반환 타입을 Optional<User>로 수정

    boolean existsById(String id); // 중복 아이디 확인
    boolean existsByNickname(String nickname); // 중복 닉네임 확인
}
