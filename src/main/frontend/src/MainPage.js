import React, { useState, useEffect } from 'react';
import './MainPage.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const [popularProducts, setPopularProducts] = useState([]);
  const [latestProducts, setLatestProducts] = useState([]);
  const [sessionInfo, setSessionInfo] = useState(null);
  const [isAdmin, setIsAdmin] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // 샘플 상품 데이터 설정
    const productData = [
      { id: 1, title: 'MacBook Pro 2019', price: 1500, description: '중고 맥북 프로, 2019년형, 좋은 상태' },
      { id: 2, title: 'iPhone 12', price: 800, description: '중고 아이폰 12, 블랙, 128GB' },
      { id: 3, title: 'Samsung Galaxy S21', price: 900, description: '중고 갤럭시 S21, 실버, 256GB' },
      { id: 4, title: 'Nintendo Switch', price: 300, description: '닌텐도 스위치, 거의 새 것' },
      { id: 5, title: 'AirPods Pro', price: 200, description: '에어팟 프로, 미사용' },
      { id: 6, title: 'Sony WH-1000XM4', price: 350, description: '소니 헤드폰, 최신 모델' },
      { id: 7, title: 'Dell XPS 13', price: 1200, description: 'Dell XPS 13, 2020년형' },
      { id: 8, title: 'GoPro Hero 9', price: 450, description: '고프로 히어로 9, 미개봉' },
    ];

    setPopularProducts(productData.slice(0, 6)); // 인기순
    setLatestProducts(productData.slice(6)); // 최신순

    // 로컬 스토리지에서 JWT 토큰 가져오기
    const token = localStorage.getItem('token');
    const isAdminValue = localStorage.getItem('isAdmin');
    setIsAdmin(isAdminValue);

    if (token) {
      // 사용자 정보 요청
      axios.get('http://localhost:8080/user/profile', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(response => {
        if (response.data && response.data.code === 1000) {
          setSessionInfo(response.data.user);
        } else {
          throw new Error('사용자 정보를 가져오는 중 오류 발생');
        }
      })
      .catch(error => {
        console.error('토큰 정보를 가져오는 중 오류 발생:', error);
        if (error.response && error.response.status === 401) {
          alert('로그인 토큰이 만료되었습니다. 다시 로그인해주세요.');
          localStorage.removeItem('token');
          navigate('/login'); // 로그인 페이지로 이동
        }
      });
    } else {
      alert('로그인이 필요합니다.');
      navigate('/login');
    }
  }, [navigate]);

  const handleLogout = () => {
    // 로그아웃 시 로컬 스토리지에서 토큰 및 isAdmin 삭제
    localStorage.removeItem('token');
    localStorage.removeItem('isAdmin');
    alert('로그아웃 되었습니다.');
    navigate('/login'); // 로그인 페이지로 이동
  };

  return (
    <div className="main-page">
      {/* 토큰 정보 섹션 */}
      <header className="header">
        <div className="session-info">
          {sessionInfo && sessionInfo.id ? (
            <div>
              <p>로그인된 사용자: {sessionInfo.id}</p>
              <p>관리자 여부: {isAdmin === 'true' ? '관리자' : '일반 사용자'}</p>
              <button onClick={handleLogout}>로그아웃</button>
            </div>
          ) : (
            <p>로그인이 필요합니다</p>
          )}
        </div>
      </header>

      {/* 카테고리 섹션 */}
      <section className="category-section">
        <h2>카테고리</h2>
        <div className="category-bar">
          <button>전자기기</button>
          <button>의류</button>
          <button>가구</button>
          <button>도서</button>
          <button>기타</button>
        </div>
      </section>

      {/* 인기 상품 섹션 */}
      <section>
        <h2>인기 상품</h2>
        <div className="product-gallery">
          {popularProducts.map(product => (
            <div key={product.id} className="product-card">
              <h3>{product.title}</h3>
              <p>가격: ${product.price}</p>
              <p>{product.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* 최신 상품 섹션 */}
      <section>
        <h2>최신 업로드 상품</h2>
        <div className="product-gallery">
          {latestProducts.map(product => (
            <div key={product.id} className="product-card">
              <h3>{product.title}</h3>
              <p>가격: ${product.price}</p>
              <p>{product.description}</p>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default MainPage;
