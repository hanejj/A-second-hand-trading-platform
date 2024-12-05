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

    const token = localStorage.getItem('token');
    if (!token) {
      alert('권한이 없습니다. 다시 로그인 해주세요.');
      navigate('/login');
      return;
    }

    // 관리자 정보 가져오기
    let adminId;
    try {
      const profileResponse = await fetch('http://localhost:8080/admin/profile?isAdmin=true', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!profileResponse.ok) {
        throw new Error(`HTTP error! status: ${profileResponse.status}`);
      }

      const profileData = await profileResponse.json();
      console.log('Profile Data:', profileData); // 응답 데이터 확인용 로그

      // Profile Data 응답 데이터 구조를 기반으로 수정
if (profileData.code === "1000" && profileData.data && profileData.data.admin_idx) {
  adminId = profileData.data.admin_idx; // admin_idx에서 관리자 ID 추출
} else {
  throw new Error('관리자 정보를 가져오는 데 실패했습니다.');
}

    } catch (error) {
      console.error('Error fetching admin profile:', error.message);
      alert(error.message || '관리자 정보를 가져오는 중 오류가 발생했습니다.');
      return;
    }

    // 공지사항 작성 요청
    const formDataToSend = new FormData();
    formDataToSend.append('title', formData.title);
    formDataToSend.append('content', formData.content);
    formDataToSend.append('adminId', adminId); // adminId를 추가
    if (formData.image) {
      formDataToSend.append('image', formData.image);
    }

    try {
      const noticeResponse = await fetch('http://localhost:8080/notice/upload', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formDataToSend,
      });

      const noticeResult = await noticeResponse.json();
      console.log('Notice Response:', noticeResult); // 응답 데이터 확인용 로그

      if (noticeResult.code === 1000) {
        alert('공지사항이 성공적으로 작성되었습니다.');
        navigate('/notices'); // 공지사항 목록 페이지로 이동
      } else {
        alert(noticeResult.message || '공지사항 작성에 실패했습니다.');
      }
    } catch (error) {
      console.error('Error submitting notice:', error.message);
      alert('공지사항 작성 중 오류가 발생했습니다.');
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
