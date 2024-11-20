import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './ProductPage.css';

const ProductPage = () => {
  const { productIdx } = useParams();
  const [product, setProduct] = useState(null);
  const [reviewData, setReviewData] = useState(null);
  const [isHearted, setIsHearted] = useState(false);
  const [isChatting, setIsChatting] = useState(false);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [reviewContent, setReviewContent] = useState('');
  const [reviewImage, setReviewImage] = useState(null);
  const [reviewScore, setReviewScore] = useState(''); // 리뷰 점수 상태

  const handleHeartClick = () => setIsHearted(!isHearted);
  const handleChatClick = () => setIsChatting(true);
  const handleReportClick = () => alert('이 상품을 신고합니다.');
  const handleWriteReviewClick = () => setShowReviewModal(true);

  const handleReviewSubmit = async () => {
    if (!reviewContent) {
      alert('리뷰 내용을 입력하세요.');
      return;
    }

    if (!reviewScore) {
      alert('거래 평가를 선택하세요.');
      return;
    }

    const formData = new FormData();
    formData.append('review', reviewContent);
    formData.append('sellerIndex', 1); // 판매자 ID
    formData.append('buyerIndex', 2); // 구매자 ID
    formData.append('reviewScore', reviewScore);

    if (reviewImage) {
      formData.append('image', reviewImage);
    }

    try {
      const response = await axios.post(
        `http://localhost:8080/product/${productIdx}/review/write`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }
      );

      if (response.data.code === '1000') {
        alert('리뷰가 성공적으로 작성되었습니다.');
        setShowReviewModal(false);
        setReviewContent('');
        setReviewImage(null);
        setReviewScore('');
        fetchReviewData(true);
      } else {
        alert('리뷰 작성에 실패했습니다.');
      }
    } catch (error) {
      console.error('리뷰 작성 중 에러:', error);
      alert('리뷰 작성 중 에러가 발생했습니다.');
    }
  };

  // 카테고리 값을 변환하는 함수
  const getCategoryName = (category) => {
    switch (category) {
      case 'Electronics':
        return '전자기기';
      case 'Fashion':
        return '의류';
      case 'Furniture':
        return '가구';
      case 'Books':
        return '도서';
      case 'Other':
        return '기타';
      default:
        return category;  // 예기치 않은 값은 그대로 반환
    }
  };

  const fetchProductDetails = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/product/${productIdx}`);
      if (response.data.code === '1000') {
        setProduct(response.data.data);
        fetchReviewData(response.data.data.review);
      }
    } catch (error) {
      console.error('Error fetching product details:', error);
    }
  };

  const fetchReviewData = async (hasReview) => {
    if (hasReview) {
      try {
        const response = await axios.get(`http://localhost:8080/product/${productIdx}/review/read`);
        if (response.data.code === '1000') {
          setReviewData(response.data.data);
        }
      } catch (error) {
        console.error('Error fetching review data:', error);
      }
    }
  };

  useEffect(() => {
    fetchProductDetails();
  }, [productIdx]);

  if (!product) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <div className="product-page">
        <img src={"http://localhost:8080/image?image="+product.image} alt={product.title} className="product-page-image" />
        <div className="product-page-info">
          <p>카테고리{' >'} {getCategoryName(product.category)}</p>
          <div className="product-page-product-info-box">
            <h1 className="product-page-h1">{product.title}</h1>
            <p>{product.price.toLocaleString()}원</p>
            <p>{product.location} / {new Date(product.createdAt).toLocaleDateString()}</p>
            <p>{product.status === 'active' ? '판매 중' : '판매 완료'}</p>
            <p>♡ 관심 {product.heartNum} · 💬 채팅 {product.chatNum}</p>
            <div className="product-page-seller-info">
              <p>판매자 {product.sellerName}</p>
            </div>
          </div>
  
          <div className="product-page-buttons">
            <button className={`product-page-heart-button ${isHearted ? 'hearted' : ''}`} onClick={handleHeartClick}>
              {isHearted ? '찜 해제🤍' : '찜🩷'}
            </button>
            <button className="product-page-chat-button" onClick={handleChatClick}>채팅</button>
            <button className="product-page-report-button" onClick={handleReportClick}>신고</button>
            {!reviewData && (
              <button className="product-page-write-review-button" onClick={handleWriteReviewClick}>
                리뷰 작성
              </button>
            )}
          </div>
        </div>
      </div>
  
      {showReviewModal && (
  <div className="product-page-review-modal">
    <div className="product-page-modal-content">
      <button className="product-page-close-button" onClick={() => setShowReviewModal(false)}>&times;</button>
      <h3>리뷰 작성</h3>
      <textarea
        value={reviewContent}
        onChange={(e) => setReviewContent(e.target.value)}
        placeholder="리뷰 내용을 입력하세요."
      />
      <input type="file" accept="image/*" onChange={(e) => setReviewImage(e.target.files[0])} />
      <div>
  <p>거래는 어땠나요?</p>
  <button
    onClick={() => setReviewScore('good')}
    className={reviewScore === 'good' ? 'selected' : ''}
  >
    좋았어요
  </button>
  <button
    onClick={() => setReviewScore('bad')}
    className={reviewScore === 'bad' ? 'selected' : ''}
  >
    아쉬웠어요
  </button>
</div>

      <button onClick={handleReviewSubmit}>리뷰 제출</button>
    </div>
  </div>
)}

  
      {/* 리뷰 섹션: 버튼들 아래로 이동 */}
      {reviewData && (
        <div className="product-page-review-section">
          <h3>리뷰</h3>
          <p><strong>작성일:</strong> {new Date(reviewData.createdAt).toLocaleDateString()}</p>
          <p>{reviewData.review}</p>
          <img src={"http://localhost:8080/image?image="+reviewData.image} alt="review" className="product-page-review-image" />
        </div>
      )}
    </>
  );
  
};

export default ProductPage;
