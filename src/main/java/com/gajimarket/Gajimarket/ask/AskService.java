package com.gajimarket.Gajimarket.ask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AskService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 문의글 및 답글 가져오기
    public List<QuestionWithAnswerDTO> getQuestionsWithAnswers(boolean isAdmin, int userIdx) {
        String sql = "SELECT " +
                "q.question_idx, q.title AS question_title, q.content AS question_content, " +
                "q.created_at AS question_created_at, q.user_idx AS question_user_idx, " +
                "q.public AS question_public, q.image AS question_image, " +
                "a.answer_idx, a.title AS answer_title, a.content AS answer_content, " +
                "a.created_at AS answer_created_at, a.admin_index AS answer_admin_index, a.public AS answer_public, a.image AS answer_image " +
                "FROM Question q " +
                "LEFT JOIN Answer a ON q.question_idx = a.question_idx " +
                "WHERE (? = true OR q.public = 'y' OR q.user_idx = ?) " +
                "ORDER BY q.created_at DESC";

        Object[] params = {isAdmin, userIdx};

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            QuestionWithAnswerDTO dto = new QuestionWithAnswerDTO();
            dto.setQuestionIdx(rs.getInt("question_idx"));
            dto.setQuestionTitle(rs.getString("question_title"));
            dto.setQuestionContent(rs.getString("question_content"));
            dto.setQuestionCreatedAt(rs.getTimestamp("question_created_at").toLocalDateTime());
            dto.setQuestionUserIdx(rs.getInt("question_user_idx"));
            dto.setQuestionPublic(rs.getString("question_public").equals("y"));
            dto.setQuestionImage(rs.getString("question_image"));

            if (rs.getInt("answer_idx") != 0) {
                AnswerDTO answer = new AnswerDTO();
                answer.setAnswerIdx(rs.getInt("answer_idx"));
                answer.setAnswerTitle(rs.getString("answer_title"));
                answer.setAnswerContent(rs.getString("answer_content"));
                answer.setAnswerCreatedAt(rs.getTimestamp("answer_created_at").toLocalDateTime());
                answer.setAnswerPublic(rs.getString("answer_public"));
                answer.setAnswerAdminIndex(rs.getInt("answer_admin_index"));
                answer.setAnswerImage(rs.getString("answer_image"));
                dto.setAnswer(answer);
            }
            return dto;
        });
    }

    // 문의글 조회
    public QuestionWithAnswerDTO getQuestionWithAnswer(int questionIdx) {
        // 질문과 닉네임을 JOIN하여 조회
        String questionSql =
                "SELECT q.question_idx, q.title, q.content, q.created_at, q.public, q.user_idx, q.image, u.nickname " +
                        "FROM Question q " +
                        "JOIN User u ON q.user_idx = u.user_idx " +
                        "WHERE q.question_idx = ?";

        QuestionWithAnswerDTO questionWithAnswerDTO = jdbcTemplate.queryForObject(questionSql, new Object[]{questionIdx}, (rs, rowNum) -> {
            QuestionWithAnswerDTO questionDTO = new QuestionWithAnswerDTO();
            questionDTO.setQuestionIdx(rs.getInt("question_idx"));
            questionDTO.setQuestionTitle(rs.getString("title"));
            questionDTO.setQuestionContent(rs.getString("content"));
            questionDTO.setQuestionCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            questionDTO.setQuestionPublic(rs.getString("public").equals("y"));
            questionDTO.setQuestionUserIdx(rs.getInt("user_idx"));
            questionDTO.setQuestionImage(rs.getString("image"));
            questionDTO.setNickname(rs.getString("nickname")); // 닉네임 추가
            return questionDTO;
        });

        // 답글 정보 조회 (답글이 있을 경우)
        String answerSql = "SELECT a.answer_idx, a.title, a.content, a.created_at, a.admin_index, a.image, a.public " +
                "FROM Answer a WHERE a.question_idx = ?";
        List<AnswerDTO> answerList = jdbcTemplate.query(answerSql, new Object[]{questionIdx}, (rs, rowNum) -> {
            AnswerDTO answerDTO = new AnswerDTO();
            answerDTO.setAnswerIdx(rs.getInt("answer_idx"));
            answerDTO.setAnswerTitle(rs.getString("title"));
            answerDTO.setAnswerContent(rs.getString("content"));
            answerDTO.setAnswerCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            answerDTO.setAnswerPublic(rs.getString("public"));
            answerDTO.setAnswerAdminIndex(rs.getInt("admin_index"));
            answerDTO.setAnswerImage(rs.getString("image"));
            answerDTO.setQuestionIdx(questionIdx);
            answerDTO.setQuestionTitle("");
            return answerDTO;
        });

        // 답글이 있을 경우, 첫 번째 답글을 반환
        if (!answerList.isEmpty()) {
            questionWithAnswerDTO.setAnswer(answerList.get(0)); // 첫 번째 답글만 가져옴
        }

        return questionWithAnswerDTO;
    }

    // 답글 상세 조회 (문의글 제목 포함)
    public AnswerDTO getAnswerDetail(int answerIdx) {
        // 1. 답글과 해당 질문 제목을 JOIN하여 조회
        String sql = "SELECT a.answer_idx, a.title AS answer_title, a.content AS answer_content, a.created_at AS answer_created_at, a.public AS answer_public," +
                "a.admin_index, a.image AS answer_image, a.question_idx, q.title AS question_title " +
                "FROM Answer a " +
                "JOIN Question q ON a.question_idx = q.question_idx " +
                "WHERE a.answer_idx = ?";

        AnswerDTO answerDTO = jdbcTemplate.queryForObject(sql, new Object[]{answerIdx}, (rs, rowNum) -> {
            AnswerDTO dto = new AnswerDTO();
            dto.setAnswerIdx(rs.getInt("answer_idx"));
            dto.setAnswerTitle(rs.getString("answer_title"));
            dto.setAnswerContent(rs.getString("answer_content"));
            dto.setAnswerCreatedAt(rs.getTimestamp("answer_created_at").toLocalDateTime());
            dto.setAnswerPublic(rs.getString("answer_public"));
            dto.setAnswerAdminIndex(rs.getInt("admin_index"));
            dto.setAnswerImage(rs.getString("answer_image"));
            dto.setQuestionIdx(rs.getInt("question_idx"));
            dto.setQuestionTitle(rs.getString("question_title"));
            return dto;
        });

        return answerDTO;
    }

    // 문의글 작성 서비스
    public int writeAsk(AskWriteRequest askWriteRequest) {
        // SQL 쿼리 작성
        String insertSql = "INSERT INTO Question (title, content, created_at, public, user_idx, image) " +
                "VALUES (?, ?, NOW(), ?, ?, ?)";

        // 문의글 작성
        jdbcTemplate.update(insertSql, askWriteRequest.getTitle(), askWriteRequest.getContent(),
                askWriteRequest.getPublicStatus(), askWriteRequest.getUser_idx(), askWriteRequest.getImage());

        // 새로 생성된 question_idx 가져오기 (MySQL의 경우 LAST_INSERT_ID() 사용)
        String lastInsertIdSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(lastInsertIdSql, Integer.class);
    }


    // 문의글에 대해 관리자가 답글 작성
    public int writeAnswer(int questionIdx, AskWriteRequest answerRequest) {
        // SQL 쿼리 (MySQL에서 LAST_INSERT_ID 사용)
        String sql = "INSERT INTO Answer (title, content, created_at, public, question_idx, admin_index, image) " +
                "VALUES (?, ?, NOW(), ?, ?, ?, ?)";

        // 쿼리 실행
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, answerRequest.getTitle());
            ps.setString(2, answerRequest.getContent());
            ps.setString(3, answerRequest.getPublicStatus());
            ps.setInt(4, questionIdx);
            ps.setInt(5, answerRequest.getUser_idx()); // admin_idx 사용
            ps.setString(6, answerRequest.getImage());
            return ps;
        });

        // 삽입된 마지막 아이디 값을 SELECT LAST_INSERT_ID()로 가져옴
        String getLastInsertIdSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(getLastInsertIdSql, Integer.class);
    }



}
