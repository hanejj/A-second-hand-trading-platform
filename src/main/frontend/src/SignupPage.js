import React from 'react';
import './SignupPage.css';

const SignupPage = () => {
  return (
    <div className="signup-container">
      <h1 className="title">회원가입</h1>
      <form className="signup-form">
        <div className="input-group">
          <label htmlFor="username">아이디</label>
          <input type="text" id="username" name="username" placeholder="아이디 입력" />
        </div>
        <div className="input-group">
          <label htmlFor="password">비밀번호</label>
          <input type="password" id="password" name="password" placeholder="비밀번호 입력" />
        </div>
        <div className="input-group">
          <label htmlFor="confirm-password">비밀번호 재확인</label>
          <input type="password" id="confirm-password" name="confirm-password" placeholder="비밀번호 재확인" />
        </div>
        <div className="input-group">
          <label htmlFor="name">이름</label>
          <input type="text" id="name" name="name" placeholder="이름 입력" />
        </div>
        <div className="input-group">
          <label htmlFor="dob">생년월일</label>
          <input type="date" id="dob" name="dob" />
        </div>
        <div className="input-group">
          <label htmlFor="gender">성별</label>
          <select id="gender" name="gender">
            <option value="">선택하세요</option>
            <option value="male">남성</option>
            <option value="female">여성</option>
          </select>
        </div>
        <div className="input-group">
          <label htmlFor="phone">핸드폰 번호</label>
          <input type="text" id="phone" name="phone" placeholder="핸드폰 번호 입력" />
        </div>
        <div className="input-group">
          <label htmlFor="nickname">닉네임</label>
          <input type="text" id="nickname" name="nickname" placeholder="닉네임 입력" />
        </div>
        <div className="input-group">
          <label htmlFor="region">지역</label>
          <input type="text" id="region" name="region" placeholder="지역 입력" />
        </div>
        <button type="submit" className="signup-button">회원가입</button>
      </form>
    </div>
  );
};

export default SignupPage;
