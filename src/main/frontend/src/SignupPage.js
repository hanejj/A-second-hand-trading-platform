import React, { useState } from 'react';
import './SignupPage.css';

const SignupPage = () => {
  const [formData, setFormData] = useState({
    id: '',
    passwd: '',
    confirmPasswd: '',
    name: '',
    birth: '',
    sex: '',
    phone: '',
    nickname: '',
    location: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Check if passwords match
    if (formData.passwd !== formData.confirmPasswd) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    // Prepare the data to be sent in the POST request
    const requestData = {
      id: formData.id,
      passwd: formData.passwd,
      name: formData.name,
      birth: formData.birth,
      sex: formData.sex === 'male' ? 'M' : 'F', // Convert to 'M' or 'F'
      phone: formData.phone,
      nickname: formData.nickname,
      location: formData.location,
    };

    try {
      const response = await fetch("http://localhost:8080/user/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestData), // Send the data as JSON
      });

      if (response.ok) {
        alert("회원가입 성공");
      } else if (response.status === 409) {
        alert("이미 존재하는 아이디 또는 닉네임입니다.");
      } else {
        alert("회원가입 실패");
      }
    } catch (error) {
      console.error("회원가입 요청 중 에러 발생:", error);
    }
  };

  return (
    <div className="signup-container">
      <h1 className="title">회원가입</h1>
      <form className="signup-form" onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="id">아이디</label>
          <input
            type="text"
            id="id"
            name="id"
            placeholder="아이디 입력"
            value={formData.id}
            onChange={handleChange}
          />
        </div>
        <div className="input-group">
          <label htmlFor="passwd">비밀번호</label>
          <input
            type="password"
            id="passwd"
            name="passwd"
            placeholder="비밀번호 입력"
            value={formData.passwd}
            onChange={handleChange}
          />
        </div>
        <div className="input-group">
          <label htmlFor="confirm-passwd">비밀번호 재확인</label>
          <input
            type="password"
            id="confirm-passwd"
            name="confirmPasswd"
            placeholder="비밀번호 재확인"
            value={formData.confirmPasswd}
            onChange={handleChange}
          />
        </div>
        <div className="input-group">
          <label htmlFor="name">이름</label>
          <input
            type="text"
            id="name"
            name="name"
            placeholder="이름 입력"
            value={formData.name}
            onChange={handleChange}
          />
        </div>
        <div className="input-group">
          <label htmlFor="birth">생년월일</label>
          <input
            type="date"
            id="birth"
            name="birth"
            value={formData.birth}
            onChange={handleChange}
          />
        </div>
        <div className="input-group">
          <label htmlFor="sex">성별</label>
          <select
            id="sex"
            name="sex"
            value={formData.sex}
            onChange={handleChange}
          >
            <option value="">선택하세요</option>
            <option value="male">남성</option>
            <option value="female">여성</option>
          </select>
        </div>
        <div className="input-group">
          <label htmlFor="phone">핸드폰 번호</label>
          <input
            type="text"
            id="phone"
            name="phone"
            placeholder="핸드폰 번호 입력"
            value={formData.phone}
            onChange={handleChange}
          />
        </div>
        <div className="input-group">
          <label htmlFor="nickname">닉네임</label>
          <input
            type="text"
            id="nickname"
            name="nickname"
            placeholder="닉네임 입력"
            value={formData.nickname}
            onChange={handleChange}
          />
        </div>
        <div className="input-group">
          <label htmlFor="location">지역</label>
          <input
            type="text"
            id="location"
            name="location"
            placeholder="지역 입력"
            value={formData.location}
            onChange={handleChange}
          />
        </div>
        <button type="submit" className="signup-button">회원가입</button>
      </form>
    </div>
  );
};

export default SignupPage;
