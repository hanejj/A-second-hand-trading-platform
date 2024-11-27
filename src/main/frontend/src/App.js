import React from 'react';
import './App.css'; 
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'; 
import MainPage from './MainPage';
import LoginPage from './LoginPage';
import SignupPage from './SignupPage';
import NoticePage from './NoticePage';
import NoticeDetailPage from './NoticeDetailPage';
import MyPage from './MyPage';
import NavButton from './NavButton';
import UserEditPage from './UserEditPage'; 
import ProductSearchPage from './ProductSearchPage';

const App = () => {
  const token = localStorage.getItem('token'); // 토큰을 통해 로그인 여부 확인

  const handleLogout = () => {
    localStorage.removeItem('token');
    alert('로그아웃 되었습니다.');
    window.location.reload(); // 로그아웃 후 페이지 새로고침
  };

  return (
    <Router>
      <header className="header">
        <Link to="/" className="logo">가지마켓</Link> 
        <nav className="nav">
          {token ? (
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
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/notices" element={<NoticePage />} />
        <Route path="/notices/:id" element={<NoticeDetailPage />} />
        <Route path="/mypage" element={<MyPage />} /> 
        <Route path="/user/:email/edit" element={<UserEditPage />} />
        <Route path="/search" element={<ProductSearchPage />} />
      </Routes>
    </Router>
  );
};

export default App;
