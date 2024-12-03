import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './AuthorPage.css'; 
import { useParams, Link } from 'react-router-dom';

const AuthorPage = () => {
  const { userIdx } = useParams(); // URL에서 userIdx 가져오기
  const [authorInfo, setAuthorInfo] = useState(null);
  const [products, setProducts] = useState([]); // 판매 상품 목록
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 매너 지수에 따라 하트 색상 계산
  const calculateHeartColor = (mannerPoint) => {
    const maxColor = { r: 139, g: 0, b: 139 }; // 보라색 (#800080)
    const minColor = { r: 240, g: 240, b: 240 }; // 흰색 (#ffffff)

    const ratio = mannerPoint / 100;
    const r = Math.round(minColor.r + (maxColor.r - minColor.r) * ratio);
    const g = Math.round(minColor.g + (maxColor.g - minColor.g) * ratio);
    const b = Math.round(minColor.b + (maxColor.b - minColor.b) * ratio);

    return `rgb(${r}, ${g}, ${b})`;
  };

  useEffect(() => {
    const getAuthorProfile = async () => {
      setLoading(true);
      try {
        const userResponse = await axios.get(`http://localhost:8080/user/author/${userIdx}`);
        const sellingResponse = await axios.get(`http://localhost:8080/user/author/${userIdx}/selling`);

        setAuthorInfo({
          ...userResponse.data.author,
          image: userResponse.data.author.image
            ? `http://localhost:8080${userResponse.data.author.image}`
            : 'default-avatar.png',
        });
        setProducts(sellingResponse.data.products || []);
      } catch (error) {
        console.error('작성자 정보를 가져오는 중 오류 발생:', error);
        setError('작성자 정보를 불러오는 중 문제가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    getAuthorProfile();
  }, [userIdx]);

  if (loading) {
    return <div>로딩 중...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className="author-page">
      <div className="header-section">
        <div className="user-info">
          <div className="user-avatar">
            <img
              src={authorInfo.image}
              alt={`${authorInfo.name}의 프로필 사진`}
              className="avatar-img"
            />
          </div>
          <div className="user-details">
            <div className="user-nickname">{authorInfo.nickname}</div>
            <div className="user-message">{authorInfo.message || '소개 없음'}</div>
          </div>
        </div>

        <div className="user-stats">
          <div className="manner-box">
            <div className="manner-value">
              {authorInfo.manner_point || 0}
              <span
                style={{
                  color: calculateHeartColor(authorInfo.manner_point || 0),
                  marginLeft: '5px',
                }}
              >
                ♥
              </span>
            </div>
            <div className="manner-label">매너 지수</div>
          </div>
        </div>

        <button className="edit-button">신고</button>
      </div>

      <div className="tabs-section">
        <h2>{authorInfo.nickname}의 판매 내역</h2>
      </div>

      <div className="product-list">
        {products.length > 0 ? (
          [...products]
            .filter((product) => product.status !== 'removed') // 삭제된 상품 제외
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)) // 최신순 정렬
            .map((product) => (
              <Link
                to={`/product/${product.product_idx}`}
                className="product-card"
                key={product.product_idx}
              >
                <div className="product-info">
                  <h3>{product.title}</h3>
                  <p>{product.price.toLocaleString()}원</p>
                  <p>{product.location}</p>
                  <p>
                    ♡ {product.heart_num} 💬 {product.chat_num}
                  </p>
                </div>
                <img
                  src={`http://localhost:8080/image?image=${product.image}`}
                  alt={product.title}
                  className="product-image"
                />
              </Link>
            ))
        ) : (
          <div>판매하는 상품이 없습니다.</div>
        )}
      </div>
    </div>
  );
};

export default AuthorPage;
