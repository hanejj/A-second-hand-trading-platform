package com.gajimarket.Gajimarket.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductService(JdbcTemplate jdbcTemplate) {
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
                        rs.getInt("seller_idx"),
                        rs.getString("seller_name"),
                        rs.getString("status"),
                        rs.getInt("buyer_idx"),
                        rs.getBoolean("review")
                )
        );
    }


    // 특정 상품 정보를 조회하는 메서드
    public Product getProductById(int product_idx) {
        String sql = "SELECT * FROM product WHERE product_idx = ?";

        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{product_idx},
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
                        rs.getInt("seller_idx"),
                        rs.getString("seller_name"),
                        rs.getString("status"),
                        rs.getInt("buyer_idx"),
                        rs.getBoolean("review")
                )
        );
    }

    //작성된 리뷰를 데이터베이스에 등록
    public void writeReview(int product_idx, ReviewRequest reviewRequest){
        String sql = "INSERT INTO Review (product_idx, review, image, seller_index, buyer_index, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, product_idx, reviewRequest.getReview(), reviewRequest.getImage(),
                reviewRequest.getSellerIndex(), reviewRequest.getBuyerIndex(), new Date());
        // Product 테이블에서 해당 product_idx에 대한 리뷰 컬럼을 true로 업데이트
        String updateProductSql = "UPDATE Product SET review = true WHERE product_idx = ?";
        jdbcTemplate.update(updateProductSql, product_idx);
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
        String sql = "INSERT INTO product (title, content, image, price, category, location, created_at, seller_idx, seller_name, selling, status) " +
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
}
