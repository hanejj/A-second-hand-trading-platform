package com.gajimarket.Gajimarket.chat;

import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ChatService {
    private final ChatDAO chatDAO;

    public ChatService(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
    }

    public void saveChat(Chat chat) throws SQLException {
        chatDAO.saveChat(
                chat.getSenderId(), // 수정된 필드 이름에 맞게 적용
                chat.getRecipientId(),
                chat.getMessageContent(),
                chat.getRelatedProductId()
        );
    }

    // 상품별 채팅 가져오기
    public List<Chat> getChatsByProduct(int productIdx) throws SQLException {
        return chatDAO.getChatsByProduct(productIdx);
    }

    // 로그인된 유저가 참여한 모든 채팅 목록 가져오기
    public List<Chat> getChatList(int userId) throws SQLException {
        return chatDAO.getChatListByUserId(userId);
    }

    public boolean transferPoints(int senderId, int recipientId, int amount) throws SQLException {
        // 송금자의 포인트 확인
        int senderPoints = chatDAO.getUserPoints(senderId);
        if (senderPoints < amount) {
            return false; // 포인트 부족
        }

        // 트랜잭션 내 작업
        chatDAO.updateUserPoints(senderId, senderPoints - amount); // 송금자 포인트 차감
        chatDAO.insertPointHistory(senderId, -amount, "withdraw"); // 송금자 기록 추가

        int recipientPoints = chatDAO.getUserPoints(recipientId);
        chatDAO.updateUserPoints(recipientId, recipientPoints + amount); // 수신자 포인트 증가
        chatDAO.insertPointHistory(recipientId, amount, "charge"); // 수신자 기록 추가

        return true; // 성공
    }
}
