package com.gajimarket.Gajimarket.admin;

import com.gajimarket.Gajimarket.ApiResponse;
import com.gajimarket.Gajimarket.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AdminController(AdminService adminService, JwtTokenProvider jwtTokenProvider) {
        this.adminService = adminService;
        this.jwtTokenProvider=jwtTokenProvider;
    }
    
    // 로그인한 관리자 유저의 정보 가져오기
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getAdminOrUserProfile(HttpServletRequest request, @RequestParam boolean isAdmin) {
        String token = request.getHeader("Authorization");
        ApiResponse apiResponse;

        // 1. 토큰 검증
        if (token == null || !token.startsWith("Bearer ")) {
            apiResponse = new ApiResponse("0", "토큰이 없습니다.");
            return ResponseEntity.ok().body(apiResponse);
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            apiResponse = new ApiResponse("0", "유효하지 않은 토큰입니다.");
            return ResponseEntity.ok().body(apiResponse);
        }

        // 2. 사용자/관리자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(token);

        try {
            if (isAdmin) {
                // 관리자 정보 조회
                Admin admin = adminService.findAdminById(userId);
                if (admin == null) {
                    apiResponse = new ApiResponse("100", "관리자를 찾을 수 없습니다.");
                    return ResponseEntity.ok().body(apiResponse);
                }
                apiResponse = new ApiResponse("1000", admin);
                return ResponseEntity.ok().body(apiResponse);
            } else {
                apiResponse = new ApiResponse("500", "관리자가 아닙니다.");
                return ResponseEntity.ok().body(apiResponse);
            }
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);
        }
    }


    // 전체 사용자 정보 가져오기
    @GetMapping("/get/userList")
    public ResponseEntity<ApiResponse> getUserList(@RequestParam boolean isAdmin){
        System.out.println("/get/userList request");

        ApiResponse apiResponse;

        try {
            // isAdmin이 false인 경우 관리자 권한이 없으므로 예외 처리
            if (!isAdmin) {
                apiResponse = new ApiResponse("500", "관리자 권한이 없음");
                return ResponseEntity.ok().body(apiResponse);
            }

            // 서비스 호출
            List<UserInfo> userList = adminService.getUserList();
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(userList);
            return ResponseEntity.ok().body(apiResponse);

        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "리스트 불러오기 실패");
            return ResponseEntity.ok().body(apiResponse);
        }
    }


    // 회원 영구 정지(매너 지수 -1로 설정)
    @PatchMapping("/ban/{userIdx}")
    public ResponseEntity<ApiResponse> banUser(
            @RequestParam boolean isAdmin,
            @PathVariable("userIdx") int userIdx) {
        System.out.println("/admin/ban/" + userIdx + " request");
        ApiResponse apiResponse;

        try {
            // isAdmin이 false인 경우 관리자 권한이 없으므로 예외 처리
            if (!isAdmin) {
                apiResponse = new ApiResponse("500", "관리자 권한이 없음");
                return ResponseEntity.ok().body(apiResponse);
            }

            adminService.banUser(userIdx);
            apiResponse = new ApiResponse("1000", "회원 영구 정지 성공");
            return ResponseEntity.ok().body(apiResponse);

        } catch (IllegalStateException e) {
            // 이미 영구 정지된 회원에 대한 예외 처리
            apiResponse = new ApiResponse("300", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);

        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "회원 영구 정지 실패");
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    // 관리자 목록 가져오기
    @GetMapping("/get/adminList")
    public ResponseEntity<ApiResponse> getAdminList(@RequestParam boolean isAdmin) {
        System.out.println("/admin/get/adminList request");

        ApiResponse apiResponse;
        try {
            // isAdmin이 false인 경우 관리자 권한이 없으므로 예외 처리
            if (!isAdmin) {
                apiResponse = new ApiResponse("500", "관리자 권한이 없음");
                return ResponseEntity.ok().body(apiResponse);
            }
            // 서비스 호출
            List<UserInfo> adminList = adminService.getAdminList();
            apiResponse = new ApiResponse("1000", null);
            apiResponse.setData(adminList);
            return ResponseEntity.ok().body(apiResponse);

        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "관리자 리스트 불러오기 실패");
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addAdmin(
            @RequestParam boolean isAdmin,
            @RequestBody AddAdminRequest request) {
        System.out.println("/admin/add request");

        ApiResponse apiResponse;
        try {
            // isAdmin이 false인 경우 관리자 권한이 없으므로 예외 처리
            if (!isAdmin) {
                apiResponse = new ApiResponse("500", "관리자 권한이 없음");
                return ResponseEntity.ok().body(apiResponse);
            }
            // 서비스 호출
            adminService.addAdmin(request);
            apiResponse = new ApiResponse("1000", "관리자 추가 성공");
            return ResponseEntity.ok().body(apiResponse);

        } catch (Exception e) {
            // 예외 처리
            if (e.getMessage().equals("이미 존재하는 아이디입니다.")) {
                apiResponse = new ApiResponse("500", e.getMessage());
            } else {
                apiResponse = new ApiResponse("0", "관리자 추가 중 알 수 없는 오류가 발생했습니다.");
            }
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    @DeleteMapping("/delete/{admin_idx}")
    public ResponseEntity<ApiResponse> deleteAdmin(
            @RequestParam boolean isAdmin,
            @PathVariable int admin_idx) {
        System.out.println("/admin/delete/" + admin_idx + " request");
        ApiResponse apiResponse;

        try {
            // isAdmin이 false인 경우 관리자 권한이 없으므로 예외 처리
            if (!isAdmin) {
                apiResponse = new ApiResponse("500", "관리자 권한이 없음");
                return ResponseEntity.ok().body(apiResponse);
            }
            // 서비스 호출
            adminService.deleteAdmin(admin_idx);
            apiResponse = new ApiResponse("1000", "관리자 삭제 성공");
            return ResponseEntity.ok().body(apiResponse);

        } catch (IllegalArgumentException e) {
            // 인덱스가 1인 관리자를 삭제하려는 경우
            apiResponse = new ApiResponse("500", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);

        } catch (NoSuchElementException e) {
            // 없는 인덱스를 요청한 경우
            apiResponse = new ApiResponse("300", e.getMessage());
            return ResponseEntity.ok().body(apiResponse);

        } catch (Exception e) {
            // 기타 예외 처리
            apiResponse = new ApiResponse("0", "관리자 삭제 중 알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.ok().body(apiResponse);
        }
    }


    // 신고 목록 가져오기
    @GetMapping("/get/reportList")
    public ResponseEntity<ApiResponse> getReportList(@RequestParam boolean isAdmin){
        System.out.println("/get/reportList request");
        ApiResponse apiResponse;
        try{
            // isAdmin이 false인 경우 관리자 권한이 없으므로 예외 처리
            if (!isAdmin) {
                apiResponse = new ApiResponse("500", "관리자 권한이 없음");
                return ResponseEntity.ok().body(apiResponse);
            }
            //서비스 호출
            List<ReportInfo> reportList = adminService.getReportList();
            apiResponse=new ApiResponse("1000", null);
            apiResponse.setData(reportList);
            return ResponseEntity.ok().body(apiResponse);

        } catch (Exception e){
            apiResponse = new ApiResponse("0", "리스트 불러오기 실패");
            return ResponseEntity.ok().body(apiResponse);
        }
    }
}
