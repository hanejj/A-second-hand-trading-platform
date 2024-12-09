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
    public List<Product> getProductList(ProductRequest productRequest, boolean isAdmin) {
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

        // isAdmin=false인 경우 status 조건 추가
        if (isAdmin==false) {
            sql += " AND status != 'removed'";
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
    public ProductPageResponse getProductById(int product_idx, Integer user_idx, boolean isAdmin) {
        // user_idx가 null일 경우 -1로 처리 (관리자인 경우 다른 로직 적용)
        Object userIdxParam = (user_idx != null) ? user_idx : -1;

        // 1. 상품 정보 조회
        String productSql = """
SELECT 
    p.*, 
    CASE 
        WHEN ? IS NOT NULL AND w.wish_idx IS NOT NULL THEN TRUE 
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

        Product product = jdbcTemplate.queryForObject(
                productSql,
                new Object[]{
                        isAdmin ? null : userIdxParam, // 관리자일 경우 null로 처리
                        isAdmin ? null : userIdxParam, // 관리자일 경우 null로 처리
                        product_idx
                },
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
                        isAdmin ? false : rs.getBoolean("is_hearted") // 관리자라면 false 반환
                )
        );

        // isAdmin=false이고 상품이 삭제 상태라면 예외 발생
        if (!isAdmin && "removed".equals(product.getStatus())) {
            throw new NoSuchElementException("삭제된 상품은 조회할 수 없습니다.");
        }

        // 2. 키워드 조회
        String keywordSql = "SELECT keyword FROM keyword WHERE product_idx = ?";
        List<String> keywords = jdbcTemplate.queryForList(keywordSql, new Object[]{product_idx}, String.class);

        // 3. 추천 상품 조회
        List<Product> recommendedProducts = Collections.emptyList();
        if (!keywords.isEmpty()) {
            String recommendedProductsSql = """
SELECT DISTINCT 
    sub.*,
    sub.is_hearted
FROM (
    SELECT 
        p.*,
        CASE 
            WHEN ? IS NOT NULL AND w.wish_idx IS NOT NULL THEN TRUE 
            ELSE FALSE 
        END AS is_hearted
    FROM 
        product p
    LEFT JOIN 
        wishlist w 
    ON 
        p.product_idx = w.product_idx AND w.user_idx = ?
    WHERE 
        p.status = 'active'
) AS sub
JOIN 
    keyword k 
ON 
    sub.product_idx = k.product_idx
WHERE 
    k.keyword IN (%s)
    AND sub.product_idx != ?
""";

            String placeholders = String.join(",", keywords.stream().map(k -> "?").toArray(String[]::new));
            String finalSql = String.format(recommendedProductsSql, placeholders);

            List<Object> params = new ArrayList<>();
            params.add(isAdmin ? null : userIdxParam); // 관리자는 찜 확인 생략
            params.add(isAdmin ? null : userIdxParam); // 관리자는 찜 확인 생략
            params.addAll(keywords);
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
                            isAdmin ? false : rs.getBoolean("is_hearted") // 관리자라면 false 반환
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

        // Product 테이블의 heart_num 증가
        String updateProductSql = "UPDATE Product SET heart_num = heart_num + 1 WHERE product_idx = ?";
        jdbcTemplate.update(updateProductSql, wishRequest.getProductIdx());
    }

    // 상품 찜 해제 기능
    public void cancelWish(WishRequest wishRequest) {
        // Wishlist에서 삭제
        String sql = "DELETE FROM Wishlist WHERE user_idx = ? AND product_idx = ?";
        jdbcTemplate.update(sql, wishRequest.getUserIdx(), wishRequest.getProductIdx());

        // Product 테이블의 heart_num 감소
        String updateProductSql = "UPDATE Product SET heart_num = heart_num - 1 WHERE product_idx = ?";
        jdbcTemplate.update(updateProductSql, wishRequest.getProductIdx());
    }

    public List<Product> searchProductsByTitle(String title) {
        // SQL 쿼리: Product 테이블과 Keyword 테이블 조인
        String sql = """
        SELECT DISTINCT p.* 
        FROM Product p
        LEFT JOIN Keyword k ON p.product_idx = k.product_idx
        WHERE p.title LIKE ? OR k.keyword LIKE ?
    """;

        List<Product> productList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            String searchTerm = "%" + title + "%";
            stmt.setString(1, searchTerm); // Product.title 검색
            stmt.setString(2, searchTerm); // Keyword.keyword 검색

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setProductIdx(resultSet.getInt("product_idx"));
                product.setCategory(resultSet.getString("category"));
                product.setTitle(resultSet.getString("title"));
                product.setPrice(resultSet.getInt("price"));
                product.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                product.setLocation(resultSet.getString("location"));
                product.setChatNum(resultSet.getInt("chat_num"));
                product.setHeartNum(resultSet.getInt("heart_num"));
                product.setSelling(resultSet.getString("selling"));
                product.setImage(resultSet.getString("image"));
                product.setWriterIdx(resultSet.getInt("writer_idx"));
                product.setWriterName(resultSet.getString("writer_name"));
                product.setStatus(resultSet.getString("status"));

                productList.add(product);
            }

        } catch (SQLException e) {
            // 상세한 예외 로그 추가
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
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

    //상품 삭제
    public boolean deleteProduct(Long productIdx) {
        // SQL 쿼리로 상품 상태를 removed로 업데이트
        String sql = "UPDATE product SET status = ? WHERE product_idx = ?";

        int rowsUpdated = jdbcTemplate.update(sql, "removed", productIdx);

        // 업데이트된 행의 수가 1 이상이면 성공, 아니면 실패
        return rowsUpdated > 0;
    }

    //상품 거래 완료 처리
    public boolean completeProduct(Long productIdx, Long buyerIdx) {
        // SQL 쿼리로 상품 상태를 completed로 업데이트하고 partner_idx를 설정
        String sql = "UPDATE product SET status = ?, partner_idx = ? WHERE product_idx = ?";

        int rowsUpdated = jdbcTemplate.update(sql, "completed", buyerIdx, productIdx);

        // 업데이트된 행의 수가 1 이상이면 성공, 아니면 실패
        return rowsUpdated > 0;
    }

    //구매 내역 조회
    public List<Product> getPurchasedProductsByUserIdx(int partnerIdx) {
        String sql = "SELECT * FROM Product WHERE partner_idx = ?";
        List<Product> productList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, partnerIdx);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setProductIdx(resultSet.getInt("product_idx"));
                product.setCategory(resultSet.getString("category"));
                product.setTitle(resultSet.getString("title"));
                product.setPrice(resultSet.getInt("price"));
                product.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                product.setLocation(resultSet.getString("location"));
                product.setChatNum(resultSet.getInt("chat_num"));
                product.setHeartNum(resultSet.getInt("heart_num"));
                product.setSelling(resultSet.getString("selling"));
                product.setImage(resultSet.getString("image"));
                product.setWriterIdx(resultSet.getInt("writer_idx"));
                product.setWriterName(resultSet.getString("writer_name"));
                product.setStatus(resultSet.getString("status"));

                productList.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException("구매 내역 조회 중 오류 발생", e);
        }

        return productList;
    }

    //상품 수정
    public int editProduct(ProductUploadRequest productUploadRequest, int productIdx) {
        // product 테이블 업데이트
        String updateProductSql = "UPDATE product " +
                "SET title = ?, content = ?, image = ?, price = ?, category = ?, location = ?, created_at = ?, writer_idx = ?, writer_name = ?, selling = ?, status = ? " +
                "WHERE product_idx = ?";

        int rowsAffected = jdbcTemplate.update(updateProductSql,
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
                "active",
                productIdx
        );

        if (rowsAffected > 0) {
            // 키워드 테이블 업데이트 처리
            // 기존 키워드 삭제
            String deleteKeywordSql = "DELETE FROM keyword WHERE product_idx = ?";
            jdbcTemplate.update(deleteKeywordSql, productIdx);

            // 새로운 키워드 삽입
            if (productUploadRequest.getKeyword() != null) {
                String insertKeywordSql = "INSERT INTO keyword (product_idx, keyword) VALUES (?, ?)";
                for (String keyword : productUploadRequest.getKeyword()) {
                    jdbcTemplate.update(insertKeywordSql, productIdx, keyword);
                }
            }
        }

        return rowsAffected;
    }
}
