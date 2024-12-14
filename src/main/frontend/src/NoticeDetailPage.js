import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import './NoticeDetailPage.css';

const NoticeDetailPage = () => {
  const { id } = useParams();
  const [notice, setNotice] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const adminStatus = localStorage.getItem('isAdmin');
    const token = localStorage.getItem('token'); 
    setIsAdmin(adminStatus === 'true' && !!token);

    const fetchNotice = async () => {
      try {
        const response = await fetch(`http://localhost:8080/notice/${id}`);
        if (response.ok) {
          const data = await response.json();
          console.log('Fetched Notice:', data);
          setNotice(data.data || data);
        } else {
          setError('공지사항을 불러오는 데 실패했습니다.');
        }
      } catch (error) {
        setError('서버와 통신 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchNotice();
  }, [id]);

  const handleDelete = async () => {
    if (window.confirm('이 공지사항을 삭제하시겠습니까?')) {
      try {
        const response = await fetch(`http://localhost:8080/notice/delete/${id}`, {
          method: 'DELETE',
        });
        if (response.ok) {
          alert('공지사항이 삭제되었습니다.');
          navigate('/notices');
        } else {
          alert('공지사항 삭제에 실패했습니다.');
        }
      } catch (error) {
        alert('서버와 통신 중 오류가 발생했습니다.');
      }
    }
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (!notice) {
    return <div>공지사항을 찾을 수 없습니다.</div>;
  }

  return (
    <div className="notice-detail-container">
      <div className="back-button">
        <Link to="/notices">
          <button>공지사항 목록</button>
        </Link>
      </div>

      <div className="notice-info">
        <h2 className="notice-title">{notice.noticeTitle || '제목 없음'}</h2>
        <p className="notice-meta">
          작성자: {notice.adminName || '알 수 없음'} | 작성일: {notice.noticeCreatedAt ? new Date(notice.noticeCreatedAt).toLocaleString() : '알 수 없음'}
        </p>
      </div>

      <div className="notice-content">
        <p>{notice.noticeContent || '내용 없음'}</p>
        {notice.noticeImage ? (
  <img
    src={"http://localhost:8080/image?image=" + notice.noticeImage}
    alt="noticeImage"
  />
) : (
  <p> </p>
)}
      </div>

      {isAdmin && (
        <div className="action-buttons">
          <button className="edit-button" onClick={() => navigate(`/notices/edit/${id}`)}>수정</button>
          <button className="delete-button" onClick={handleDelete}>삭제</button>
        </div>
      )}
    </div>
  );
};

export default NoticeDetailPage;