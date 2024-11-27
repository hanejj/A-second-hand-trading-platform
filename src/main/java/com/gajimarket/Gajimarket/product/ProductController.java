package com.gajimarket.Gajimarket.product;

import com.gajimarket.Gajimarket.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public ResponseEntity<ApiResponse> getProductList(
            @RequestParam(required = false) String selling,  // 팔아요/구해요
            @RequestParam(required = false) String category, // 카테고리
            @RequestParam(required = false) String order     // 정렬기준
    ) {
        System.out.println("/product request");
        System.out.println("sell: "+selling);
        System.out.println("category: "+category);
        System.out.println("order: "+order);
        ApiResponse apiResponse;
        try {
            // Request에서 받은 파라미터를 ProductRequest 객체에 세팅
            ProductRequest productRequest = new ProductRequest(selling, category, order);
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
    // 아직 로그인이 구현되지 않았으므로 클라리언트에서 보내는 임의의 user_idx 사용, 후에 session으로 변경
    @GetMapping("/{product_idx}")
    public ResponseEntity<ApiResponse> getProductById(
            @PathVariable int product_idx,
            @RequestParam("user_idx") int user_idx) {
        System.out.println("/product/" + product_idx + " request");
        try {
            // 서비스 호출
            ProductPageResponse response = productService.getProductById(product_idx, user_idx);

            // response가 null이거나 데이터가 없는 경우
            if (response == null || response.getProduct() == null) {
                throw new NoSuchElementException("상품 정보를 찾을 수 없습니다.");
            }

            // 정상적으로 데이터를 반환하는 경우
            ApiResponse apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(response);
            return ResponseEntity.ok().body(apiResponse);

        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            // 상품이 없는 예외 발생 시
            ApiResponse apiResponse = new ApiResponse("0", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // 기타 예외 처리
            ApiResponse apiResponse = new ApiResponse("0", "상품 정보 불러오기 실패");
            return ResponseEntity.ok().body(apiResponse);
        }
    }


    // 리뷰 쓰기 기능
    @PostMapping("/{product_idx}/review/write")
    public ResponseEntity<ApiResponse> writeReview(
            @PathVariable int product_idx,
            @RequestParam("review") String review,
            @RequestParam("image") MultipartFile image,
            @RequestParam("sellerIndex") int sellerIndex,
            @RequestParam("buyerIndex") int buyerIndex,
            @RequestParam("reviewScore") String reviewScore) { // 좋음/나쁨 평가
        System.out.println("/"+product_idx+"/review/write request");
        ApiResponse apiResponse;
        try {
            // 이미지 저장
            String imagePath = saveImage(image);

            // 생성일은 서버에서 처리
            LocalDateTime createdAt = LocalDateTime.now();

            // ReviewRequest 객체 생성
            ReviewRequest reviewRequest = new ReviewRequest();
            reviewRequest.setReview(review);
            reviewRequest.setImage(imagePath);
            reviewRequest.setSellerIndex(sellerIndex);
            reviewRequest.setBuyerIndex(buyerIndex);
            reviewRequest.setReviewScore(reviewScore); // 평가 추가
            reviewRequest.setCreatedAt(createdAt);

            // 서비스 호출
            productService.writeReview(product_idx, reviewRequest);

            apiResponse = new ApiResponse("1000", "리뷰 쓰기 성공");
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            apiResponse = new ApiResponse("0", "리뷰 쓰기 실패");
            return ResponseEntity.ok().body(apiResponse);
        }
    }


    // 리뷰 읽기 기능
    @GetMapping("/{product_idx}/review/read")
    public ResponseEntity<ApiResponse> readReview(@PathVariable int product_idx) {
        System.out.println("/"+product_idx+"/review/read request");
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
    public ResponseEntity<ApiResponse> uploadProduct(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("price") int price,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam("createdAt") String  createdAt,
            @RequestParam("user_idx") int userIdx,
            @RequestParam("nickname") String nickname,
            @RequestParam("keyword") String[] keywords,
            @RequestParam("sell") String sellType,
            @RequestParam("image") MultipartFile image) {

        System.out.println("product /upload request");

        ApiResponse apiResponse;
        try {
            // 이미지 저장 처리
            String imagePath = saveImage(image);
            // createdAt을 LocalDateTime으로 변환
            // createdAt을 ZonedDateTime으로 파싱 (Z가 포함된 날짜 처리)
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime createdDateTime = zonedDateTime.toLocalDateTime(); // LocalDateTime으로 변환

            // 업로드된 상품 정보 처리
            ProductUploadRequest productUploadRequest = new ProductUploadRequest(title, content, imagePath, price, category, location, createdDateTime, userIdx, nickname, Arrays.asList(keywords), sellType);

            // 상품 등록 서비스 호출
            int productIdx = productService.uploadProduct(productUploadRequest);

            // 응답 데이터
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("product_idx", productIdx); // 등록된 상품의 인덱스
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(responseData);

            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            apiResponse = new ApiResponse("0", "상품 업로드 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    //파일 시스템에 이미지 저장
    private String saveImage(MultipartFile image) {
        try {
            String uploadDir = "src/main/resources/static/uploads/";
            String originalFileName = image.getOriginalFilename();
            String extension = ""; // 파일 확장자

            // 파일 확장자 추출
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // 고유 파일명 생성: UUID 기반
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            Path filePath = Paths.get(uploadDir + uniqueFileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + uniqueFileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 저장 실패");
        }
    }

    // 상품 찜 추가 기능
    @PostMapping("/{product_idx}/wish")
    public ResponseEntity<ApiResponse> addWish(
            @PathVariable int product_idx,
            @RequestParam("user_idx") int user_idx){
        System.out.println("/product"+product_idx+"/wish request");
        ApiResponse apiResponse;
        try {
            // 생성일은 서버에서 처리
            LocalDateTime createdAt = LocalDateTime.now();

            // WishRequest 객체 생성
            WishRequest wishRequest=new WishRequest();
            wishRequest.setUserIdx(user_idx);
            wishRequest.setProductIdx(product_idx);
            wishRequest.setCreatedAt(createdAt);

            // 서비스 호출
            productService.addWish(wishRequest);
            apiResponse = new ApiResponse("1000", "찜 추가 성공");
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            apiResponse = new ApiResponse("0", "찜 추가 실패");
            return ResponseEntity.ok().body(apiResponse);
        }

    }

    // 상품 찜 해제 기능
    @PostMapping("/{product_idx}/wish/cancel")
    public ResponseEntity<ApiResponse> cancelWish(
            @PathVariable int product_idx,
            @RequestParam("user_idx") int user_idx){
        System.out.println("/product/"+product_idx+"/wish/cancel request");
        ApiResponse apiResponse;
        try {
            LocalDateTime createdAt=null; // 찜 삭제 시간은 필요없음

            // WishRequest 객체 생성
            WishRequest wishRequest=new WishRequest();
            wishRequest.setUserIdx(user_idx);
            wishRequest.setProductIdx(product_idx);
            wishRequest.setCreatedAt(createdAt);

            // 서비스 호출
            productService.cancelWish(wishRequest);
            apiResponse = new ApiResponse("1000", "찜 취소 성공");
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            apiResponse = new ApiResponse("0", "찜 취소 실패");
            return ResponseEntity.ok().body(apiResponse);
        }

    }

    // 검색 API 추가
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String title) {
        List<Product> products = productService.searchProductsByTitle(title);
        return ResponseEntity.ok(products);
    }
}
