package com.gajimarket.Gajimarket.data;

import com.gajimarket.Gajimarket.ApiResponse;
import com.gajimarket.Gajimarket.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService){
        this.dataService=dataService;
    }

    // 카테고리별 상품 수
    @GetMapping("/product/category-count")
    public ResponseEntity<ApiResponse> getCategoryCount(){
        ApiResponse apiResponse;
        try {
            List<CategoryCount> categoryCounts = dataService.getCategoryCounts();
            apiResponse=new ApiResponse("1000",null);
            apiResponse.setData(categoryCounts);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e){
            apiResponse=new ApiResponse("0", "차트 데이터 로드 실패");
            return ResponseEntity.internalServerError().body(apiResponse);
        }
    }

    //최신 일주일 간 포인트 충전량
    @GetMapping("/point/charge-last-week")
    public ResponseEntity<ApiResponse> getLastWeekChargeData() {
        ApiResponse apiResponse;
        try {
            List<ChargeData> chargeDataList = dataService.getLastWeekChargeData();
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(chargeDataList);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "차트 데이터 로드 실패");
            return ResponseEntity.internalServerError().body(apiResponse);
        }
    }

    //주요 도시별 거래 타입 비율
    @GetMapping("/product/regional-transaction")
    public ResponseEntity<ApiResponse> getRegionalTransactionData() {
        ApiResponse apiResponse;
        try {
            List<RegionTransactionData> chargeDataList = dataService.getRegionalTransactionData();
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(chargeDataList);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "차트 데이터 로드 실패");
            return ResponseEntity.internalServerError().body(apiResponse);
        }
    }

    // 최근 한 달간 찜수가 가장 많은 상위 5개 상품 조회
    @GetMapping("/product/most-hearted")
    public ResponseEntity<ApiResponse> getTopHeartProducts() {
        ApiResponse apiResponse;
        try {
            List<ProductHeartDTO> chargeDataList = dataService.getTop5ProductsByHeart();
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(chargeDataList);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "차트 데이터 로드 실패");
            return ResponseEntity.internalServerError().body(apiResponse);
        }
    }
}
