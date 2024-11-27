import React, { useState } from 'react';
import axios from 'axios';
import './ProductSearchPage.css';

const ProductSearchPage = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [products, setProducts] = useState([]);

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    axios.get(`http://localhost:8080/product/search`, {
      params: {
        title: searchTerm,
      },
    })
      .then(response => {
        setProducts(response.data);
      })
      .catch(error => {
        console.error('상품 검색 중 오류 발생:', error);
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
      <div className="product-list">
        {products.length > 0 ? (
          products.map(product => (
            <div key={product.product_idx} className="product-card">
              <h3>{product.title}</h3>
              <p>{product.content}</p>
              <p>가격: {product.price}원</p>
              <p>위치: {product.location}</p>
            </div>
          ))
        ) : (
          <p>검색 결과가 없습니다.</p>
        )}
      </div>
    </div>
  );
};

export default ProductSearchPage;
