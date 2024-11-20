package com.gajimarket.Gajimarket.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String id, String passwd) {
        logger.info("인증 시도: 아이디 = {}", id); // 디버깅 로그 추가

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.warn("사용자 없음: 아이디 = {}", id); // 디버깅 로그 추가
            return false;
        }

        User user = optionalUser.get();

        // 비밀번호 비교
        boolean isMatch = passwd.equals(user.getPasswd());
        logger.info("비밀번호 비교 결과: {}", isMatch ? "일치" : "불일치");

        return isMatch;
    }

    public User findUserById(String id) {
        logger.info("사용자 정보 조회: 아이디 = {}", id); // 디버깅 로그 추가
        return userRepository.findById(id).orElse(null);
    }

    public void signUp(User user) {
        try {
            // 중복 검사 및 기타 로직
            if (userRepository.existsById(user.getId())) {
                throw new RuntimeException("이미 존재하는 아이디입니다.");
            }
            if (userRepository.existsByNickname(user.getNickname())) {
                throw new RuntimeException("이미 존재하는 닉네임입니다.");
            }
            userRepository.save(user);
        } catch (Exception e) {
            // 로그 추가
            System.err.println("회원가입 처리 중 에러 발생: " + e.getMessage());
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.");
        }
    }

    public void updateProfile(String user_idx, String message, String imagePath) {
        User user = userRepository.findById(user_idx).orElseThrow(() -> new RuntimeException("User not found"));
        user.setMessage(message);
        user.setImage(imagePath);
        userRepository.save(user);
    }
}
