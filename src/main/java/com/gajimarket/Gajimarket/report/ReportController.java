package com.gajimarket.Gajimarket.report;

import com.gajimarket.Gajimarket.ApiResponse;
import com.gajimarket.Gajimarket.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    //신고 상세 조회
    @GetMapping("/{report_idx}")
    public ResponseEntity<ApiResponse> getReportDetail(@PathVariable("report_idx") int reportIdx) {
        ApiResponse apiResponse;
        try {
            ReportDetail reportDetail = reportService.getReportDetail(reportIdx);
            apiResponse=new ApiResponse<>("1000",reportDetail);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", e.getMessage());
            return  ResponseEntity.ok().body(apiResponse);
        }
    }

    // 상품 신고
    @PostMapping("/product/{product_idx}")
    public ResponseEntity<ApiResponse> reportProduct(
            @PathVariable("product_idx") int productIdx,
            @RequestBody ReportRequest reportRequest) {
        int reportingUserIdx=1; //임의로 신고자 인덱스 1로 설정, 로그인 api와 연결 후 수정해야함
        ApiResponse apiResponse;
        try {
            reportService.reportProduct(productIdx, reportRequest.getTitle(), reportRequest.getContent(), reportingUserIdx);
            apiResponse=new ApiResponse<>("1000", "상품 신고 성공");
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", e.getMessage());
            return  ResponseEntity.ok().body(apiResponse);
        }
    }

    // 사용자 신고
    @PostMapping("/user/{user_idx}")
    public ResponseEntity<ApiResponse> reportUser(
            @PathVariable("user_idx") int userIdx, // 신고당한 사용자
            @RequestBody ReportRequest reportRequest) {
        int reportingUserIdx = 1; // 임의로 신고자 인덱스를 1로 설정, 로그인 API 연결 후 수정 필요
        ApiResponse apiResponse;
        try {
            // 신고 서비스 호출
            reportService.reportUser(userIdx, reportRequest.getTitle(), reportRequest.getContent(), reportingUserIdx);
            apiResponse = new ApiResponse<>("1000", "사용자 신고 성공");
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse<>("0", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    // 신고 거부 처리
    @PatchMapping("/{report_idx}/reject")
    public ResponseEntity<ApiResponse> rejectReport(@PathVariable("report_idx") int reportIdx) {
        ApiResponse apiResponse;
        try {
            // 서비스 호출
            reportService.rejectReport(reportIdx);
            apiResponse = new ApiResponse<>("1000", "신고 거부 성공");
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse<>("0", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    // 신고 완료 처리
    @PatchMapping("/{report_idx}/complete")
    public ResponseEntity<ApiResponse> completeReport(@PathVariable("report_idx") int reportIdx) {
        ApiResponse apiResponse;
        try {
            // 서비스 호출
            reportService.rejectReport(reportIdx);
            apiResponse = new ApiResponse<>("1000", "신고 처리 성공");
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse<>("0", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);
        }
    }




}
