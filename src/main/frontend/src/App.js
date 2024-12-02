import React, { useEffect, useState } from 'react';
import './App.css'; 
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'; 
import MainPage from './MainPage';
import LoginPage from './LoginPage';
import SignupPage from './SignupPage';
import NoticePage from './NoticePage';
import NoticeDetailPage from './NoticeDetailPage';
import MyPage from './MyPage';
import NavButton from './NavButton';
import AdminMainPage from "./AdminMainPage";
import UserManagemnetPage from "./UserManagementPage";
import AdminManagemnetPage from "./AdminManagementPage";
import ReportListPage from "./ReportListPage";
import CategoryPage from './CategoryPage'; // CategoryPage 추가
import ProductPage from './ProductPage'; // ProductPage 추가
import ProductUploadPage from './ProductUploadPage'; // ProductPage 추가
import UserEditPage from './UserEditPage'; 
import ProductSearchPage from './ProductSearchPage';
import Chat from './Chat';
import InquiriesPage from './InquiriesPage';
import InquiryDetailPage from './InquiryDetailPage';
import AnswerDetailPage from './AnswerDetailPage';
import QuestionUploadPage from './QuestionUploadPage';
import AnswerUploadPage from './AnswerUploadPage';

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // 로그인 상태를 확인
  useEffect(() => {
    const storedToken = localStorage.getItem('token'); // 토큰 가져오기
    if (storedToken) {
      setIsLoggedIn(true); // 토큰이 존재하면 로그아웃 버튼으로 설정
    } else {
      setIsLoggedIn(false); // 토큰이 없으면 로그인 버튼으로 설정
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token'); // 토큰 제거
    localStorage.removeItem('isAdmin'); //관리자 제거
    setIsLoggedIn(false); // 상태 갱신
    alert('로그아웃 되었습니다.');
    window.location.href = '/'; // 로그아웃 후 메인 페이지로 이동
  };

  return (
    <Router>
      <header className="header">
        <Link to="/" className="logo">
          <img src="/logo.png" alt="가지마켓 로고" />
        </Link>
        <nav className="nav">
          {isLoggedIn ? (
            <button onClick={handleLogout} className="nav-button">로그아웃</button>
          ) : (
            <NavButton to="/login">로그인</NavButton>
          )}
          <NavButton to="/notices">공지사항</NavButton>
          <NavButton to="/inquiries">문의사항</NavButton>
          <NavButton to="/mypage">마이페이지</NavButton>
          <NavButton to="/search">상품 검색</NavButton> 
        </nav>
        
      </header>

      {/* 카테고리 섹션 */}
      <section className="category-section">
        <h2>상품 카테고리</h2>
        <div className="category-bar">
        <Link to="/category/all" className="category-button">
        <button>전체</button>
      </Link>
      <Link to="/category/Electronics" className="category-button">
        <button>전자기기</button>
      </Link>
      <Link to="/category/Fashion" className="category-button">
        <button>의류</button>
      </Link>
      <Link to="/category/Furniture" className="category-button">
        <button>가구</button>
      </Link>
      <Link to="/category/Books" className="category-button">
        <button>도서</button>
      </Link>
      <Link to="/category/Other" className="category-button">
        <button>기타</button>
      </Link>
        </div>
      </section>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/category/:category" element={<CategoryPage />} />
        <Route path="/product/:productIdx" element={<ProductPage />} />
        <Route path="/product/upload" element={<ProductUploadPage />} />
        <Route path="/login" element={<LoginPage setIsLoggedIn={setIsLoggedIn} />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/notices" element={<NoticePage />} />
        <Route path="/notices/:id" element={<NoticeDetailPage />} />
        <Route path="/admin" element={<AdminMainPage />} />
        <Route path="/management/user" element={<UserManagemnetPage />} />
        <Route path="/management/admin" element={<AdminManagemnetPage />} />
        <Route path="/admin/report" element={<ReportListPage />} />
        <Route path="/mypage" element={<MyPage />} /> 
        <Route path="/user/:email/edit" element={<UserEditPage />} />
        <Route path="/search" element={<ProductSearchPage />} />
        <Route path="/product/:productIdx/chat" element={<Chat />} />
        <Route path="/inquiries" element={<InquiriesPage />} />
        <Route path="/inquiry/question/:question_idx" element={<InquiryDetailPage />} />
        <Route path="/inquiry/answer/:answer_idx" element={<AnswerDetailPage />} />
        <Route path="/inquiry/upload" element={<QuestionUploadPage />} />
        <Route path="/inquiry/:question_idx/upload/answer" element={<AnswerUploadPage />} />
      </Routes>
    </Router>
  );
};

export default App;
