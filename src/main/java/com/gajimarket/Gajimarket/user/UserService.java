package com.gajimarket.Gajimarket.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(DataSource dataSource, BCryptPasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean authenticate(String id, String passwd) {
        logger.info("인증 시도: 아이디 = {}", id);

        String sql = "SELECT passwd FROM user WHERE id = ?";
        Map<String, Object> user = jdbcTemplate.queryForMap(sql, id);

        if (user == null || user.isEmpty()) {
            logger.warn("사용자 없음: 아이디 = {}", id);
            return false;
        }

        String storedPassword = (String) user.get("passwd");
        return storedPassword.equals(passwd);
    }

    public User findUserById(String id) {
        logger.info("사용자 정보 조회: 아이디 = {}", id);

        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            var resultSet = stmt.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            User foundUser = new User();
            foundUser.setId(resultSet.getString("id"));
            foundUser.setName(resultSet.getString("name"));
            foundUser.setPhone(resultSet.getString("phone"));
            foundUser.setNickname(resultSet.getString("nickname"));
            foundUser.setLocation(resultSet.getString("location"));

            return foundUser;
        } catch (SQLException e) {
            logger.error("SQL Error during user lookup: " + e.getMessage());
            throw new RuntimeException("Database error during user lookup", e);
        }
    }

    public void signUp(User user) {
        String sql = "INSERT INTO user (id, passwd, name, birth, sex, phone, nickname, location, image, message, manner_point) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // PreparedStatement에 값 설정
            stmt.setString(1, user.getId());        // 아이디
            stmt.setString(2, user.getPasswd());    // 비밀번호
            stmt.setString(3, user.getName());      // 이름
            stmt.setDate(4, Date.valueOf(user.getBirth()));  // 생년월일 (String -> Date 변환)
            stmt.setString(5, user.getSex());       // 성별
            stmt.setString(6, user.getPhone());     // 연락처
            stmt.setString(7, user.getNickname());  // 닉네임
            stmt.setString(8, user.getLocation());  // 거주지
            stmt.setString(9, user.getImage());     // 이미지 (NULL 가능)
            stmt.setString(10, user.getMessage());  // 소개글 (NULL 가능)
            stmt.setInt(11, user.getMannerPoint() != null ? user.getMannerPoint() : 50);  // 매너 점수, 기본값 50

            // 쿼리 실행
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User sign up successful");
            } else {
                logger.error("Failed to sign up user");
            }
        } catch (SQLException e) {
            logger.error("SQL Error during sign up: " + e.getMessage());
            throw new RuntimeException("Database error during sign up", e);
        }
    }
}
