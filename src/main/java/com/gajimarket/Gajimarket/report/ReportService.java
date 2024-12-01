package com.gajimarket.Gajimarket.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 특정 신고 상세 조회
    public ReportDetail getReportDetail(int reportIdx) throws Exception {
        // SQL 쿼리 작성
        // Report 테이블에서 신고 정보 가져오기, User 테이블에서 유저 정보 가져오기, Prodct 테이블에서 신고된 상품 정보 가져오기
        String sql = """
            SELECT 
                r.report_idx,
                r.title,
                r.content,
                r.status,
                r.created_at,
                r.reporting_idx AS reporting_user_idx,
                reporting_user.id AS reporting_user_id,
                reporting_user.nickname AS reporting_user_nickname,
                r.reported_user AS reported_user_idx,
                reported_user.id AS reported_user_id,
                reported_user.nickname AS reported_user_nickname,
                r.reported_product AS reported_product_idx,
                p.title AS reported_product_title,
                p.content AS reported_product_content
            FROM 
                Report r
            LEFT JOIN User reporting_user ON r.reporting_idx = reporting_user.user_idx
            LEFT JOIN User reported_user ON r.reported_user = reported_user.user_idx
            LEFT JOIN Product p ON r.reported_product = p.product_idx
            WHERE 
                r.report_idx = ?;
        """;

        // 데이터베이스에서 조회
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{reportIdx},
                    (rs, rowNum) -> new ReportDetail(
                            rs.getInt("report_idx"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            // 신고한 유저 정보
                            rs.getInt("reporting_user_idx"),
                            rs.getString("reporting_user_id"),
                            rs.getString("reporting_user_nickname"),
                            // 신고 당한 유저 정보
                            rs.getInt("reported_user_idx"),
                            rs.getString("reported_user_id"),
                            rs.getString("reported_user_nickname"),
                            // 신고당한 상품 정보
                            rs.getInt("reported_product_idx"),
                            rs.getString("reported_product_title"),
                            rs.getString("reported_product_content")
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            throw new Exception("신고 내역을 찾을 수 없습니다.");
        }
    }

    // 상품 신고 메서드
    public void reportProduct(int productIdx, String title, String content, int reportingUserIdx) throws Exception {
        // 1. 상품이 존재하는지 확인, 존재하면 글쓴이 유저의 인덱스 가져오기
        String productCheckSql = "SELECT writer_idx FROM Product WHERE product_idx = ?";
        Integer reportedUserIdx;
        try {
            reportedUserIdx = jdbcTemplate.queryForObject(productCheckSql, new Object[]{productIdx}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            throw new Exception("신고하려는 상품이 존재하지 않습니다.");
        }

        // 2. 신고 데이터 삽입
        String insertReportSql = """
            INSERT INTO Report (title, content, reporting_idx, status, reported_user, reported_product, created_at) 
            VALUES (?, ?, ?, 'pending', ?, ?, NOW())
        """;
        int result = jdbcTemplate.update(insertReportSql, title, content, reportingUserIdx, reportedUserIdx, productIdx);

        if (result != 1) {
            throw new Exception("상품 신고를 저장하는 데 실패했습니다.");
        }
    }

    // 사용자 신고
    public void reportUser(int reportedUserIdx, String title, String content, int reportingUserIdx) throws Exception {
        // 1. 신고 대상 사용자 확인
        String checkUserSql = "SELECT COUNT(*) FROM user WHERE user_idx = ?";
        int userExists = jdbcTemplate.queryForObject(checkUserSql, Integer.class, reportedUserIdx);

        if (userExists == 0) {
            throw new Exception("신고 대상 사용자가 존재하지 않습니다.");
        }

        // 2. 신고 내역 추가
        String insertReportSql = "INSERT INTO Report (title, content, reporting_idx, status, reported_user, created_at) " +
                "VALUES (?, ?, ?, 'pending', ?, NOW())";
        int result = jdbcTemplate.update(insertReportSql, title, content, reportingUserIdx, reportedUserIdx);

        if (result != 1) {
            throw new Exception("사용자 신고에 실패했습니다.");
        }
    }


    // 신고 거부
    public void rejectReport(int reportIdx) throws Exception {
        // 1. 신고가 존재하는지 확인
        String checkReportSql = "SELECT status FROM Report WHERE report_idx = ?";
        List<String> reportStatusList = jdbcTemplate.query(
                checkReportSql,
                (rs, rowNum) -> rs.getString("status"),
                reportIdx
        );

        if (reportStatusList.isEmpty()) {
            throw new Exception("해당 신고가 존재하지 않습니다.");
        }

        // 2. 현재 상태 유효성 검증
        String currentStatus = reportStatusList.get(0);
        if (!"pending".equals(currentStatus)) {
            throw new Exception("이미 처리된 신고입니다. 상태: " + currentStatus);
        }

        // 3. 신고 상태를 'rejected'로 변경
        String updateStatusSql = "UPDATE Report SET status = 'rejected' WHERE report_idx = ?";
        int result = jdbcTemplate.update(updateStatusSql, reportIdx);

        if (result != 1) {
            throw new Exception("신고 거부 처리에 실패했습니다.");
        }
    }


}
