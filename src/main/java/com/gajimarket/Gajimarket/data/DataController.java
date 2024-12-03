package com.gajimarket.Gajimarket.data;

import com.gajimarket.Gajimarket.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


}
