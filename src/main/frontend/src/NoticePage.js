import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './NoticePage.css';

const NoticePage = () => {
  const [notices, setNotices] = useState([]);

  useEffect(() => {
    // 임의의 공지사항 데이터
    const noticeData = [
      { id: 1, title: '첫 번째 공지사항', author: '관리자', date: '2024-10-13' },
      { id: 2, title: '두 번째 공지사항', author: '관리자', date: '2024-10-14' },
      { id: 3, title: '세 번째 공지사항', author: '관리자', date: '2024-10-15' },
    ];

    setNotices(noticeData);
  }, []);

  return (
    <div className="notice-page">

      {/* 공지사항 목록 */}
      <h2>공지사항 목록</h2>
      <table>
        <thead>
          <tr>
            <th>No</th>
            <th>제목</th>
            <th>글쓴이</th>
            <th>작성시간</th>
          </tr>
        </thead>
        <tbody>
          {notices.map(notice => (
            <tr key={notice.id}>
              <td>{notice.id}</td>
              <td>
                <Link to={`/notices/${notice.id}`}>{notice.title}</Link>
              </td>
              <td>{notice.author}</td>
              <td>{notice.date}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 검색 및 글쓰기 기능 */}
      <div className="search-section">
        <input type="text" placeholder="검색어를 입력하세요..." />
        <button>검색</button>
        <Link to="/notices/new" className="write-button">글쓰기</Link>
      </div>
    </div>
  );
};

export default NoticePage;
