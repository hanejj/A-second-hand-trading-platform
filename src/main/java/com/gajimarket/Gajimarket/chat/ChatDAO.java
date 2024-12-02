package com.gajimarket.Gajimarket.chat;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatDAO {

    private final JdbcTemplate jdbcTemplate;

    public ChatDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 채팅 메시지 저장
    public void saveChat(int senderId, int recipientId, String messageContent, int relatedProductId) {
        String query = "INSERT INTO Chat (sender_idx, receiver_idx, content, created_at, product_idx) VALUES (?, ?, ?, NOW(), ?)";
        jdbcTemplate.update(query, senderId, recipientId, messageContent, relatedProductId);
    }

    // 특정 상품의 채팅 메시지 가져오기
    public List<Chat> getChatsByProduct(int relatedProductId) {
        String query = "SELECT * FROM Chat WHERE product_idx = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Chat chat = new Chat();
            chat.setChatId(rs.getInt("chat_idx")); // chat_idx → chatId
            chat.setSenderId(rs.getInt("sender_idx")); // sender_idx → senderId
            chat.setRecipientId(rs.getInt("receiver_idx")); // receiver_idx → recipientId
            chat.setMessageContent(rs.getString("content")); // content → messageContent
            chat.setSentAt(rs.getTimestamp("created_at").toLocalDateTime()); // created_at → sentAt
            chat.setRelatedProductId(rs.getInt("product_idx")); // product_idx → relatedProductId
            return chat;
        }, relatedProductId);
    }

    public List<Chat> getChatListByUserId(int userId) {
        String query = "SELECT chat_idx, sender_idx, receiver_idx, content, created_at, product_idx " +
                "FROM Chat WHERE sender_idx = ? OR receiver_idx = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Chat chat = new Chat();
            chat.setChatId(rs.getInt("chat_idx"));
            chat.setSenderId(rs.getInt("sender_idx"));
            chat.setRecipientId(rs.getInt("receiver_idx"));
            chat.setMessageContent(rs.getString("content"));
            chat.setSentAt(rs.getTimestamp("created_at").toLocalDateTime());
            chat.setRelatedProductId(rs.getInt("product_idx"));
            return chat;
        }, userId, userId); // sender_idx 또는 receiver_idx가 유저 ID와 일치하는 채팅을 가져옴
    }

    public int getUserPoints(int userIdx) {
        String query = "SELECT point FROM Point WHERE user_idx = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, userIdx);
    }

    public void updateUserPoints(int userIdx, int newPoints) {
        String query = "UPDATE Point SET point = ? WHERE user_idx = ?";
        jdbcTemplate.update(query, newPoints, userIdx);
    }
}
