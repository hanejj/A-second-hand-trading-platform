package com.gajimarket.Gajimarket.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
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
            foundUser.setUserIdx(Integer.valueOf(resultSet.getString("user_idx")));
            foundUser.setId(resultSet.getString("id"));
            foundUser.setName(resultSet.getString("name"));
            foundUser.setPhone(resultSet.getString("phone"));
            foundUser.setNickname(resultSet.getString("nickname"));
            foundUser.setLocation(resultSet.getString("location"));
            foundUser.setPasswd(resultSet.getString("passwd"));
            foundUser.setImage(resultSet.getString("image"));
            foundUser.setMessage(resultSet.getString("message"));
            foundUser.setBirth(resultSet.getString("birth"));
            foundUser.setSex(resultSet.getString("sex"));
            foundUser.setMannerPoint(Integer.valueOf(resultSet.getString("manner_point")));

            return foundUser;
        } catch (SQLException e) {
            logger.error("SQL Error during user lookup: " + e.getMessage());
            throw new RuntimeException("Database error during user lookup", e);
        }
    }

    public void signUp(User user) {
        String userSql = "INSERT INTO user (id, passwd, name, birth, sex, phone, nickname, location, image, message, manner_point) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String pointSql = "INSERT INTO Point (user_idx, point) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false); // 트랜잭션 시작

            try (PreparedStatement userStmt = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pointStmt = connection.prepareStatement(pointSql)) {

                // User 테이블에 데이터 삽입
                userStmt.setString(1, user.getId());        // 아이디
                userStmt.setString(2, user.getPasswd());    // 비밀번호
                userStmt.setString(3, user.getName());      // 이름
                userStmt.setDate(4, Date.valueOf(user.getBirth()));  // 생년월일
                userStmt.setString(5, user.getSex());       // 성별
                userStmt.setString(6, user.getPhone());     // 연락처
                userStmt.setString(7, user.getNickname());  // 닉네임
                userStmt.setString(8, user.getLocation());  // 거주지
                userStmt.setString(9, user.getImage() != null ? user.getImage() : "/uploads/user.png"); // 기본 이미지
                userStmt.setString(10, user.getMessage());  // 소개글 (NULL 가능)
                userStmt.setInt(11, user.getMannerPoint() != null ? user.getMannerPoint() : 50);  // 매너 점수 기본값

                // 실행 및 user_idx 가져오기
                int rowsAffected = userStmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userIdx = generatedKeys.getInt(1); // 생성된 user_idx

                            // Point 테이블에 초기 포인트 삽입
                            pointStmt.setInt(1, userIdx);
                            pointStmt.setInt(2, 0); // 초기 포인트 0
                            pointStmt.executeUpdate();

                            connection.commit(); // 트랜잭션 커밋
                            logger.info("User sign up and point initialization successful");
                        } else {
                            connection.rollback(); // 트랜잭션 롤백
                            logger.error("Failed to retrieve generated user_idx");
                            throw new RuntimeException("Failed to retrieve user_idx");
                        }
                    }
                } else {
                    connection.rollback(); // 트랜잭션 롤백
                    logger.error("Failed to sign up user");
                    throw new RuntimeException("Failed to sign up user");
                }
            } catch (SQLException e) {
                connection.rollback(); // 트랜잭션 롤백
                logger.error("SQL Error during sign up: " + e.getMessage());
                throw new RuntimeException("Database error during sign up", e);
            }
        } catch (SQLException e) {
            logger.error("Database connection error: " + e.getMessage());
            throw new RuntimeException("Database connection error", e);
        }
    }

    public User updateUserDetails(String email, User updatedUser) {
        String sql = "UPDATE user SET name = ?, phone = ?, location = ?, passwd = ?, image = ?, message = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // 기존 사용자 정보를 가져옵니다.
            User existingUser = findUserById(email);
            if (existingUser == null) {
                logger.warn("No user found for email: {}", email);
                return null;
            }

            // 비밀번호가 없으면 기존 비밀번호 유지
            String updatedPassword = updatedUser.getPasswd() != null && !updatedUser.getPasswd().isEmpty()
                    ? updatedUser.getPasswd() : existingUser.getPasswd();

            // PreparedStatement에 값 설정
            stmt.setString(1, updatedUser.getName() != null ? updatedUser.getName() : existingUser.getName());  // 이름
            stmt.setString(2, updatedUser.getPhone() != null ? updatedUser.getPhone() : existingUser.getPhone());  // 연락처
            stmt.setString(3, updatedUser.getLocation() != null ? updatedUser.getLocation() : existingUser.getLocation());  // 거주지
            stmt.setString(4, updatedPassword);  // 비밀번호 (수정되지 않았으면 기존 비밀번호 유지)
            stmt.setString(5, updatedUser.getImage() != null ? updatedUser.getImage() : existingUser.getImage());  // 이미지 (NULL 가능)
            stmt.setString(6, updatedUser.getMessage() != null ? updatedUser.getMessage() : existingUser.getMessage());  // 소개글 (NULL 가능)
            stmt.setString(7, email);  // 이메일 (ID)

            // 쿼리 실행
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User details update successful for email: {}", email);
                return findUserById(email);  // 업데이트 후 최신 정보를 가져와서 반환
            } else {
                logger.warn("No user found to update for email: {}", email);
                return null;
            }
        } catch (SQLException e) {
            logger.error("SQL Error during user update: " + e.getMessage());
            throw new RuntimeException("Database error during user update", e);
        }
    }

    public User findUserByIdx(int userIdx) {
        logger.info("사용자 정보 조회: user_idx = {}", userIdx);

        String sql = "SELECT * FROM user WHERE user_idx = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userIdx);
            var resultSet = stmt.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            User foundUser = new User();
            foundUser.setUserIdx(resultSet.getInt("user_idx"));
            foundUser.setId(resultSet.getString("id"));
            foundUser.setName(resultSet.getString("name"));
            foundUser.setPhone(resultSet.getString("phone"));
            foundUser.setNickname(resultSet.getString("nickname"));
            foundUser.setLocation(resultSet.getString("location"));
            foundUser.setPasswd(resultSet.getString("passwd"));
            foundUser.setImage(resultSet.getString("image"));
            foundUser.setMessage(resultSet.getString("message"));
            foundUser.setBirth(resultSet.getString("birth"));
            foundUser.setSex(resultSet.getString("sex"));
            foundUser.setMannerPoint(resultSet.getInt("manner_point"));

            return foundUser;
        } catch (SQLException e) {
            logger.error("SQL Error during user lookup by user_idx: " + e.getMessage());
            throw new RuntimeException("Database error during user lookup by user_idx", e);
        }
    }
}
