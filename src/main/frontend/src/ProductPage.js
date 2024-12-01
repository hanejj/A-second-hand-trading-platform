import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./ProductPage.css";
import { Link } from "react-router-dom";

const ProductPage = () => {
  const [user, setUser] = useState(null); // 현재 로그인한 사용자 정보 저장
  const { productIdx } = useParams();
  const [product, setProduct] = useState(null);
  const [reviewData, setReviewData] = useState(null);
  const [isHearted, setIsHearted] = useState(false); // 초기 상태
  const [isChatting, setIsChatting] = useState(false);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [reviewContent, setReviewContent] = useState("");
  const [reviewImage, setReviewImage] = useState(null);
  const [reviewScore, setReviewScore] = useState(""); // 리뷰 점수 상태\
  const [userIdx, setUserIdx] = useState(null); // 로그인한 사용자가 있을 때만 userIdx 설정
  const [recommendedProducts, setRecommendedProducts] = useState([]); // 추천 상품
  const navigate = useNavigate();
  const [isAdmin, setIsAdmin] = useState(null);

  //찜 버튼 클릭 시
  const handleHeartClick = async () => {
    if (!userIdx) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    try {
      const endpoint = isHearted
        ? `http://localhost:8080/product/${productIdx}/wish/cancel`
        : `http://localhost:8080/product/${productIdx}/wish`;

      const response = await axios.post(endpoint, null, {
        params: { user_idx: userIdx },
      });

      if (response.data.code === "1000") {
        setIsHearted(!isHearted); // 성공 시 상태 변경
        alert(isHearted ? "찜이 취소되었습니다." : "찜이 추가되었습니다.");
      } else {
        alert("찜 요청에 실패했습니다.");
      }
    } catch (error) {
      console.error("찜 요청 중 오류:", error);
      alert("찜 요청 중 에러가 발생했습니다.");
    }
  };

  //상품 삭제 버튼 클릭 시
  const handleDeleteClick = async () => {
    const confirmation = window.confirm("정말로 이 상품을 삭제하시겠습니까?");
    if (!confirmation) return; // 사용자가 취소하면 삭제하지 않음
  
    try {
      const response = await axios.put(
        `http://localhost:8080/product/${productIdx}/delete`
      );
  
      if (response.data.code === "1000") {
        // 삭제 완료 메시지 표시 및 페이지 이동
        alert("상품 삭제가 완료되었습니다.");
        navigate("/"); // 메인 페이지로 리디렉션
      } else {
        alert("상품 삭제에 실패했습니다.");
      }
    } catch (error) {
      console.error("상품 삭제 중 오류 발생:", error);
      alert("상품 삭제 중 오류가 발생했습니다.");
    }
  };
  

  //채팅 버튼 클릭 시
  const handleChatClick = () => {
    if (!userIdx) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    setIsChatting(true);
  };

  //신고 버튼 클릭 시
  const handleReportClick = () => {
    if (!userIdx) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    // 상품 신고 기능 연결 필요
  };

  // 리뷰 작성 버튼 클릭 시
  const handleWriteReviewClick = () => setShowReviewModal(true);

  // 리뷰 작성하기
  const handleReviewSubmit = async () => {
    if (!reviewContent) {
      alert("리뷰 내용을 입력하세요.");
      return;
    }

    if (!reviewScore) {
      alert("거래 평가를 선택하세요.");
      return;
    }

    const formData = new FormData();
    formData.append("review", reviewContent);
    formData.append("partnerIndex", user.userIdx);

    // `sellerIndex`는 현재 로그인한 사용자(user.userIdx)
    formData.append("sellerIndex", user.userIdx);
    // `buyerIndex`는 product.partner_idx와 동일
    formData.append("buyerIndex", product.partnerIdx);
    formData.append("reviewScore", reviewScore);

    if (reviewImage) {
      formData.append("image", reviewImage);
    }

    try {
      const response = await axios.post(
        `http://localhost:8080/product/${productIdx}/review/write`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        },
      );

      if (response.data.code === "1000") {
        alert("리뷰가 성공적으로 작성되었습니다.");
        setShowReviewModal(false);
        setReviewContent("");
        setReviewImage(null);
        setReviewScore("");
        fetchReviewData(true);
      } else {
        alert("리뷰 작성에 실패했습니다.");
      }
    } catch (error) {
      console.error("리뷰 작성 중 에러:", error);
      alert("리뷰 작성 중 에러가 발생했습니다.");
    }
  };

  // 카테고리 이름 한글화
  const getCategoryName = (category) => {
    switch (category) {
      case "Electronics":
        return "전자기기";
      case "Fashion":
        return "의류";
      case "Furniture":
        return "가구";
      case "Books":
        return "도서";
      case "Other":
        return "기타";
      default:
        return category;
    }
  };

  // 상품 정보 가져오기
  const fetchProductDetails = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/product/${productIdx}`,
        {
          params: { user_idx: userIdx }, // user_idx 추가
        },
      );
      if (response.data.code === "1000") {
        setProduct(response.data.data.product); // 상품 정보
        setRecommendedProducts(response.data.data.recommendedProducts); // 추천 상품
        setIsHearted(response.data.data.product.isHearted || false); // 초기 찜 상태 설정
        // 상품 정보 변경 시 리뷰 데이터 초기화
        setReviewData(null); // 리뷰 데이터 초기화
        fetchReviewData(response.data.data.product.review); // 리뷰 데이터를 새로 가져오기
      }
    } catch (error) {
      console.error("Error fetching product details:", error);
    }
  };

  // 리뷰 정보 가져오기
  const fetchReviewData = async (hasReview) => {
    if (hasReview) {
      try {
        const response = await axios.get(
          `http://localhost:8080/product/${productIdx}/review/read`,
        );
        if (response.data.code === "1000") {
          setReviewData(response.data.data);
        }
      } catch (error) {
        console.error("Error fetching review data:", error);
      }
    }
  };

  useEffect(() => {
    setIsAdmin(JSON.parse(localStorage.getItem("isAdmin")));
    // 현재 로그인한 사용자 정보 가져오기
    const token = localStorage.getItem("token");
    if (token) {
      axios
        .get("http://localhost:8080/user/profile", {
          headers: {
            Authorization: "Bearer " + token, // JWT 토큰을 Authorization 헤더에 추가
          },
        })
        .then((response) => {
          if (response.data && response.data.user) {
            setUser(response.data.user); // 사용자 정보 저장
            setUserIdx(response.data.user.userIdx);
            console.log("UserIdx:", response.data.user.userIdx);
          }
        })
        .catch((error) => {
          console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        });
    }

    //상품 정보 가져오기
    fetchProductDetails();
  }, [userIdx, productIdx]);

  if (!product) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <div className="product-page">
        <img
          src={"http://localhost:8080/image?image=" + product.image}
          alt={product.title}
          className="product-page-image"
        />
        <div className="product-page-info">
          <p>
            카테고리{" >"} {getCategoryName(product.category)}
          </p>
          <div className="product-page-product-info-box">
            <h1 className="product-page-h1">{product.title}</h1>
            <p>{product.price.toLocaleString()}원</p>
            <p>
              {product.location} /{" "}
              {new Date(product.createdAt).toLocaleDateString()}
            </p>
            <p>{product.status === "active" ? "거래 중" : "거래 완료"}</p>
            <p>
              ♡ 관심 {product.heartNum} · 💬 채팅 {product.chatNum}
            </p>
            <div className="product-page-seller-info">
              <p>작성자 {product.writerName}</p>
            </div>
          </div>

          <div className="product-page-buttons">
            <button
              className={`product-page-heart-button ${isHearted ? "hearted" : ""}`}
              onClick={handleHeartClick}
            >
              {isHearted ? "찜 해제🤍" : "찜🩷"}
            </button>
            <button
              className="product-page-chat-button"
              onClick={handleChatClick}
            >
              채팅
            </button>
            <button
              className="product-page-report-button"
              onClick={handleReportClick}
            >
              신고
            </button>
            {/*현재 로그인한 유저의 게시글이거나 관리자인 경우 삭제 가능*/}
            {(user && userIdx === product.writerIdx) || isAdmin === true ? (
              <button
                className="product-page-report-button"
                onClick={handleDeleteClick}
              >
                삭제
              </button>
            ) : null}
            {/*현재 로그인 유저가 거래 상대방일 때이고, 거래가 이미 완료된 상태일 때만 리뷰 작성 버튼이 보임*/}
            {!reviewData &&
              user &&
              userIdx === product.partnerIdx &&
              product.status === "completed" && (
                <button
                  className="product-page-write-review-button"
                  onClick={handleWriteReviewClick}
                >
                  리뷰 작성
                </button>
              )}
          </div>
        </div>
      </div>
      {/* 추천 상품 섹션 */}
      <div className="recommended-gallery-title">
        <h3>키워드 기반 추천 상품</h3>
      </div>
      <div className="recommended-gallery">
        {recommendedProducts.length > 0 ? (
          recommendedProducts.map((product) => (
            <div key={product.product_idx}>
              <Link
                to={`/product/${product.productIdx}`}
                className="product-card"
              >
                <div className="product-info">
                  <h3>{product.title}</h3>
                  <p>{product.price}원</p>
                  <p>{product.location}</p>
                  <p>
                    ♡ {product.heartNum} 💬 {product.chatNum}
                  </p>
                </div>
                <img
                  src={`http://localhost:8080/image?image=${product.image}`}
                  alt={product.title}
                />
              </Link>
            </div>
          ))
        ) : (
          <p>추천 상품이 없습니다.</p>
        )}
      </div>

      {showReviewModal && (
        <div className="product-page-review-modal">
          <div className="product-page-modal-content">
            <button
              className="product-page-close-button"
              onClick={() => setShowReviewModal(false)}
            >
              &times;
            </button>
            <h3>리뷰 작성</h3>
            <textarea
              value={reviewContent}
              onChange={(e) => setReviewContent(e.target.value)}
              placeholder="리뷰 내용을 입력하세요."
            />
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setReviewImage(e.target.files[0])}
            />
            <div>
              <p>거래는 어땠나요?</p>
              <button
                onClick={() => setReviewScore("good")}
                className={reviewScore === "good" ? "selected" : ""}
              >
                좋았어요
              </button>
              <button
                onClick={() => setReviewScore("bad")}
                className={reviewScore === "bad" ? "selected" : ""}
              >
                아쉬웠어요
              </button>
            </div>

            <button onClick={handleReviewSubmit}>리뷰 제출</button>
          </div>
        </div>
      )}
      {reviewData && (
        <div className="product-page-review-section">
          <h3>리뷰</h3>
          <p>
            <strong>작성일:</strong>{" "}
            {new Date(reviewData.createdAt).toLocaleDateString()}
          </p>
          <p>{reviewData.review}</p>
          <img
            src={"http://localhost:8080/image?image=" + reviewData.image}
            alt="review"
            className="product-page-review-image"
          />
        </div>
      )}
    </>
  );
};

export default ProductPage;
