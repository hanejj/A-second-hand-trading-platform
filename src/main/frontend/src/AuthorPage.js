import React, { useState, useEffect } from "react";
import axios from "axios";
import "./AuthorPage.css";
import "./CategoryPage.css";
import { useParams, Link, useNavigate } from "react-router-dom";

const AuthorPage = () => {
  const navigate = useNavigate();
  const { userIdx } = useParams(); // URL에서 userIdx 가져오기
  const [authorInfo, setAuthorInfo] = useState(null);
  const [products, setProducts] = useState([]); // 판매 상품 목록
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));

  //신고 모달
  const [showReportModal, setShowReportModal] = useState(false);
  const [reportTitle, setReportTitle] = useState("");
  const [reportContent, setReportContent] = useState("");

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
        const userResponse = await axios.get(
          `http://localhost:8080/user/author/${userIdx}`,
        );
        const sellingResponse = await axios.get(
          `http://localhost:8080/user/author/${userIdx}/selling`,
        );

        setAuthorInfo({
          ...userResponse.data.author,
          image: userResponse.data.author.image
            ? `http://localhost:8080${userResponse.data.author.image}`
            : "default-avatar.png",
        });
        setProducts(sellingResponse.data.products || []);
      } catch (error) {
        console.error("작성자 정보를 가져오는 중 오류 발생:", error);
        setError("작성자 정보를 불러오는 중 문제가 발생했습니다.");
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

  //신고 버튼 클릭 시
  const handleReportClick = () => {
    if (!userIdx) {
      alert("로그인이 필요합니다.");
      navigate("/login"); // 로그인 페이지로 리디렉션
      return;
    }
    setShowReportModal(true); // 신고 모달을 열기
  };

  //신고 모달에서 제출할 때
  const handleReportSubmit = async () => {
    try {
      const response = await axios.post(
        `http://localhost:8080/report/user/${userIdx}`,
        {
          title: reportTitle,
          content: reportContent,
          user_idx: userIdx,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
        },
      );

      if (response.data.code === "1000") {
        alert("신고가 성공적으로 접수되었습니다.");
        setShowReportModal(false);
      } else {
        alert("신고에 실패했습니다: " + response.data.message);
      }
    } catch (error) {
      alert("신고 중 오류가 발생했습니다.");
    }
  };

  const handleBanUser = async () => {
    const isConfirmed = window.confirm(`${authorInfo.nickname} 회원을 영구 정지하시겠습니까?`);
  
    if (isConfirmed) {
      // 로컬 스토리지에서 isAdmin 값을 가져오기
      const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
  
      console.log("영구 정지 인덱스 : ", userIdx);
      axios
        .patch(`http://localhost:8080/admin/ban/${userIdx}`, null, {
          params: { isAdmin }, // Query parameter로 isAdmin 추가
        })
        .then((response) => {
          const data = response.data;
          if (data.code === "1000") {
            alert("회원 영구 정지 성공");
            window.location.reload();
          } else if(data.code==="500"){
            alert("관리자 권한이 없습니다.");
          } 
          else if(data.code==="300"){
            alert("이미 정지된 회원입니다.");
          }
          else {
            alert("회원 영구 정지 실패");
          }
        })
        .catch((error) => {
          console.error("에러 발생:", error);
          alert("회원 영구 정지 요청 중 에러가 발생했습니다.");
        });
    }
  };

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
            <div className="user-message">
              {authorInfo.message || "소개 없음"}
            </div>
          </div>
          <div className="user-stats">
          <div className="manner-box">
            <div className="manner-value">
              {authorInfo.manner_point || 0}
              <span
                style={{
                  color: calculateHeartColor(authorInfo.manner_point || 0),
                  marginLeft: "5px",
                }}
              >
                ♥
              </span>
            </div>
            <div className="manner-label">매너 지수</div>
          </div>
        </div>
        </div>
        {/* 관리자에게는 신고 버튼이 안 보임 */}
        {!isAdmin && (
          <button className="edit-button" onClick={handleReportClick}>
            신고
          </button>
        )}
        {/* 관리자에게만 영구 정지 버튼이 보임 */}
        {isAdmin && (
          <button className="edit-button" onClick={handleBanUser}>
            영구 정지
          </button>
        )}
      </div>

      <div className="tabs-section">
        <h2>{authorInfo.nickname}의 판매 내역</h2>
      </div>

      <div className="product-list">
  {products.length > 0 ? (
    products
      .filter((product) => product.status !== "removed") // 삭제된 상품 제외
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)) // 최신순 정렬
      .map((product) => (
        <Link
          to={`/product/${product.product_idx}`}
          className={`product-card ${
            product.status === "completed" ? "inactive-product" : ""
          }`}
          key={product.product_idx}
        >
          <div
            className={`product-info-box ${
              product.status === "completed" ? "inactive-product-info-box" : ""
            }`} // 상품 정보 박스에 클래스 추가
          >
            <h3>{product.title}</h3>
            <p>{product.price.toLocaleString()}원</p>
            <p>{product.location}</p>
            <p>
              ♡ {product.heart_num} 💬 {product.chat_num}
            </p>
            {product.status === "completed" && (
              <p className="product-status">거래 완료</p>
            )}
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

      {showReportModal && (
        <div className="author-page-modal">
          <div className="author-page-modal-content">
            <button
              className="author-page-close-button"
              onClick={() => setShowReportModal(false)}
            >
              &times;
            </button>
            <h3>유저 신고</h3>
            {/* 신고 제목 레이블과 입력 필드 */}
            <div>
              <label htmlFor="reportTitle" className="report-title-label">
                신고 제목
              </label>
              <input
                id="reportTitle"
                type="text"
                value={reportTitle}
                onChange={(e) => setReportTitle(e.target.value)}
                placeholder="신고 제목을 입력하세요."
                className="report-title-input"
              />
            </div>
            <textarea
              value={reportContent}
              onChange={(e) => setReportContent(e.target.value)}
              placeholder="신고 내용을 입력하세요."
            />
            {/* 제출 버튼 */}
            <button
              onClick={handleReportSubmit}
              disabled={!reportTitle || !reportContent} // 제목과 내용이 모두 채워져야 활성화
            >
              신고 제출
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuthorPage;
