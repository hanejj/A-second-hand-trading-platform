import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Chart } from "react-google-charts";
import axios from "axios";
import "./AdminMainPage.css";

const AdminMainPage = () => {
  const navigate = useNavigate();
  // 데이터 시각화
  const [categoryData, setCategoryData] = useState([["Category", "Count"]]); // 카테고리별 상품 수
  const [chargePointData, setChargePointData] = useState([["날짜", "충전량"]]); // 최신 일주일 포인트 충전량
  const [regionTransactionData, setRegionTransactionData] = useState([["지역", "판매", "구매"]]); // 주요 도시 거래 비율
  const [topHeartProductsData, setTopHeartProductsData] = useState([["상품명", "찜 수"]]); // 찜수가 많은 상품 TOP 5
  const [loading, setLoading] = useState(true);
  
  // 카테고리 이름 매핑
  const categoryTranslation = {
    Electronics: "전자제품",
    Fashion: "패션",
    Books: "도서",
    Furniture: "가구",
    Other: "기타",
  };

  useEffect(() => {
    // 카테고리별 상품 수
    axios
      .get("http://localhost:8080/data/product/category-count")
      .then((response) => {
        if (response.data.code === "1000") {
          const data = response.data.data;

          // Google Chart에 사용할 데이터 포맷으로 변환
          const chartDataArray = [["카테고리", "상품 수"]];
          data.forEach((item) => {
            const translatedCategory =
              categoryTranslation[item.category] || item.category;
            chartDataArray.push([translatedCategory, item.productCount]);
          });
          setCategoryData(chartDataArray);
        } else {
          console.error("데이터 로드 실패:", response.data);
        }
      })
      .catch((error) => {
        console.error("API 호출 중 오류 발생:", error);
      });

    // 최신 일주일 포인트 충전량
    axios
      .get("http://localhost:8080/data/point/charge-last-week")
      .then((response) => {
        if (response.data.code === "1000") {
        const data = response.data.data;
        const chartDataArray = [["날짜", "충전량"]]; // 차트의 헤더 설정
        data.forEach((item) => {
          chartDataArray.push([item.date, item.totalCharge]); // 날짜와 충전량 추가
        });
        setChargePointData(chartDataArray);
        setLoading(false);
      } else {
        console.error("데이터 로드 실패:", response.data);
      }
      })
      .catch((error) => {
        console.error("API 호출 중 오류 발생:", error);
        setLoading(false);
      });
    // 주요 도시에서의 거래 비율
  axios
    .get("http://localhost:8080/data/product/regional-transaction")
    .then((response) => {
      if (response.data.code === "1000") {
        const data = response.data.data;
        const chartDataArray = [["지역", "판매", "구매"]];
        const regions = ['서울', '경기도', '부산', '인천', '광주'];

        // 지역별로 판매/구매 비율을 집계
        regions.forEach((region) => {
          const regionData = data.filter(item => item.region === region);
          const sellCount = regionData.find(item => item.selling === "sell")?.transactionCount || 0;
          const getCount = regionData.find(item => item.selling === "get")?.transactionCount || 0;

          chartDataArray.push([region, sellCount, getCount]);
        });

        setRegionTransactionData(chartDataArray); // 상태 업데이트
      } else {
        console.error("주요 도시 거래 비율 데이터 로드 실패:", response.data);
      }
    })
    .catch((error) => {
      console.error("주요 도시 거래 비율 데이터 로드 실패:", error);
    });
    // 주요 도시에서의 거래 비율
  axios
  .get("http://localhost:8080/data/product/regional-transaction")
  .then((response) => {
    if (response.data.code === "1000") {
      const data = response.data.data;
      const chartDataArray = [["지역", "판매", "구매"]];
      const regions = ['서울', '경기도', '부산', '인천', '광주'];

      // 지역별로 판매/구매 비율을 집계
      regions.forEach((region) => {
        const regionData = data.filter(item => item.region === region);
        const sellCount = regionData.find(item => item.selling === "sell")?.transactionCount || 0;
        const getCount = regionData.find(item => item.selling === "get")?.transactionCount || 0;

        chartDataArray.push([region, sellCount, getCount]);
      });

      setRegionTransactionData(chartDataArray); // 상태 업데이트
    } else {
      console.error("주요 도시 거래 비율 데이터 로드 실패:", response.data);
    }
  })
  .catch((error) => {
    console.error("주요 도시 거래 비율 데이터 로드 실패:", error);
  });
  // 최근 한 달간 찜수 많이 받은 상품 TOP 5
  axios
  .get("http://localhost:8080/data/product/most-hearted")
  .then((response) => {
    if (response.data.code === "1000") {
      const data = response.data.data;
      const chartDataArray = [["상품명", "찜 수", { role: "tooltip", type: "string", p: { html: true } }]];
      data.forEach((item) => {
        const tooltip = `<div><strong>제목:</strong> ${item.title} - ${item.category} <br/><strong>찜 수:</strong> ${item.heartNum}</div>`;
            chartDataArray.push([item.title, item.heartNum, tooltip]);
      });
      setTopHeartProductsData(chartDataArray);
    } else {
      console.error("찜수 많은 상품 데이터 로드 실패:", response.data);
    }
  })
  .catch((error) => {
    console.error("찜수 많은 상품 데이터 로드 실패:", error);
  });
  }, []);

  const handleButtonClick = (section) => {
    if (section === "회원 관리") {
      navigate("/management/user"); // 회원관리 페이지로 이동
    } else if (section === "관리자 관리") {
      navigate("/management/admin");
    } else if (section === "상품 관리") {
      navigate("/category/all");
    } else if (section === "공지사항") {
      navigate("/notices");
    } else if (section === "신고 내역") {
      navigate("/admin/report");
    } else if (section === "문의사항") {
      navigate("/inquiries");
    } else {
      console.log(`${section} 버튼 클릭됨`);
      // 다른 섹션별로 로직 추가 가능
    }
  };

  return (
    <div className="admin-main-page">
      <h1>관리자페이지</h1>
{/* Google Chart 데이터 시각화 */}
<div className="chart-grid">
      {/* 카테고리별 상품 수 */}
      <div className="chart-container">
        {categoryData.length > 0 ? (
          <Chart
            chartType="PieChart"
            data={categoryData}
            options={{
              title: "카테고리별 상품 수",
              titleTextStyle: {
                fontSize: 20, // 타이틀 폰트 크기 설정
                bold: true,
              },
              pieHole: 0.4,
              is3D: false,
              colors: ["#8E44AD", "#D2B4DE", "#F7DC6F", "#76D7C4", "#E59866"],
            }}
            width={"100%"}
            height={"400px"}
          />
        ) : (
          <p>차트 데이터를 로드 중입니다...</p>
        )}
      </div>
      
      {/* 포인트 충전량 합 현황 */}
      <div className="chart-container">
        {!loading ? (
          <Chart
            chartType="LineChart"
            data={chargePointData} // 포인트 충전량 차트 데이터
            options={{
              title: "최근 1주일 동안의 포인트 충전량 합",
              titleTextStyle: {
                fontSize: 20, // 타이틀 폰트 크기 설정
                bold: true,
              },
              hAxis: {
                title: "날짜",
                format: "yyyy-MM-dd",
              },
              vAxis: {
                title: "충전량",
                minValue: 0,
              },
              legend: { position: "none" },
              colors: ["#a472c3"], // 포인트 충전량 라인 색
            }}
            width={"100%"}
            height={"400px"}
          />
        ) : (
          <p>차트 데이터를 로드 중입니다...</p>
        )}
      </div>

      {/* 주요 도시 거래 비율 차트 */}
      <div className="chart-container">
          {regionTransactionData.length > 1 ? (
            <Chart
              chartType="ColumnChart"
              data={regionTransactionData}
              options={{
                title: "주요 도시에서의 거래 비율",
                titleTextStyle: {
                  fontSize: 20,
                  bold: true,
                },
                hAxis: {
                  title: "지역",
                },
                vAxis: {
                  title: "거래 횟수",
                },
                isStacked: true,  // 거래 비율을 누적형으로 표시
                colors: ["#a472c3", "#F2F5A9"], // 판매와 구매에 대한 색상
              }}
              width={"100%"}
              height={"400px"}
            />
          ) : (
            <p>주요 도시 거래 비율 데이터를 로드 중입니다...</p>
          )}
        </div>

        {/* 찜수 많은 상품 TOP 5 차트 */}
        <div className="chart-container">
          {topHeartProductsData.length > 1 ? (
            <Chart
              chartType="BarChart"
              data={topHeartProductsData}
              options={{
                title: "이번 달 인기 상품(찜 기준)",
                titleTextStyle: {
                  fontSize: 20,
                  bold: true,
                },
                hAxis: {
                  title: "찜 수",
                  minValue: 0,
                },
                vAxis: {
                  title: "상품명",
                },
                colors: ["#E2A9F3"],
                tooltip: { isHtml: true },
              }}
              width={"100%"}
              height={"400px"}
            />
          ) : (
            <p>찜수 많은 상품 데이터를 로드 중입니다...</p>
          )}
        </div>


      
    </div>

      <div className="admin-main-page-button-grid">
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("회원 관리")}
        >
          회원관리
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("상품 관리")}
        >
          상품관리
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("관리자 관리")}
        >
          관리자관리
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("공지사항")}
        >
          공지사항
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("문의사항")}
        >
          문의사항
        </button>
        <button
          className="admin-main-page-button"
          onClick={() => handleButtonClick("신고 내역")}
        >
          신고내역
        </button>
      </div>
    </div>
  );
};

export default AdminMainPage;
