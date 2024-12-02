import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './NoticeEditPage.css';

const NoticeEditPage = () => {
  const { id } = useParams(); // URL에서 공지사항 ID 가져옴
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    image: null,
  });
  const [loading, setLoading] = useState(true); 
  const [error, setError] = useState(null); 
  const navigate = useNavigate();

  useEffect(() => {
    // 공지사항 데이터 가져오기
    const fetchNotice = async () => {
      try {
        const response = await fetch(`http://localhost:8080/notice/${id}`);
        if (response.ok) {
          const result = await response.json();
          console.log('Fetched notice:', result); // 데이터 확인용
          if (result.code === 1000 && result.data) {
            setFormData({
              title: result.data.noticeTitle || '',
              content: result.data.noticeContent || '',
              image: null, // 이미지는 수정 시 새로 업로드해야 함
            });
          } else {
            setError(result.message || '공지사항 데이터를 불러오는 데 실패했습니다.');
          }
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

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleImageChange = (e) => {
    setFormData((prevData) => ({
      ...prevData,
      image: e.target.files[0],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formDataToSend = new FormData();
    formDataToSend.append('title', formData.title);
    formDataToSend.append('content', formData.content);
    if (formData.image instanceof File) {
      formDataToSend.append('image', formData.image);
    }

    try {
      const response = await fetch(`http://localhost:8080/notice/edit/${id}`, {
        method: 'PUT', // 수정 요청
        body: formDataToSend,
      });

      const result = await response.json();

      if (result.code === 1000) {
        alert(result.message); // 성공 메시지
        navigate(`/notices/${id}`);
      } else {
        alert(result.message || '공지사항 수정에 실패했습니다.'); // 실패 메시지
      }
    } catch (error) {
      alert('서버와의 통신 중 오류가 발생했습니다.');
    }
  };

  if (loading) {
    return <div>로딩 중...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className="notice-edit-container">
      <h2>공지사항 수정</h2>
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="title">제목</label>
          <input
            type="text"
            id="title"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="content">내용</label>
          <textarea
            id="content"
            name="content"
            value={formData.content}
            onChange={handleChange}
            required
          ></textarea>
        </div>
        <div className="input-group">
          <label htmlFor="image">이미지</label>
          <input
            type="file"
            id="image"
            name="image"
            accept="image/*"
            onChange={handleImageChange}
          />
        </div>
        <button type="submit" className="submit-button">수정 완료</button>
      </form>
    </div>
  );
};

export default NoticeEditPage;
