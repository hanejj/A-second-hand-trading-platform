package com.gajimarket.Gajimarket.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

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

}
