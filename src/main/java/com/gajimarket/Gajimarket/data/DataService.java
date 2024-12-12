package com.gajimarket.Gajimarket.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataService {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public DataService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    //카테고리별 상품 수
    public List<CategoryCount> getCategoryCounts(){
        String sql = "SELECT category, COUNT(*) AS product_count FROM product GROUP BY category";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new CategoryCount(
                        rs.getString("category"),
                        rs.getInt("product_count")
                )
        );
    }

    // 최신 일주일 간 포인트 충전량 데이터 가져오기
    public List<ChargeData> getLastWeekChargeData() {
        String sql = "SELECT DATE(created_at) AS date, SUM(change_amount) AS total_charge " +
                "FROM Point_history " +
                "WHERE transaction_type = 'charge' " +
                "AND created_at >= CURDATE() - INTERVAL 6 DAY " +
                "GROUP BY DATE(created_at) " +
                "ORDER BY DATE(created_at) DESC";

        // SQL 실행 결과를 ChargeData 객체로 변환하여 반환
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new ChargeData(
                        rs.getDate("date"),
                        rs.getInt("total_charge")
                )
        );
    }

    // 주요 도시에서의 판매/구매 거래 비율을 조회하는 메서드
    public List<RegionTransactionData> getRegionalTransactionData() {
        String sql = """
            WITH RegionalData AS (
                SELECT 
                    CASE 
                        WHEN location LIKE '%서울%' THEN '서울'
                        WHEN location LIKE '%경기%' THEN '경기도'
                        WHEN location LIKE '%부산%' THEN '부산'
                        WHEN location LIKE '%인천%' THEN '인천'
                        WHEN location LIKE '%광주%' THEN '광주'
                        ELSE '기타'
                    END AS region,
                    selling
                FROM 
                    product
            )
            SELECT 
                region,
                selling,
                COUNT(*) AS transaction_count
            FROM 
                RegionalData
            GROUP BY 
                region, selling
            ORDER BY 
                region, selling;
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new RegionTransactionData(
                        rs.getString("region"),
                        rs.getString("selling"),
                        rs.getInt("transaction_count")
                )
        );
    }

    // 최근 한 달간 찜 수가 많은 상품 TOP 5
    // BeanPropertyRowMapper 대신 Custom RowMapper를 사용하는 방법
    public List<ProductHeartDTO> getTop5ProductsByHeart() {
        String sql = "SELECT title, category, heart_num, created_at " +
                "FROM product " +
                "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH) " +
                "ORDER BY heart_num DESC " +
                "LIMIT 5";

        // RowMapper를 사용해 LocalDateTime 변환을 처리
        List<ProductHeartDTO> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new ProductHeartDTO(
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("heart_num"),
                        rs.getTimestamp("created_at").toLocalDateTime() // LocalDateTime으로 변환
                )
        );

        return result;
    }

    // 사용자별 평균 상품 판매 개수와 비교하여 필터링 및 평균 값 포함
    public List<Map<String, Object>> getAboveAverageProductUsersWithAverage() {
        String sql = """
    WITH ProductCounts AS (
        SELECT writer_idx, COUNT(*) AS product_count
        FROM Product
        GROUP BY writer_idx
    ),
    AverageCount AS (
        SELECT AVG(product_count) AS average_count
        FROM ProductCounts
    )
    SELECT u.nickname, pc.product_count, ac.average_count
    FROM User u
    JOIN ProductCounts pc ON u.user_idx = pc.writer_idx
    CROSS JOIN AverageCount ac
    HAVING pc.product_count > ac.average_count;
    """;

        try {
            return jdbcTemplate.query(
                    sql,
                    (rs, rowNum) -> Map.of(
                            "nickname", rs.getString("nickname"),
                            "transactionCount", rs.getInt("product_count"),
                            "average", rs.getDouble("average_count") 
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("쿼리 실행 중 오류 발생", e);
        }
    }

}
