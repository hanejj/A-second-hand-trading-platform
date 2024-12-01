import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './SetProfilePage.css';

const SetProfilePage = () => {
  const navigate = useNavigate();
  const [image, setImage] = useState(null);
  const [message, setMessage] = useState('');

  // 이미지 업로드 핸들러
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImage(file);
    }
  };

  // 설정 완료 핸들러
  const handleComplete = async () => {
    const formData = new FormData();
    if (image) formData.append('image', image);
    formData.append('message', message);

    try {
      const response = await fetch('http://localhost:8080/user/profile', {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        alert('프로필 설정이 완료되었습니다.');
        navigate('/login'); // 로그인 페이지로 이동
      } else {
        alert('프로필 설정 중 오류가 발생했습니다.');
      }
    } catch (error) {
      console.error('프로필 설정 요청 중 에러 발생:', error);
    }
  };

  // 나중에 설정 버튼 핸들러
  const handleSkip = () => {
    navigate('/login'); // 로그인 페이지로 바로 이동
  };

  return (
    <div className="set-profile-container">
      <h1 className="title">프로필 설정</h1>
      <div className="form-group">
        <label htmlFor="image">프로필 이미지</label>
        <input
          type="file"
          id="image"
          accept="image/*"
          onChange={handleImageChange}
        />
        {image && <p>선택된 파일: {image.name}</p>}
      </div>
      <div className="form-group">
        <label htmlFor="message">소개 메시지</label>
        <textarea
          id="message"
          placeholder="자신을 소개하는 메시지를 입력하세요."
          value={message}
          onChange={(e) => setMessage(e.target.value)}
        />
      </div>
      <div className="button-group">
        <button className="complete-button" onClick={handleComplete}>
          완료
        </button>
        <button className="skip-button" onClick={handleSkip}>
          나중에 설정
        </button>
      </div>
    </div>
  );
};

export default SetProfilePage;
