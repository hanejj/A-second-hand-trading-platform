import React from 'react';
import { useParams, Link } from 'react-router-dom';
import './NoticeDetailPage.css';

const NoticeDetailPage = () => {
  const { id } = useParams(); // URL에서 ID를 가져옴

  // 공지사항 데이터 (임의로 작성된 예시)
  const notices = [
    {
      id: 1,
      title: '첫 번째 공지사항',
      author: '관리자',
      date: '2024-10-13',
      content: '여기는 첫 번째 공지사항의 내용이 들어갑니다. 이 공지사항은 사용자가 보다 나은 경험을 할 수 있도록 도와줄 것입니다.',
    },
    {
      id: 2,
      title: '두 번째 공지사항',
      author: '관리자',
      date: '2024-10-14',
      content: '여기는 두 번째 공지사항의 내용이 들어갑니다. 신규 업데이트 및 기능 추가에 대한 정보가 포함되어 있습니다.',
    },
    {
      id: 3,
      title: '세 번째 공지사항',
      author: '관리자',
      date: '2024-10-15',
      content: '여기는 세 번째 공지사항의 내용입니다. 저희는 항상 사용자 의견을 듣고 있습니다.',
    },
  ];

  // ID에 해당하는 공지사항 찾기
  const notice = notices.find(notice => notice.id === parseInt(id));

  // 해당 공지사항이 없을 경우 처리
  if (!notice) {
    return <div>공지사항을 찾을 수 없습니다.</div>;
  }

  return (
    <div className="notice-detail-container">
      {/* 공지사항 목록 버튼 */}
      <div className="back-button">
        <Link to="/notices">
          <button>공지사항 목록</button>
        </Link>
      </div>

      {/* 공지사항 제목, 작성자, 작성일 */}
      <div className="notice-info">
        <h2 className="notice-title">{notice.title}</h2>
        <p className="notice-meta">
          작성자: {notice.author} | 작성일: {notice.date}
        </p>
      </div>

      {/* 공지사항 내용 */}
      <div className="notice-content">
        <p>{notice.content}</p>
      </div>

      {/* 수정 및 삭제 버튼 */}
      <div className="action-buttons">
        <button className="edit-button">수정</button>
        <button className="delete-button">삭제</button>
      </div>
    </div>
  );
};

export default NoticeDetailPage;
