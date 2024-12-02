import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import "./InquiryDetailPage.css"; // 스타일 파일
import axios from 'axios';

const ReportDetailPage = () => {
  const { reportIdx } = useParams(); // URL에서 reportIdx를 추출
  const navigate = useNavigate(); // 페이지 이동을 위한 hook
  const [reportDetail, setReportDetail] = useState(null); // 신고 상세 정보 상태
  const [error, setError] = useState(null); // 에러 상태
  const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));

  // 신고글 상세 조회 API 호출
  useEffect(() => {
    const fetchReportDetail = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/report/${reportIdx}`);
        if (response.data.code === "1000") {
          setReportDetail(response.data.data); // 신고 내역을 상태에 저장
        } else {
          setError(response.data.data); // 에러 메시지 저장
        }
      } catch (err) {
        console.error("API 호출 중 오류 발생", err);
        setError("서버 오류로 신고 내역을 불러올 수 없습니다.");
      }
    };

    fetchReportDetail();
  }, [reportIdx]);

  const { text, color } = getStatusTextAndColor(reportDetail.status); // 상태와 색상 추출

  
  // 오류 메시지나 로딩 상태 처리
  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!reportDetail) {
    return <div>Loading...</div>;
  }

  // 작성일 포맷 변환
  const formattedDate = new Date(
    reportDetail.createdAt,
  ).toLocaleDateString();

    // 신고 처리 상태 한글로 변환 및 색상 변경
  const getStatusTextAndColor = (status) => {
    switch (status) {
      case "pending":
        return { text: "처리 전", color: "black" }; // 처리 전 상태는 검은색
      case "resolved":
        return { text: "처리 완료", color: "green" }; // 처리 완료는 초록색
      case "rejected":
        return { text: "신고 취소", color: "red" }; // 신고 취소는 빨간색
      default:
        return { text: "알 수 없음", color: "black" }; // 기본 색상은 검은색
    }
  };

  const { text, color } = getStatusTextAndColor(reportDetail.status); // 상태와 색상 추출

  return (
    <div className="inquiry-detail-page">
      <div className="inquiry-detail-page-header">
        <h1 className="inquiry-detail-page-inquiry-title">
          {reportDetail.title}
        </h1>
        <div className="inquiry-detail-page-inquiry-meta">
          <p>
            <strong>신고자:</strong> {reportDetail.reportingUserNickname}
          </p>
          <p>
            <strong>신고일:</strong> {formattedDate}
          </p>
          <p>
            <strong>처리 상태:</strong> <span style={{ color }}>{text}</span> {/* 색상 적용 */}
          </p>
        </div>
      </div>
      <hr className="inquiry-detail-page-inquiry-divider" />
      <div className="inquiry-detail-page-inquiry-content">
          <p>
            <strong>신고하는 유저:</strong> {reportDetail.reportedUserNickname}
          </p>
          <p>
            <strong>신고하는 글:</strong> {reportDetail.reportedProductTitle}
          </p>
          <p>
            <strong>내용:</strong> {reportDetail.content}
          </p>
      </div>

      

    </div>
  );
};

export default ReportDetailPage;
