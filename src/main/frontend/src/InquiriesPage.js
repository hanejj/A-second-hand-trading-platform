import React, { useEffect, useState } from "react";
import axios from "axios";
import "./InquiriesPage.css";
import { Link } from "react-router-dom";

const InquiriesPage = () => {
  const [inquiries, setInquiries] = useState([]);
  const [userIdx, setUserIdx] = useState(null);
  const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));

  const fetchInquiries = async () => {
    try {
      const response = await axios.get("http://localhost:8080/ask", {
        params: {
          isAdmin: isAdmin, 
          userIdx: userIdx,
        },
      });
      if (response.data.code === "1000") {
        setInquiries(response.data.data);
      } else {
        console.error("문의글을 가져오는 데 실패했습니다.");
      }
    } catch (error) {
      console.error("문의글을 가져오는 중 오류 발생:", error);
    }
  };

  // 문의글을 가져오는 함수
  useEffect(() => {
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
            setUserIdx(response.data.user.userIdx);
          }
        })
        .catch((error) => {
          console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        });
    }

    fetchInquiries();
  }, [userIdx,isAdmin]);

  // 공개여부를 판단하는 함수
  const getPublicStatus = (publicFlag) => {
    return publicFlag === true || publicFlag === "y" ? "공개" : "비공개";
  };

  return (
    <div className="inquiries-page-container">
      <h1 className="inquiries-page-title">문의사항 게시판</h1>
      <div className="inquiries-page-button-container">
        {userIdx != null && !isAdmin && (
          <Link to="/inquiry/upload">
            <button className="inquiries-page-button">문의하기</button>
          </Link>
        )}
      </div>
      <table className="inquiries-page-table">
        <thead>
          <tr>
            <th className="inquiries-page-column">순번</th>
            <th className="inquiries-page-column">제목</th>
            <th className="inquiries-page-column">날짜</th>
            <th className="inquiries-page-column">공개여부</th> {/* 공개여부 컬럼 추가 */}
          </tr>
        </thead>
        <tbody>
          {inquiries
            .sort(
              (a, b) =>
                new Date(b.questionCreatedAt) - new Date(a.questionCreatedAt),
            ) // 날짜 기준 최신순 정렬
            .map((inquiry, index) => (
              <React.Fragment key={inquiry.questionIdx}>
                <tr className="inquiries-page-row">
                  <td>{inquiries.length - index}</td> {/* 최신 글은 가장 높은 순번, 오래된 글은 1번에 가까운 순번 */}
                  <td>
                    <Link
                      to={`/Inquiry/question/${inquiry.questionIdx}`}
                      className="inquiries-page-link"
                    >
                      {inquiry.questionTitle}
                    </Link>
                  </td>
                  <td>
                    {new Date(inquiry.questionCreatedAt).toLocaleDateString()}
                  </td>
                  <td>{getPublicStatus(inquiry.questionPublic)}</td> {/* 공개여부 표시 */}
                </tr>
                {inquiry.answer && (
                  <tr className="inquiries-page-answer-row">
                    <td></td>
                    <td className="inquiries-page-answer-title">
                      <Link
                        to={`/Inquiry/answer/${inquiry.answer.answerIdx}`}
                        className="inquiries-page-link"
                      >
                        ㄴ {inquiry.answer.answerTitle}
                      </Link>
                    </td>
                    <td>
                      {new Date(
                        inquiry.answer.answerCreatedAt,
                      ).toLocaleDateString()}
                    </td>
                    <td>{getPublicStatus(inquiry.answer.answerPublic)}</td> {/* 답변의 공개여부 표시 */}
                  </tr>
                )}
              </React.Fragment>
            ))}
        </tbody>
      </table>
    </div>
  );
};

export default InquiriesPage;
