import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './NoticeWritePage.css';

const NoticeWritePage = () => {
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    image: null,
  });
  const navigate = useNavigate();

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

    // 관리자 권한 확인
    const isAdmin = localStorage.getItem('isAdmin') === 'true';
    if (!isAdmin) {
      alert('권한이 없습니다.');
      return;
    }

    const formDataToSend = new FormData();
    formDataToSend.append('title', formData.title);
    formDataToSend.append('content', formData.content);
    if (formData.image) {
      formDataToSend.append('image', formData.image);
    }

    try {
      const response = await fetch('http://localhost:8080/notice/upload', {
        method: 'POST',
        body: formDataToSend,
      });

      if (response.ok) {
        alert('공지사항이 성공적으로 작성되었습니다.');
        navigate('/notices'); // 공지사항 목록 페이지로 이동
      } else {
        alert('공지사항 작성에 실패했습니다.');
      }
    } catch (error) {
      console.error('Error submitting notice:', error);
      alert('서버와의 통신 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="notice-write-page">
      <h2>공지사항 작성</h2>
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
        <button type="submit" className="submit-button">등록</button>
      </form>
    </div>
  );
};

export default NoticeWritePage;
