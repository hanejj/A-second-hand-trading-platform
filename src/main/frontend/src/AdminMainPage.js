import React from "react";
import { useNavigate } from "react-router-dom";
import "./AdminMainPage.css"; // 버튼 스타일이 정의된 CSS 파일

const AdminMainPage = () => {
  const navigate = useNavigate();

  const handleButtonClick = (section) => {
    if (section === "회원 관리") {
      navigate("/management/user"); // 회원관리 페이지로 이동
    } 
    else if (section==="관리자 관리"){
      navigate("/management/admin");
    }
    else {
      console.log(`${section} 버튼 클릭됨`);
      // 다른 섹션별로 로직 추가 가능
    }
  };

  return (
    <div className="admin-main-page">
      <h1>관리자페이지</h1>
      <div className="admin-main-page-button-grid">
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("회원 관리")}
        >
          회원관리
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("상품 관리")}
        >
          상품관리
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("관리자 관리")}
        >
          관리자관리
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("공지사항")}
        >
          공지사항
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("문의사항")}
        >
          문의사항
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("신고 내역")}
        >
          신고내역
        </button>
      </div>
    </div>
  );
};

export default AdminMainPage;
