import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './ProductUploadPage.css';
import { useNavigate } from 'react-router-dom';

const ProductUploadPage = () => {
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [price, setPrice] = useState('');
  const [category, setCategory] = useState('Electronics');
  const [location, setLocation] = useState('');
  const [keywords, setKeywords] = useState('');
  const [image, setImage] = useState(null);
  const [sellType, setSellType] = useState('sell');
  const [isFormValid, setIsFormValid] = useState(false);

  const handleSellTypeChange = (type) => {
    setSellType(type);
  };

  const handleKeywordsChange = (event) => {
    setKeywords(event.target.value);
  };

  const handleImageChange = (event) => {
    setImage(event.target.files[0]);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData();
    formData.append('title', title);
    formData.append('content', content);
    formData.append('price', price);
    formData.append('category', category);
    formData.append('location', location);
    formData.append('createdAt', new Date().toISOString());
    formData.append('user_idx', 1); // 임시 사용자 ID
    formData.append('nickname', '길동이'); // 임시 닉네임
    formData.append('keyword', keywords.split(','));
    formData.append('sell', sellType);

    if (image) {
      formData.append('image', image);
    }

    try {
      const response = await axios.post('http://localhost:8080/product/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.data.code === '1000') {
        const productIdx = response.data.data.product_idx;
        alert('상품이 성공적으로 업로드되었습니다.');
        navigate(`/product/${productIdx}`);
      } else {
        alert('상품 업로드에 실패했습니다.');
      }
    } catch (error) {
      console.error('상품 업로드 실패:', error);
      alert('상품 업로드 중 오류가 발생했습니다.');
    }
  };

  // 뒤로 가기 버튼 핸들러
  const handleGoBack = () => {
    navigate(-1); // 이전 페이지로 이동
  };

  useEffect(() => {
    // 필수 입력 필드 검증
    const isValid =
      title.trim() !== '' &&
      content.trim() !== '' &&
      price.trim() !== '' &&
      location.trim() !== '' &&
      image !== null;
    setIsFormValid(isValid);
  }, [title, content, price, location, image]);

  return (
    <div className="product-upload-page">
      {/* 뒤로 가기 버튼 */}
      <button className="ProductUploadPage-go-back-button" onClick={handleGoBack}>
        &lt; 뒤로 가기
      </button>
      <h2 className="ProductUploadPage-title">상품 업로드</h2>
      <form onSubmit={handleSubmit} className="ProductUploadPage-upload-form">
        <div className="ProductUploadPage-form-group">
          <label>제목</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            className="ProductUploadPage-input-field"
          />
        </div>
  
        <div className="ProductUploadPage-form-group">
          <label>내용</label>
          <textarea
            type="text"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
            className="ProductUploadPage-input-field"
          />
        </div>
  
        <div className="ProductUploadPage-form-group">
          <label>가격</label>
          <input
            type="number"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            required
            className="ProductUploadPage-input-field"
            placeholder="숫자만 입력하세요"
          />
        </div>
  
        <div className="ProductUploadPage-form-group">
          <label>위치</label>
          <input
            type="text"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            required
            className="ProductUploadPage-input-field"
          />
        </div>
  
        <div className="ProductUploadPage-form-group">
          <label>카테고리</label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            required
            className="ProductUploadPage-input-field"
          >
            <option value="Electronics">전자기기</option>
            <option value="Fashion">의류</option>
            <option value="Furniture">가구</option>
            <option value="Books">도서</option>
            <option value="Other">기타</option>
          </select>
        </div>
  
        <div className="ProductUploadPage-form-group">
          <label>키워드</label>
          <input
            type="text"
            value={keywords}
            onChange={handleKeywordsChange}
            placeholder="쉼표로 구분하여 키워드 입력(키워드1,키워드2,...)"
            className="ProductUploadPage-input-field"
          />
        </div>
  
        <div className="ProductUploadPage-form-group">
          <label>이미지</label>
          <input
            type="file"
            onChange={handleImageChange}
            required
            className="ProductUploadPage-input-field"
          />
        </div>
  
        <div className="ProductUploadPage-form-group">
          <label>판매 종류</label>
          <div className="ProductUploadPage-sell-type-buttons">
            <button
              type="button"
              onClick={() => handleSellTypeChange('sell')}
              className={sellType === 'sell' ? 'active' : ''}
            >
              팔아요
            </button>
            <button
              type="button"
              onClick={() => handleSellTypeChange('get')}
              className={sellType === 'get' ? 'active' : ''}
            >
              구해요
            </button>
          </div>
        </div>
  
        {!isFormValid && (
          <p className="ProductUploadPage-error-message">
            모든 필수 항목을 입력해주세요!
          </p>
        )}
  
        <button
          type="submit"
          className={`ProductUploadPage-submit-button ${
            isFormValid ? 'active' : 'disabled'
          }`}
          disabled={!isFormValid}
        >
          상품 업로드
        </button>
      </form>
    </div>
  );
  
};

export default ProductUploadPage;
