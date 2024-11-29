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
import AdminMainPage from "./AdminMainPage";
import UserManagemnetPage from "./UserManagementPage";
import AdminManagemnetPage from "./AdminManagementPage";
import ReportListPage from "./ReportListPage";
import CategoryPage from './CategoryPage'; // CategoryPage 추가
import ProductPage from './ProductPage'; // ProductPage 추가
import ProductUploadPage from './ProductUploadPage'; // ProductPage 추가
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
        <Link to="/" className="logo">
          <img src="/logo.png" alt="가지마켓 로고" />
        </Link>
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
        <Route path="/admin" element={<AdminMainPage />} />
        <Route path="/management/user" element={<UserManagemnetPage />} />
        <Route path="/management/admin" element={<AdminManagemnetPage />} />
        <Route path="/admin/report" element={<ReportListPage />} />
        <Route path="/mypage" element={<MyPage />} /> 
        <Route path="/user/:email/edit" element={<UserEditPage />} />
        <Route path="/search" element={<ProductSearchPage />} />
      </Routes>
    </Router>
  );
};

export default App;
