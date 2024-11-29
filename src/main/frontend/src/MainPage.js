import React, { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import './MainPage.css';

const MainPage = () => {
  const navigate = useNavigate();
  const [popularProducts, setPopularProducts] = useState([]);
  const [latestProducts, setLatestProducts] = useState([]);

  
  //개발환경에서 임의로 관리자 상태로 설정
  // isAdmin 값을 true로 로컬 스토리지에 저장
  localStorage.setItem("isAdmin", JSON.stringify(true));
  const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
  useEffect(() => {
    if (isAdmin) {
      navigate("/admin"); // AdminMainPage로 이동
    }

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
  }, [isAdmin, navigate]);

  return (
    <div className="main-page">

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
