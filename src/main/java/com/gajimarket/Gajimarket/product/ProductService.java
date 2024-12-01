package com.gajimarket.Gajimarket.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class ProductService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
  
    @Autowired
    public ProductService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }



    // "/product" 요청 시 상품 목록 조회
    public List<Product> getProductList(ProductRequest productRequest) {
        String sql = "";
        Object[] params;

        // 전체 카테고리인 경우
        if ("all".equals(productRequest.getCategory())) {
            sql = "SELECT * FROM product WHERE selling = ?";
            params = new Object[]{productRequest.getSelling()};
        }
        // 특정 카테고리인 경우
        else {
            sql = "SELECT * FROM product WHERE selling = ? AND category = ?";
            params = new Object[]{productRequest.getSelling(), productRequest.getCategory()};
        }

        // 인기순/최신순 처리 로직 추가
        if ("pop".equals(productRequest.getOrder())) {
            sql += " ORDER BY heart_num DESC";  // 인기순(찜 수) 정렬
        } else if ("new".equals(productRequest.getOrder())) {
            sql += " ORDER BY created_at DESC";  // 최신순 정렬
        }

        return jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> new Product(
                        rs.getInt("product_idx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("location"),
                        rs.getInt("chat_num"),
                        rs.getInt("heart_num"),
                        rs.getString("selling"),
                        rs.getString("image"),
                        rs.getInt("writer_idx"),
                        rs.getString("writer_name"),
                        rs.getString("status"),
                        // 필요하지 않은 정보는 가져오지 않음
                        0, //rs.getInt("partner_idx"),
                        false, //rs.getBoolean("review"),
                        null, //rs.getString("content")
                        false //rs.getBoolean("is_hearted")
                )
        );
    }


    // 특정 상품 정보를 조회하는 메서드
    public ProductPageResponse getProductById(int product_idx, Integer user_idx) { // user_idx는 Integer로 변경 (null 허용)
        // 1. 상품 정보 조회
        String productSql = """
    SELECT 
        p.*, 
        CASE 
            WHEN w.wish_idx IS NOT NULL THEN TRUE 
            ELSE FALSE 
        END AS is_hearted
    FROM 
        product p
    LEFT JOIN 
        wishlist w 
    ON 
        p.product_idx = w.product_idx AND w.user_idx = ?
    WHERE 
        p.product_idx = ?
    """;

        // 찜 상태를 처리할 user_idx를 설정 (비로그인 상태면 기본값 사용)
        Object userIdxParam = (user_idx != null) ? user_idx : -1; // 로그인하지 않은 경우 -1 사용
        Product product = jdbcTemplate.queryForObject(
                productSql,
                new Object[]{userIdxParam, product_idx},
                (rs, rowNum) -> new Product(
                        rs.getInt("product_idx"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("location"),
                        rs.getInt("chat_num"),
                        rs.getInt("heart_num"),
                        rs.getString("selling"),
                        rs.getString("image"),
                        rs.getInt("writer_idx"),
                        rs.getString("writer_name"),
                        rs.getString("status"),
                        rs.getObject("partner_idx", Integer.class),
                        rs.getBoolean("review"),
                        rs.getString("content"),
                        rs.getBoolean("is_hearted") // 찜 상태
                )
        );

        // 2. 키워드 조회
        String keywordSql = "SELECT keyword FROM keyword WHERE product_idx = ?";
        List<String> keywords = jdbcTemplate.queryForList(keywordSql, new Object[]{product_idx}, String.class);

        // 3. 추천 상품 조회 (user_idx가 null이면 기본값 처리)
        List<Product> recommendedProducts = Collections.emptyList();
        if (!keywords.isEmpty()) {
            String recommendedProductsSql = """
            SELECT DISTINCT 
                p.*,
                CASE
                    WHEN EXISTS (
                        SELECT 1
                        FROM wishlist w
                        WHERE w.product_idx = p.product_idx AND w.user_idx = ?
                    ) THEN TRUE
                    ELSE FALSE
                END AS is_hearted
            FROM
                product p
            JOIN
                keyword k ON p.product_idx = k.product_idx
            WHERE
                k.keyword IN (%s)
                AND p.product_idx != ?
        """;

            String placeholders = String.join(",", keywords.stream().map(k -> "?").toArray(String[]::new));
            String finalSql = String.format(recommendedProductsSql, placeholders);

            List<Object> params = new ArrayList<>();
            params.add(userIdxParam); // user_idx 또는 -1
            params.addAll(keywords); // IN 절 파라미터
            params.add(product_idx);

            recommendedProducts = jdbcTemplate.query(
                    finalSql,
                    params.toArray(),
                    (rs, rowNum) -> new Product(
                            rs.getInt("product_idx"),
                            rs.getString("category"),
                            rs.getString("title"),
                            rs.getInt("price"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getString("location"),
                            rs.getInt("chat_num"),
                            rs.getInt("heart_num"),
                            rs.getString("selling"),
                            rs.getString("image"),
                            rs.getInt("writer_idx"),
                            rs.getString("writer_name"),
                            rs.getString("status"),
                            rs.getObject("partner_idx", Integer.class),
                            rs.getBoolean("review"),
                            rs.getString("content"),
                            rs.getBoolean("is_hearted") // 찜 상태
                    )
            );
        }

        // 응답 객체 생성
        return new ProductPageResponse(product, recommendedProducts);
    }





    //작성된 리뷰를 데이터베이스에 등록
    public void writeReview(int product_idx, ReviewRequest reviewRequest) {
        // 리뷰 삽입
        String sql = "INSERT INTO Review (product_idx, review, image, seller_index, buyer_index, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, product_idx, reviewRequest.getReview(), reviewRequest.getImage(),
                reviewRequest.getSellerIndex(), reviewRequest.getBuyerIndex(), reviewRequest.getCreatedAt());

        // Product 테이블에서 해당 product_idx에 대한 리뷰 상태를 true로 업데이트
        String updateProductSql = "UPDATE Product SET review = true WHERE product_idx = ?";
        jdbcTemplate.update(updateProductSql, product_idx);

        // 리뷰 스코어에 따라 판매자의 manner_point 업데이트
        String updateMannerPointSql = "UPDATE user " +
                "SET manner_point = manner_point + " +
                "CASE WHEN ? = 'good' THEN 5 " +
                "WHEN ? = 'bad' THEN -5 " +
                "ELSE 0 END " +
                "WHERE user_idx = ?";
        jdbcTemplate.update(updateMannerPointSql, reviewRequest.getReviewScore(), reviewRequest.getReviewScore(), reviewRequest.getSellerIndex());

    }


    //리뷰 가져오기
    public Review getReviewByProductId(int productIdx) {
        String sql = "SELECT r.review_idx, r.product_idx, r.review, r.image, r.seller_index, r.buyer_index, r.created_at " +
                "FROM Review r " +
                "WHERE r.product_idx = ?";

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{productIdx},
                    (rs, rowNum) -> new Review(
                            rs.getInt("review_idx"),
                            rs.getInt("product_idx"),
                            rs.getString("review"),
                            rs.getString("image"),
                            rs.getInt("seller_index"),
                            rs.getInt("buyer_index"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("해당 제품에 대한 리뷰가 존재하지 않습니다.");
        }
    }

    //상품 업로드
    public int uploadProduct(ProductUploadRequest productUploadRequest) {
        String sql = "INSERT INTO product (title, content, image, price, category, location, created_at, writer_idx, writer_name, selling, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                productUploadRequest.getTitle(),
                productUploadRequest.getContent(),
                productUploadRequest.getImage(),
                productUploadRequest.getPrice(),
                productUploadRequest.getCategory(),
                productUploadRequest.getLocation(),
                productUploadRequest.getCreatedAt(),
                productUploadRequest.getUser_idx(),
                productUploadRequest.getNickname(),
                productUploadRequest.getSell(),
                "active"
        );

        // 삽입된 product_idx 가져오기
        Integer productIdx = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        // 키워드가 있는 경우, 키워드 테이블 업데이트
        if (productUploadRequest.getKeyword() != null && productIdx != null) {
            for (String keyword : productUploadRequest.getKeyword()) {
                String keywordSql = "INSERT INTO keyword (product_idx, keyword) VALUES (?, ?)";
                jdbcTemplate.update(keywordSql, productIdx, keyword);
            }
        }

        return productIdx;
    }

    // 상품 찜 기능
    public void addWish(WishRequest wishRequest) {
        // Wishlist에 추가
        String sql = "INSERT INTO Wishlist (user_idx, product_idx, created_at) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, wishRequest.getUserIdx(), wishRequest.getProductIdx(), wishRequest.getCreatedAt());
    }

    // 상품 찜 해제 기능
    public void cancelWish(WishRequest wishRequest) {
        // Wishlist에서 삭제
        String sql = "DELETE FROM Wishlist WHERE user_idx = ? AND product_idx = ?";
        jdbcTemplate.update(sql, wishRequest.getUserIdx(), wishRequest.getProductIdx());
    }

    public List<Product> searchProductsByTitle(String title) {
        String sql = "SELECT * FROM Product WHERE title LIKE ?";
        List<Product> productList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + title + "%");
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setProductIdx(resultSet.getInt("product_idx"));
                product.setTitle(resultSet.getString("title"));
                product.setContent(resultSet.getString("content"));
                product.setPrice(resultSet.getInt("price"));
                product.setLocation(resultSet.getString("location"));
                product.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

                productList.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error during product search", e);
        }

        return productList;
    }

    public int getProductWriterIdx(int productIdx) {
        String sql = "SELECT writer_idx FROM product WHERE product_idx = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productIdx);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("writer_idx");
            } else {
                throw new RuntimeException("Product not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}
