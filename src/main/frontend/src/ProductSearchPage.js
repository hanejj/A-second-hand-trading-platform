import React, { useState } from 'react';
import axios from 'axios';
import './ProductSearchPage.css';
import { Link } from 'react-router-dom';

const ProductSearchPage = () => {
  const [searchTerm, setSearchTerm] = useState(''); // 검색어 상태
  const [products, setProducts] = useState([]); // 검색 결과 상태
  const [errorMessage, setErrorMessage] = useState(''); // 에러 메시지 상태

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value); // 검색어 업데이트
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    axios.get(`http://localhost:8080/product/search`, {
      params: { title: searchTerm },
    })
      .then(response => {
        if (response.data.code === 1000) {
          setProducts(response.data.data); // 검색 결과 업데이트
          setErrorMessage(''); // 에러 메시지 초기화
        } else {
          console.error('상품 검색 실패:', response.data.message);
          setProducts([]);
          setErrorMessage('상품 검색에 실패했습니다.');
        }
      })
      .catch(error => {
        console.error('상품 검색 중 오류 발생:', error);
        setProducts([]); // 결과 초기화
        setErrorMessage('서버와의 연결 중 오류가 발생했습니다.');
      });
  };

  return (
    <div className="product-search-page">
      <h2>상품 검색</h2>
      <form onSubmit={handleSearchSubmit}>
        <input
          type="text"
          value={searchTerm}
          onChange={handleSearchChange}
          placeholder="상품 제목을 입력하세요"
        />
        <button type="submit">검색</button>
      </form>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      <div className="product-list">
        {products.length > 0 ? (
          products.map(product => (
            <Link 
              to={`/product/${product.productIdx}`} 
              className="product-card" 
              key={product.productIdx}
            >
              <div className="product-info">
                <h3>{product.title}</h3>
                <p>가격: {product.price.toLocaleString()}원</p>
                <p>위치: {product.location}</p>
                <p>♡ {product.heartNum} 💬 {product.chatNum}</p>
              </div>
              <img 
                src={`http://localhost:8080/image?image=${product.image}`} 
                alt={product.title} 
                className="product-image"
              />
            </Link>
          ))
        ) : (
          <p>검색 결과가 없습니다.</p>
        )}
      </div>
    </div>
  );
};

export default ProductSearchPage;
