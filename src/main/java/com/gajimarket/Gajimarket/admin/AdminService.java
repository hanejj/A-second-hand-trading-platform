package com.gajimarket.Gajimarket.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdminService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AdminService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    // 회원 목록 가져오기
    public List<UserInfo> getUserList() {
        // SQL 쿼리 작성
        String sql = "SELECT user_idx, id, nickname, manner_point FROM user";

        // 데이터베이스에서 결과를 조회하고 DTO(UserInfo)로 매핑
        List<UserInfo> userList = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new UserInfo(
                        rs.getInt("user_idx"),
                        rs.getString("id"),
                        rs.getString("nickname"),
                        rs.getInt("manner_point")
                )
        );

        return userList;
    }

    // 회원 영구정지=매너 지수 -1로 설정
    public void banUser(int userIdx) {
        // SQL 쿼리 작성
        String sql = "UPDATE user SET manner_point = -1 WHERE user_idx = ?";

        // 업데이트 실행
        int rowsAffected = jdbcTemplate.update(sql, userIdx);

        if (rowsAffected == 0) {
            throw new RuntimeException("회원 정지 실패: 존재하지 않는 user_idx " + userIdx);
        }
    }

    // 관리자 목록 가져오기
    public List<UserInfo> getAdminList() {
        String sql = "SELECT admin_index AS idx, id, name AS name FROM admin";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new UserInfo(
                        rs.getInt("idx"),
                        rs.getString("id"),
                        rs.getString("name"),
                        0
                )
        );
    }

    // 관리자 추가하기
    public void addAdmin(AddAdminRequest request) throws Exception {
        // 1. 중복 검사
        String checkDuplicateSql = "SELECT COUNT(*) FROM admin WHERE id = ?";
        int count = jdbcTemplate.queryForObject(checkDuplicateSql, new Object[]{request.getId()}, Integer.class);

        if (count > 0) {
            throw new Exception("이미 존재하는 아이디입니다."); // 중복 아이디 처리
        }

        // 2. 관리자 추가
        String sql = "INSERT INTO admin (id, passwd, name) VALUES (?, ?, ?)";
        int result = jdbcTemplate.update(sql, request.getId(), request.getPasswd(), request.getName());

        if (result != 1) {
            throw new Exception("관리자 추가에 실패했습니다.");
        }
    }

    public void deleteAdmin(int admin_idx) {
        // 1. 인덱스가 1인지 확인
        if (admin_idx == 1) {
            throw new IllegalArgumentException("인덱스가 1인 관리자는 삭제할 수 없습니다.");
        }

        // 2. 해당 관리자가 존재하는지 확인
        String checkQuery = "SELECT COUNT(*) FROM admin WHERE admin_index = ?";
        int count = jdbcTemplate.queryForObject(checkQuery, new Object[]{admin_idx}, Integer.class);
        if (count == 0) {
            throw new NoSuchElementException("존재하지 않는 관리자 인덱스입니다.");
        }

        // 3. 관리자 삭제
        String deleteQuery = "DELETE FROM admin WHERE admin_index = ?";
        jdbcTemplate.update(deleteQuery, admin_idx);
    }

    // 신고 목록 가져오기
    public List<ReportInfo> getReportList() {
        // SQL 쿼리 작성: Report 테이블에서 필요한 정보를 조회
        String sql = "SELECT report_idx, title, content, reporting_idx, status, reported_user, reported_product, created_at FROM report";

        // 데이터베이스에서 결과를 조회하고 DTO(ReportInfo)로 매핑
        List<ReportInfo> reportList = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new ReportInfo(
                        rs.getInt("report_idx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("reporting_idx"),
                        rs.getString("status"),
                        rs.getInt("reported_user"),
                        rs.getInt("reported_product"),
                        rs.getTimestamp("created_at")
                )
        );

        return reportList;
    }

}
