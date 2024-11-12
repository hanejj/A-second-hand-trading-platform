package com.gajimarket.Gajimarket.product;

import com.gajimarket.Gajimarket.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 조건별 상품 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getProductList(@RequestBody ProductRequest productRequest) {
        ApiResponse apiResponse;
        try {
            List<Product> product_list = productService.getProductList(productRequest);
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(product_list);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            // 예외가 발생한 경우 코드 0으로 설정
            apiResponse = new ApiResponse("0", "리스트 불러오기 실패");
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    // 특정 상품 정보를 요청받아 반환
    @GetMapping("/{product_idx}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable int product_idx) {
        ApiResponse apiResponse;
        try {
            Product product = productService.getProductById(product_idx);
            //response 생성 후 반환
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(product);
            return ResponseEntity.ok()
                    .body(apiResponse);
        }catch (Exception e){
            // 예외가 발생한 경우 코드 0으로 설정
            apiResponse = new ApiResponse("0", "상품 정보 불러오기 실패");
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    // 리뷰 쓰기 기능
    @PostMapping("/{product_idx}/review/write")
    public ResponseEntity<ApiResponse> writeReview(@PathVariable int product_idx, @RequestBody ReviewRequest reviewRequest) {
        ApiResponse apiResponse;
        try{
            productService.writeReview(product_idx, reviewRequest);
            apiResponse=new ApiResponse("1000", "리뷰 쓰기 성공");
            return ResponseEntity.ok()
                    .body(apiResponse);
        }
        catch (Exception e){
            apiResponse = new ApiResponse("0", "리뷰 쓰기 실패");
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    // 리뷰 읽기 기능
    @GetMapping("/{product_idx}/review/read")
    public ResponseEntity<ApiResponse> readReview(@PathVariable int product_idx) {
        ApiResponse apiResponse;
        try {
            Review review = productService.getReviewByProductId(product_idx);
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(review);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    // 상품 게시글 업로드
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadProduct(@RequestBody ProductUploadRequest productUploadRequest) {
        ApiResponse apiResponse;
        try {
            int productIdx=productService.uploadProduct(productUploadRequest);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("product_idx", productIdx); //등록된 상품의 인덱스
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(responseData);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            apiResponse = new ApiResponse("0", "상품 업로드 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
