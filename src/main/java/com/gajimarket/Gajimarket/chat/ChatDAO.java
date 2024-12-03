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

    // 특정 상품의 채팅 메시지 가져오기 (닉네임 포함)
    public List<Chat> getChatsByProduct(int relatedProductId) {
        String query = "SELECT c.*, u.nickname AS senderNickname " +
                "FROM Chat c " +
                "JOIN User u ON c.sender_idx = u.user_idx " +
                "WHERE c.product_idx = ? " +
                "ORDER BY c.created_at ASC";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Chat chat = new Chat();
            chat.setChatId(rs.getInt("chat_idx"));
            chat.setSenderId(rs.getInt("sender_idx"));
            chat.setRecipientId(rs.getInt("receiver_idx"));
            chat.setMessageContent(rs.getString("content"));
            chat.setSentAt(rs.getTimestamp("created_at").toLocalDateTime());
            chat.setRelatedProductId(rs.getInt("product_idx"));
            chat.setSenderNickname(rs.getString("senderNickname")); // 닉네임 추가
            return chat;
        }, relatedProductId);
    }

    // 로그인된 사용자의 모든 채팅 목록 가져오기 (닉네임 포함)
    public List<Chat> getChatListByUserId(int userId) {
        String query = "SELECT c.*, u.nickname AS senderNickname " +
                "FROM Chat c " +
                "JOIN User u ON c.sender_idx = u.user_idx " +
                "WHERE c.sender_idx = ? OR c.receiver_idx = ? " +
                "ORDER BY c.created_at ASC";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Chat chat = new Chat();
            chat.setChatId(rs.getInt("chat_idx"));
            chat.setSenderId(rs.getInt("sender_idx"));
            chat.setRecipientId(rs.getInt("receiver_idx"));
            chat.setMessageContent(rs.getString("content"));
            chat.setSentAt(rs.getTimestamp("created_at").toLocalDateTime());
            chat.setRelatedProductId(rs.getInt("product_idx"));
            chat.setSenderNickname(rs.getString("senderNickname")); // 닉네임 추가
            return chat;
        }, userId, userId);
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
