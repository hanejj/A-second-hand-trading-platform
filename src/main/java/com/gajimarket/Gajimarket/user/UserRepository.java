package com.gajimarket.Gajimarket.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 사용자 조회 (Optional로 반환)
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper(), id);
        return users.stream().findFirst(); // Optional 반환
    }

    // 아이디 중복 확인
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM user WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // 닉네임 중복 확인
    public boolean existsByNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM user WHERE nickname = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nickname);
        return count != null && count > 0;
    }

    // 사용자 저장 (회원가입)
    public void save(User user) {
        String sql = "INSERT INTO user (id, passwd, nickname, name) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getId(), user.getPasswd(), user.getNickname(), user.getName());
    }

    // 사용자 삭제 (예시)
    public void deleteById(String id) {
        String sql = "DELETE FROM user WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // RowMapper 구현 (User 객체 생성)
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setPasswd(rs.getString("passwd"));
            user.setNickname(rs.getString("nickname"));
            user.setName(rs.getString("name"));
            return user;
        };
    }

    //닉네임 수정 시, 상품 테이블에도 업데이트
    public void updateNicknameInProductTable(String oldNickname, String newNickname) {
        String sql = "UPDATE product SET writer_name = ? WHERE writer_name = ?";
        jdbcTemplate.update(sql, newNickname, oldNickname);
    }
}
