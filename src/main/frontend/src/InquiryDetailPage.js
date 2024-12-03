import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import "./InquiryDetailPage.css"; // 스타일 파일

const InquiryDetailPage = () => {
  const { question_idx } = useParams();
  const [question, setQuestion] = useState(null);
  const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
  const navigate = useNavigate();
  // 문의글 상세 조회 API 호출
  useEffect(() => {
    const fetchQuestionDetail = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/ask/question/${question_idx}`,
        );
        const data = await response.json();
        if (data.code === "1000") {
          setQuestion(data.data); // 질문 데이터를 상태로 설정
        }
      } catch (error) {
        console.error("문의글 조회 실패:", error);
      }
    };

    fetchQuestionDetail();
  }, [question_idx]);

  if (!question) {
    return <div>Loading...</div>; // 데이터 로딩 중 표시
  }

  // 작성일 포맷 변환
  const formattedDate = new Date(
    question.questionCreatedAt,
  ).toLocaleDateString();


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
        <h1 className="inquiry-detail-page-inquiry-title">
          {question.questionTitle}
        </h1>
        <div className="inquiry-detail-page-inquiry-meta">
          <p>
            <strong>작성자:</strong> {question.nickname}
          </p>
          <p>
            <strong>작성일:</strong> {formattedDate}
          </p>
          <p>
            <strong>공개여부:</strong>{" "}
            {question.questionPublic === true ? "공개" : "비공개"}
          </p>
        </div>
      </div>
      <hr className="inquiry-detail-page-inquiry-divider" />
      <div className="inquiry-detail-page-inquiry-content">
        <p>{question.questionContent}</p>
        {question.questionImage && (
          <div>
            <img
              src={
                "http://localhost:8080/image?image=" + question.questionImage
              }
              alt="문의글 이미지"
              className="inquiry-detail-page-img"
            />
          </div>
        )}
      </div>

      {!question.answer && isAdmin && (
        <div className="inquiry-detail-page-add-answer">
          <Link to={`/inquiry/${question_idx}/upload/answer`}>
            <button className="inquiry-detail-page-add-answer-button">
              답글 달기
            </button>
          </Link>
        </div>
      )}
      {/* 답글 섹션 */}
      {question.answer && (
        <div className="inquiry-detail-page-answer-section">
          <h3>답글</h3>
          <p>
            <strong>제목:</strong>{" "}
            <Link
              to={`/inquiry/answer/${question.answer.answerIdx}`}
              className="inquiry-detail-page-answer-link"
            >
              {question.answer.answerTitle}↗
            </Link>
          </p>
        </div>
      )}
    </div>
  );
};

export default InquiryDetailPage;
