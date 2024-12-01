package com.gajimarket.Gajimarket.ask;

import com.gajimarket.Gajimarket.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ask")
public class AskController {

    @Autowired
    private AskService askService;

    // 전체 문의 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getAskList(
            @RequestParam(required = false, defaultValue = "false") boolean isAdmin,
            @RequestParam(required = false, defaultValue = "-1") int userIdx) {

        ApiResponse apiResponse;
        try {
            List<QuestionWithAnswerDTO> questionList = askService.getQuestionsWithAnswers(isAdmin, userIdx);
            apiResponse = new ApiResponse("1000", questionList);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "문의 목록 조회 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    // 문의글 조회
    @GetMapping("/question/{question_idx}")
    public ResponseEntity<ApiResponse> getQuestionDetails(@PathVariable int question_idx) {
        ApiResponse apiResponse;
        try {
            // 서비스에서 질문과 답글을 함께 가져옴
            QuestionWithAnswerDTO questionWithAnswerDTO = askService.getQuestionWithAnswer(question_idx);

            apiResponse = new ApiResponse("1000", questionWithAnswerDTO);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "문의 상세 조회 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
    
    // 답글 조회
    @GetMapping("/answer/{answer_idx}")
    public ResponseEntity<ApiResponse> getAnswerDetail(@PathVariable int answer_idx) {
        ApiResponse apiResponse;
        try {
            // 서비스에서 질문과 답글을 함께 가져옴
            AnswerDTO answerDTO = askService.getAnswerDetail(answer_idx);

            apiResponse = new ApiResponse("1000", answerDTO);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse("0", "답글 상세 조회 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    // 문의글 작성 API
    @PostMapping("/write")
    public ResponseEntity<ApiResponse> writeAsk(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("publicStatus") String publicStatus,
            @RequestParam("user_idx") int userIdx,
            @RequestParam boolean isAdmin) {

        ApiResponse apiResponse;
        try {
            // isAdmin이 true인 경우 관리자이므로 문의글을 작성할 수 없음
            if (isAdmin) {
                apiResponse = new ApiResponse("500", "관리자는 문의할 수 없음");
                return ResponseEntity.ok().body(apiResponse);
            }

            String imagepath="";
            // 이미지 경로가 없을 경우 기본값 처리
            if (image != null) {
                // 이미지 저장 처리
                imagepath = saveImage(image);
            }

            // AskWriteRequest 객체 생성
            AskWriteRequest askWriteRequest = new AskWriteRequest(title, content, imagepath, publicStatus, userIdx);

            // 문의글 작성 서비스 호출
            int questionIdx = askService.writeAsk(askWriteRequest);

            // 응답 데이터에 question_idx 추가
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("question_idx", questionIdx); // 생성된 문의글 인덱스

            apiResponse = new ApiResponse("1000", "문의글 작성 성공");
            apiResponse.setData(responseData);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            apiResponse = new ApiResponse("0", "문의글 작성 실패");
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


    // 문의글에 대해 관리자가 답글 작성
    @PostMapping("/{question_idx}/answer")
    public ResponseEntity<ApiResponse> writeAnswer(
            @PathVariable("question_idx") int questionIdx,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("publicStatus") String publicStatus,
            @RequestParam("admin_idx") int adminIdx,
            @RequestParam boolean isAdmin) {

        ApiResponse apiResponse;

        try {
            // isAdmin이 false인 경우 권한 없음 응답
            if (!isAdmin) {
                apiResponse = new ApiResponse("500", "관리자는 아니므로 답변을 달 수 없습니다.");
                return ResponseEntity.ok().body(apiResponse);
            }

            String imagePath = "";
            // 이미지 경로 처리
            if (image != null) {
                imagePath = saveImage(image);
            }

            // DTO 객체 생성
            AskWriteRequest answerRequest = new AskWriteRequest(title, content, imagePath, publicStatus, adminIdx);

            // 답글 작성 서비스 호출
            int answerIdx = askService.writeAnswer(questionIdx, answerRequest);

            apiResponse = new ApiResponse("1000", answerIdx);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            apiResponse = new ApiResponse("0", "답글 작성 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }


}
