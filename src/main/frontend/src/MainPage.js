import React, { useState, useEffect } from "react";
import axios from "axios";
import "./MainPage.css";
import { useNavigate } from "react-router-dom";

import { Link } from "react-router-dom";

const MainPage = () => {
  const [popularProducts, setPopularProducts] = useState([]);
  const [latestProducts, setLatestProducts] = useState([]);
  const [sessionInfo, setSessionInfo] = useState(null);
  const [isAdmin, setIsAdmin] = useState(null);
  const navigate = useNavigate();
  
  // 페이지 접속 시 API 요청
  useEffect(() => {
    const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
    if (isAdmin) {
      navigate("/admin"); // AdminMainPage로 이동
    } else {
      navigate("/"); // MainPage로 이동
    }
    // 인기순 상품 목록 요청
    const fetchPopularProducts = async () => {
      try {
        const response = await axios.get("http://localhost:8080/product", {
          params: {
            selling: "sell",
            category: "all",
            order: "pop", // 인기순 정렬
            isAdmin: isAdmin,
          },
        });
        if (response.data.code === "1000") {
          const data = response.data.data.slice(0, 9);
          console.log("Popular Products:", data); // 인기 상품 확인
          setPopularProducts(data); // 상위 9개 상품만 저장
        } else {
          console.error("Failed to fetch popular products");
        }
      } catch (error) {
        console.error("Error fetching popular products:", error);
      }
    };

    // 최신순 상품 목록 요청
    const fetchLatestProducts = async () => {
      try {
        const response = await axios.get("http://localhost:8080/product", {
          params: {
            selling: "sell",
            category: "all",
            order: "new", // 최신순 정렬
            isAdmin: isAdmin,
          },
        });
        if (response.data.code === "1000") {
          const data = response.data.data.slice(0, 9);
          console.log("Latest Products:", data); // 최신 상품 확인
          setLatestProducts(data); // 상위 9개 상품만 저장
        } else {
          console.error("Failed to fetch latest products");
        }
      } catch (error) {
        console.error("Error fetching latest products:", error);
      }
    };

    fetchPopularProducts();
    fetchLatestProducts();
  }, [isAdmin, navigate]);

  return (
    <div className="main-page">
      {/* 인기 상품 섹션 */}
      <section>
        <div className="main-page-title">
          <h1>인기 상품</h1>
        </div>
        <div className="product-gallery">
          {popularProducts.map((product) => (
            <div 
            key={product.product_idx} 
            className={`product-wrapper ${
              product.status === "removed" || product.status === "completed" 
                ? "inactive-product" 
                : ""
            }`}
          >
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
                  {/* 상태에 따른 텍스트 표시 */}
                  {product.status === "removed" && <p className="product-status">삭제</p>}
                  {product.status === "completed" && <p className="product-status">거래 완료</p>}
                </div>
                <img
                  src={`http://localhost:8080/image?image=${product.image}`}
                  alt={product.title}
                />
              </Link>
            </div>
          ))}
        </div>
      </section>

      {/* 최신 상품 섹션 */}
      <section>
        <div className="main-page-title">
          <h1>최신 업로드 상품</h1>
        </div>
        <div className="product-gallery">
          {latestProducts.map((product) => (
            <div 
            key={product.product_idx} 
            className={`product-wrapper ${
              product.status === "removed" || product.status === "completed" 
                ? "inactive-product" 
                : ""
            }`}
          >
              <Link
                to={`/product/${product.productIdx}`}
                className="product-link"
              >
                <div className="product-info">
                  <h3>{product.title}</h3>
                  <p>{product.price}원</p>
                  <p>{product.location}</p>
                  <p>
                    ♡ {product.heartNum} 💬 {product.chatNum}
                  </p>
                  {/* 상태에 따른 텍스트 표시 */}
                  {product.status === "removed" && <p className="product-status">삭제</p>}
                  {product.status === "completed" && <p className="product-status">거래 완료</p>}
                </div>
              </Link>
              <Link
                to={`/product/${product.productIdx}`}
                className="product-link"
              >
                <img
                  src={"http://localhost:8080/image?image=" + product.image}
                  alt={product.title}
                />
              </Link>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default MainPage;
