import React, { useEffect, useState } from "react";
import "./ReportListPage.css";

const ReportListPage = () => {
  const [reports, setReports] = useState([]);

  // 서버에서 신고 목록 불러오기
  const fetchReportList = () => {
    fetch("http://localhost:8080/admin/get/reportList")
      .then((response) => response.json())
      .then((data) => {
        if (data.code === "1000") {
          // 최신 순으로 정렬 (createdAt 기준)
          setReports(data.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
        } else {
          console.error("신고 목록 불러오기 실패");
        }
      })
      .catch((error) => console.error("에러 발생:", error));
  };

  useEffect(() => {
    fetchReportList(); // 초기 데이터 로드
  }, []);

  // 신고 처리 상태 한글로 변환 및 색상 변경
  const getStatusTextAndColor = (status) => {
    switch (status) {
      case "pending":
        return { text: "처리 전"}; 
      case "resolved":
        return { text: "처리 완료" };
      case "rejected":
        return { text: "신고 취소"}; 
      default:
        return { text: "알 수 없음" };
    }
  };

  // 처리 상태에 따라 배경색 변경
  const getRowBackgroundColor = (status) => {
    if (status === "resolved" || status === "rejected") {
      return "#E6E6E6"; // 처리 완료 또는 신고 취소는 회색 배경
    }
    return "transparent"; // 처리 전은 기본 배경
  };

  return (
    <div className="report-management-page">
      <h1>신고 내역 관리</h1>
      <table className="report-management-page-report-table">
        <thead>
          <tr>
            <th>순번</th>
            <th>신고 제목</th>
            <th>신고 처리 상태</th>
            <th>신고 일자</th>
          </tr>
        </thead>
        <tbody>
          {reports.map((report, index) => {
            const { text, color } = getStatusTextAndColor(report.status);
            const rowBackgroundColor = getRowBackgroundColor(report.status);
            return (
              <tr key={report.reportIdx} style={{ backgroundColor: rowBackgroundColor }}>
                <td>{index + 1}</td>
                <td>{report.title}</td>
                <td style={{ color }}>{text}</td>
                <td>{new Date(report.createdAt).toLocaleDateString()}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default ReportListPage;
