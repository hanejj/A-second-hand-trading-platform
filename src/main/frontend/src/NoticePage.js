import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./NoticePage.css";

const NoticePage = () => {
  const [notices, setNotices] = useState([]);
  const [isAdmin, setIsAdmin] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const adminStatus = localStorage.getItem("isAdmin");
    const token = localStorage.getItem("token");
    setIsAdmin(adminStatus === "true" && !!token);

    const fetchNotices = async () => {
      try {
        const response = await fetch("http://localhost:8080/notice");
        if (response.ok) {
          const data = await response.json();
          console.log("Fetched notices:", data);
          setNotices(data.data || []);
        } else {
          console.error("Failed to fetch notices");
          setNotices([]);
        }
      } catch (error) {
        console.error("Error fetching notices:", error);
        setNotices([]);
      }
    };

    fetchNotices();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm("이 공지사항을 삭제하시겠습니까?")) {
      try {
        const response = await fetch(`http://localhost:8080/notice/delete/${id}`, {
          method: "DELETE",
        });
        if (response.ok) {
          alert("공지사항이 삭제되었습니다.");
          setNotices(notices.filter((notice) => notice.noticeId !== id));
        } else {
          alert("공지사항 삭제에 실패했습니다.");
        }
      } catch (error) {
        console.error("Error deleting notice:", error);
        alert("서버와의 통신 중 오류가 발생했습니다.");
      }
    }
  };

  const handleGoBack = () => {
    navigate(-1); // 이전 페이지로 이동
  };

  return (
    <div className="notice-page">
      <h1 className="notice-page-title">공지사항 게시판</h1>
      {isAdmin && (
        <div className="notice-page-button-container">
          <button
            className="notice-page-button"
            onClick={() => navigate("/notices/new")}
          >
            글쓰기
          </button>
        </div>
      )}
      <table className="notice-page-table">
        <thead>
          <tr>
            <th className="notice-page-column">순번</th>
            <th className="notice-page-column">제목</th>
            <th className="notice-page-column">글쓴이</th>
            <th className="notice-page-column">작성시간</th>
            {isAdmin && <th className="notice-page-column">관리</th>}
          </tr>
        </thead>
        <tbody>
          {notices
            .sort((a, b) => new Date(b.noticeCreatedAt) - new Date(a.noticeCreatedAt)) // 최신순 정렬
            .map((notice, index) => (
              <tr key={notice.noticeId} className="notice-page-row">
                <td>{notices.length - index}</td> {/* 최신순으로 번호 부여 */}
                <td>
                  <Link to={`/notices/${notice.noticeId}`} className="notice-page-link">
                    {notice.noticeTitle}
                  </Link>
                </td>
                <td>{notice.adminName}</td>
                <td>{new Date(notice.noticeCreatedAt).toLocaleDateString()}</td>
                {isAdmin && (
                  <td>
                    <button
                      className="edit-button"
                      onClick={() => navigate(`/notices/edit/${notice.noticeId}`)}
                    >
                      수정
                    </button>
                    <button
                      className="delete-button"
                      onClick={() => handleDelete(notice.noticeId)}
                    >
                      삭제
                    </button>
                  </td>
                )}
              </tr>
            ))}
        </tbody>
      </table>
    </div>
  );
};

export default NoticePage;
