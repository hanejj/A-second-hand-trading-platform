package com.gajimarket.Gajimarket.notice;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NoticeDAO {

    private final DataSource dataSource;

    public NoticeDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        String query = """
        SELECT n.notice_idx AS noticeId, n.admin_index AS adminId, n.title AS noticeTitle,
               n.content AS noticeContent, n.created_at AS noticeCreatedAt, n.image AS noticeImage,
               a.name AS adminName
        FROM Notice n
        JOIN Admin a ON n.admin_index = a.admin_index
        ORDER BY n.notice_idx ASC
        """; // notice_idx 기준으로 오름차순 정렬

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Notice notice = new Notice();
                notice.setNoticeId(resultSet.getInt("noticeId"));
                notice.setAdminId(resultSet.getInt("adminId"));
                notice.setNoticeTitle(resultSet.getString("noticeTitle"));
                notice.setNoticeContent(resultSet.getString("noticeContent"));
                notice.setNoticeCreatedAt(resultSet.getTimestamp("noticeCreatedAt").toLocalDateTime());
                notice.setNoticeImage(resultSet.getString("noticeImage"));
                notice.setAdminName(resultSet.getString("adminName"));
                notices.add(notice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notices;
    }

    public Notice getNoticeById(int noticeId) {
        String query = """
            SELECT n.notice_idx AS noticeId, n.admin_index AS adminId, n.title AS noticeTitle,
                   n.content AS noticeContent, n.created_at AS noticeCreatedAt, n.image AS noticeImage,
                   a.name AS adminName
            FROM Notice n
            JOIN Admin a ON n.admin_index = a.admin_index
            WHERE n.notice_idx = ?
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, noticeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Notice notice = new Notice();
                    notice.setNoticeId(resultSet.getInt("noticeId"));
                    notice.setAdminId(resultSet.getInt("adminId"));
                    notice.setNoticeTitle(resultSet.getString("noticeTitle"));
                    notice.setNoticeContent(resultSet.getString("noticeContent"));
                    notice.setNoticeCreatedAt(resultSet.getTimestamp("noticeCreatedAt").toLocalDateTime());
                    notice.setNoticeImage(resultSet.getString("noticeImage"));
                    notice.setAdminName(resultSet.getString("adminName"));
                    return notice;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // 해당 ID의 공지사항이 없는 경우 null 반환
    }

    public boolean deleteNoticeById(int noticeId) {
        String query = "DELETE FROM Notice WHERE notice_idx = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, noticeId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // 삭제 성공 시 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // 삭제 실패 시 false 반환
    }

    public void createNotice(Notice notice) {
        String query = """
        INSERT INTO Notice (admin_index, title, content, created_at, image)
        VALUES (?, ?, ?, NOW(), ?)
    """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, notice.getAdminId());
            statement.setString(2, notice.getNoticeTitle());
            statement.setString(3, notice.getNoticeContent());
            statement.setString(4, notice.getNoticeImage());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateNotice(Notice notice) {
        String query = """
        UPDATE Notice
        SET title = ?, content = ?, image = ?
        WHERE notice_idx = ?
    """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, notice.getNoticeTitle());
            statement.setString(2, notice.getNoticeContent());
            statement.setString(3, notice.getNoticeImage());
            statement.setInt(4, notice.getNoticeId());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
