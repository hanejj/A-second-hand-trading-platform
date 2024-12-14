import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import "./InquiryDetailPage.css"; // 스타일 파일

const AnswerDetailPage = () => {
  const { answer_idx } = useParams(); // URL에서 answer_idx를 받아옴
  const [answer, setAnswer] = useState(null);
  const navigate = useNavigate();

  // 답글 상세 조회 API 호출
  useEffect(() => {
    const fetchAnswerDetail = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/ask/answer/${answer_idx}`,
        );
        const data = await response.json();
        if (data.code === "1000") {
          setAnswer(data.data); // 답글 데이터를 상태로 설정
        }
      } catch (error) {
        console.error("답글 조회 실패:", error);
      }
    };

    fetchAnswerDetail();
  }, [answer_idx]);

  if (!answer) {
    return <div>Loading...</div>; // 데이터 로딩 중 표시
  }

  // 작성일 포맷 변환
  const formattedDate = new Date(answer.answerCreatedAt).toLocaleDateString();

  // 뒤로 가기 버튼 핸들러
  const handleGoBack = () => {
    navigate(-1); // 이전 페이지로 이동
  };

  return (
    <div className="inquiry-detail-page">
      {/* 뒤로 가기 버튼 */}
      <button className="go-back-button" onClick={handleGoBack}>
        &lt; 뒤로 가기
      </button>
      <div className="inquiry-detail-page-header">
        <h1 className="inquiry-detail-page-answer-title">
          {answer.answerTitle}
        </h1>
        <div className="inquiry-detail-page-answer-meta">
          <p>
            <strong>작성자:</strong> 관리자
          </p>
          <p>
            <strong>작성일:</strong> {formattedDate}
          </p>
          <p>
            <strong>공개여부:</strong>{" "}
            {answer.answerPublic === "y" || answer.answerPublic === true
              ? "공개"
              : "비공개"}
          </p>
        </div>
      </div>
      <hr className="inquiry-detail-page-answer-divider" />
      {/* 문의글 제목 표시 */}
      <div className="inquiry-detail-page-answer-section">
        <h3>문의글</h3>
        <p>
          <strong>제목:</strong>{" "}
          <Link
            to={`/inquiry/question/${answer.questionIdx}`}
            className="inquiry-detail-page-answer-link"
          >
            {answer.questionTitle}↗
          </Link>
        </p>
      </div>
      <div className="inquiry-detail-page-answer-content">
        <p>{answer.answerContent}</p>
        {answer.answerImage && (
          <div>
            <img
              src={"http://localhost:8080/image?image=" + answer.answerImage}
              alt="답글 이미지"
              className="inquiry-detail-page-img"
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default AnswerDetailPage;
