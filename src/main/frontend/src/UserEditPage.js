import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import './UserEditPage.css';

const UserEditPage = () => {
  const { email } = useParams();
  const navigate = useNavigate();
  const [userData, setUserData] = useState({
    name: '',
    phone: '',
    nickname: '',
    message: '',
    location: '',
    passwd: '',
    confirmPassword: '',
    image: '',
  });
  const [imageFile, setImageFile] = useState(null);

  // 사용자 데이터 가져오기
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    axios.get(`http://localhost:8080/user/${email}/get`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((response) => {
        const data = response.data.user;
        setUserData({
          name: data.name || '',
          phone: data.phone || '',
          nickname: data.nickname || '',
          message: data.message || '',
          location: data.location || '',
          passwd: '',
          confirmPassword: '',
          image: data.image || '',
        });
      })
      .catch((error) => {
        console.error('사용자 정보를 가져오는 중 오류 발생:', error);
        alert('사용자 정보를 가져오는 중 문제가 발생했습니다.');
      });
  }, [email, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData((prevState) => ({ ...prevState, [name]: value }));
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    setImageFile(file);
    setUserData((prevState) => ({ ...prevState, image: URL.createObjectURL(file) }));
  };

  const handleSubmit = () => {
    if (userData.passwd && userData.passwd !== userData.confirmPassword) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    const token = localStorage.getItem('token');
    const formData = new FormData();
    formData.append('user', JSON.stringify({
      name: userData.name,
      phone: userData.phone,
      nickname: userData.nickname,
      message: userData.message,
      location: userData.location,
      passwd: userData.passwd || '',
    }));
    if (imageFile) {
      formData.append('image', imageFile);
    }

    axios.put(`http://localhost:8080/user/${email}/edit`, formData, {
      headers: { 'Content-Type': 'multipart/form-data', Authorization: `Bearer ${token}` },
    })
      .then(() => {
        alert('정보가 성공적으로 수정되었습니다.');
        navigate('/mypage');
      })
      .catch((error) => {
        console.error('사용자 정보를 업데이트하는 중 오류 발생:', error);
        alert('정보 수정 중 문제가 발생했습니다.');
      });
  };

  return (
    <div className="user-edit-page">
      <h2 className="user-edit-title">내 정보 수정</h2>
      <div className="edit-form-container">
        <div className="edit-avatar-section">
          <div className="avatar-image-container">
            <img src={userData.image || 'default-avatar.png'} alt="User Avatar" className="avatar-img" />
            <label htmlFor="image-upload" className="image-upload-label">
              사진 변경
            </label>
            <input 
              type="file" 
              id="image-upload" 
              accept="image/*" 
              onChange={handleImageChange} 
              className="image-upload-input"
            />
            <button className="image-remove-button" onClick={() => setUserData(prevState => ({ ...prevState, image: '' }))}>현재 사진 삭제</button>
          </div>
          <div className="nickname-section">
            <span className="nickname-label">닉네임</span>
            <input
              type="text"
              name="nickname"
              value={userData.nickname}
              onChange={handleChange}
              className="nickname-input"
            />
          </div>
          <div className="message-section">
            <span className="message-label">소개글</span>
            <textarea
              name="message"
              value={userData.message}
              onChange={handleChange}
              className="message-input"
              placeholder="소개글을 작성해주세요."
            />
          </div>
        </div>
        <div className="edit-info-section">
          <div className="input-group">
            <label>새 비밀번호</label>
            <input
              type="password"
              name="passwd"
              value={userData.passwd}
              onChange={handleChange}
              className="input-field"
            />
          </div>
          <div className="input-group">
            <label>비밀번호 확인</label>
            <input
              type="password"
              name="confirmPassword"
              value={userData.confirmPassword}
              onChange={handleChange}
              className="input-field"
            />
          </div>
          <div className="input-group">
            <label>연락처</label>
            <input
              type="text"
              name="phone"
              value={userData.phone}
              onChange={handleChange}
              className="input-field"
              placeholder="연락처를 입력해주세요."
            />
          </div>
          <div className="input-group">
            <label>지역</label>
            <input
              type="text"
              name="location"
              value={userData.location}
              onChange={handleChange}
              className="input-field"
              placeholder="지역"
            />
          </div>
          <div className="input-group">
            <label>이름</label>
            <input
              type="text"
              name="name"
              value={userData.name}
              onChange={handleChange}
              className="input-field"
              placeholder="이름"
            />
          </div>
        </div>
      </div>
      <button className="submit-button" onClick={handleSubmit}>확인</button>
    </div>
  );
};

export default UserEditPage;
