package com.gajimarket.Gajimarket.chat;

import com.gajimarket.Gajimarket.user.User;
import com.gajimarket.Gajimarket.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat") // "/api"를 제거
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;

    public ChatController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    // 서버 코드: 사용자 이메일을 기반으로 `userId` 가져오기
    @PostMapping("/send")
    public List<Chat> sendMessage(@RequestBody Chat chat) {
        try {
            System.out.println("Received chat data: " + chat);

            // 이메일로 사용자 ID를 찾는 코드 추가
            User sender = userService.findUserByIdx(chat.getSenderId());
            if (sender == null) {
                throw new IllegalArgumentException("User not found for senderId: " + chat.getSenderId());
            }

            // 메시지 저장
            chatService.saveChat(chat);

            // 관련된 모든 채팅 메시지 반환
            return chatService.getChatsByProduct(chat.getRelatedProductId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing chat message", e);
        }
    }

    // 특정 상품의 이전 채팅 메시지 가져오기
    @GetMapping("/{productId}")
    public ResponseEntity<?> getPreviousChats(@PathVariable int productId) {
        try {
            // 특정 상품과 관련된 채팅 메시지 가져오기
            List<Chat> chatMessages = chatService.getChatsByProduct(productId);

            if (chatMessages.isEmpty()) {
                // 채팅 기록이 없을 때 메시지 반환
                return ResponseEntity.ok(Map.of("message", "채팅 기록이 없습니다.", "chats", chatMessages));
            }

            // 채팅 기록 반환
            return ResponseEntity.ok(Map.of("chats", chatMessages));
        } catch (SQLException e) {
            System.err.println("Database error while retrieving chat messages: " + e.getMessage());
            throw new RuntimeException("Error retrieving previous chat messages", e);
        } catch (Exception e) {
            System.err.println("Unexpected error while retrieving chat messages: " + e.getMessage());
            throw new RuntimeException("Unexpected error retrieving previous chat messages", e);
        }
    }

    // 로그인된 유저의 채팅 목록 가져오기
    @GetMapping("/get/chatList")
    public ResponseEntity<?> getChatList(@RequestParam int userId) {
        try {
            List<Chat> chatList = chatService.getChatList(userId);

            // 채팅을 상품 ID별로 그룹화
            Map<Integer, List<Chat>> chatGroupedByProduct = chatList.stream()
                    .collect(Collectors.groupingBy(Chat::getRelatedProductId));

            if (chatGroupedByProduct.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "채팅 내역이 없습니다.", "chats", chatGroupedByProduct));
            }

            return ResponseEntity.ok(Map.of("chats", chatGroupedByProduct));
        } catch (SQLException e) {
            System.err.println("Error retrieving chat list: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "채팅 내역을 불러오는 중 오류가 발생했습니다."));
        } catch (Exception e) {
            System.err.println("Unexpected error while retrieving chat list: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "예상치 못한 오류가 발생했습니다."));
        }
    }

    @PostMapping("/transaction/transfer")
    public ResponseEntity<Map<String, Object>> transferPoints(@RequestBody TransferRequest transferRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = chatService.transferPoints(
                    transferRequest.getSenderId(),
                    transferRequest.getRecipientId(),
                    transferRequest.getAmount()
            );

            if (success) {
                response.put("code", 1000);
                response.put("message", "송금이 완료되었습니다.");
            } else {
                response.put("code", 0);
                response.put("message", "포인트가 부족합니다.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "송금 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
