import React from 'react';
import './App.css'; 
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'; // Link 추가
import MainPage from './MainPage';
import LoginPage from './LoginPage';
import SignupPage from './SignupPage';
import NoticePage from './NoticePage';
import NoticeDetailPage from './NoticeDetailPage';
import NavButton from './NavButton';
import CategoryPage from './CategoryPage'; // CategoryPage 추가
import ProductPage from './ProductPage'; // ProductPage 추가
import ProductUploadPage from './ProductUploadPage'; // ProductPage 추가

const App = () => {
  return (
    <Router>
      <header className="header">
        <Link to="/" className="logo">
          <img src="/logo.png" alt="가지마켓 로고" />
        </Link>
        <nav className="nav">
          <NavButton to="/login"zz>로그인</NavButton>
          <NavButton to="/notices">공지사항</NavButton>
          <NavButton to="/inquiries">문의사항</NavButton>
          <NavButton to="/mypage">마이페이지</NavButton>
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
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/notices" element={<NoticePage />} />
        <Route path="/notices/:id" element={<NoticeDetailPage />} />
      </Routes>
    </Router>
  );
};

export default App;
