import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './ProductUploadPage.css';
import { useParams, useNavigate } from 'react-router-dom';

const ProductEditPage = () => {
  const navigate = useNavigate();
  const { productIdx } = useParams();
  const [product, setProduct] = useState(null);
  const [user, setUser] = useState(null); // 현재 로그인한 사용자 정보 저장
  const [userIdx, setUserIdx] = useState(null); 
  const [userNickname, setUserNickname] = useState(null); 
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
    formData.append('user_idx', userIdx); 
    formData.append('nickname', userNickname);
    formData.append('keyword', keywords.split(','));
    formData.append('sell', sellType);
  
    if (image) {
      formData.append('image', image);
    }
  
    try {
      // /product/{productIdx}/edit 엔드포인트로 PUT 요청
      const response = await axios.put(`http://localhost:8080/product/${productIdx}/edit`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
  
      if (response.data.code === '1000') {
        alert('상품이 성공적으로 수정되었습니다.');
        navigate(`/product/${productIdx}`); // 수정 후 해당 상품 페이지로 이동
      } else {
        alert('상품 수정에 실패했습니다.');
      }
    } catch (error) {
      console.error('상품 수정 실패:', error);
      alert('상품 수정 중 오류가 발생했습니다.');
    }
  };
  

  // 뒤로 가기 버튼 핸들러
  const handleGoBack = () => {
    navigate(-1); // 이전 페이지로 이동
  };

  // 상품 정보 가져오기
  const fetchProductDetails = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/product/${productIdx}`,
        {
          params: {
            user_idx: userIdx,
            isAdmin: false,
          },
        },
      );
      if (response.data.code === "1000") {
        const productData = response.data.data.product;

        // 상품 정보 설정
        setProduct(productData);
        // 각 필드의 초기값 설정
        setTitle(productData.title);
        setContent(productData.content);
        setPrice(productData.price.toString());
        setCategory(productData.category);
        setLocation(productData.location);
        setSellType(productData.selling);
      } else if (response.data.code === "500") {
        // 접근 불가 상품 처리
        alert("접근 불가 상품입니다.");
        navigate("/"); // 메인 페이지로 이동
      } else {
        console.error("Unexpected response code:", response.data.code);
        alert("상품 정보를 불러오는 중 문제가 발생했습니다.");
      }
    } catch (error) {
      console.error("Error fetching product details:", error);
    }
  };


  // 상품 정보를 가져오는 useEffect
  useEffect(() => {
    fetchProductDetails();
  }, []);

  useEffect(() => {
    // 현재 로그인 정보 가져오기
    const token = localStorage.getItem("token");
    if (token) {
      axios
        .get("http://localhost:8080/user/profile", {
          headers: {
            Authorization: "Bearer " + token,
          },
        })
        .then((response) => {
          if (response.data && response.data.user) {
            setUser(response.data.user);
            setUserIdx(response.data.user.userIdx);
            setUserNickname(response.data.user.nickname);
          }
        })
        .catch((error) => {
          console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        });
    }
  }, []);

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
      <h2 className="ProductUploadPage-title">상품 수정</h2>
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
          {product?.image && (
            <div>
              <img
                src={"http://localhost:8080/image?image="+product.image}
                alt="Uploaded Product"
                style={{ width: '200px', marginBottom: '10px' }}
              />
            </div>
          )}
          <input
            type="file"
            onChange={handleImageChange}
            className="ProductUploadPage-input-field"
          />
        </div>
  
        <div className="ProductUploadPage-form-group">
          <label>거래 종류</label>
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
          상품 수정 제출
        </button>
      </form>
    </div>
  );
  
};

export default ProductEditPage;
