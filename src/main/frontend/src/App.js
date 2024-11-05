import React from 'react';
import './App.css'; 
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'; // Link 추가
import MainPage from './MainPage';
import LoginPage from './LoginPage';
import SignupPage from './SignupPage';
import NoticePage from './NoticePage';
import NoticeDetailPage from './NoticeDetailPage';
import NavButton from './NavButton';

const App = () => {
  return (
    <Router>
      <header className="header">
        <Link to="/" className="logo">가지마켓</Link> {/* Link로 변경 */}
        <nav className="nav">
          <NavButton to="/login">로그인</NavButton>
          <NavButton to="/notices">공지사항</NavButton>
          <NavButton to="/inquiries">문의사항</NavButton>
          <NavButton to="/mypage">마이페이지</NavButton>
        </nav>
      </header>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/notices" element={<NoticePage />} />
        <Route path="/notices/:id" element={<NoticeDetailPage />} />
      </Routes>
    </Router>
  );
};

export default App;
