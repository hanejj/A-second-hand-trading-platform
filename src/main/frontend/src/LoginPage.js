import React from 'react';
import './LoginPage.css';

const LoginPage = () => {
  return (
    <div className="login-container">
      <h1 className="logo">가지마켓</h1>
      <form className="login-form">
        <div className="input-group">
          <label htmlFor="username">아이디</label>
          <input type="text" id="username" name="username" placeholder="아이디 입력" />
        </div>
        <div className="input-group">
          <label htmlFor="password">비밀번호</label>
          <input type="password" id="password" name="password" placeholder="비밀번호 입력" />
        </div>
        <button type="submit" className="login-button">로그인</button>
      </form>
      <div className="login-links">
        <a href="/forgot-password">아이디/비번 찾기</a>
        <a href="/signup">회원가입</a>
      </div>
    </div>
  );
};

export default LoginPage;
