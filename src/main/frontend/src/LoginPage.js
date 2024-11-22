// LoginPage.js
import React, { useState } from 'react';
import './LoginPage.css';
import { useNavigate } from 'react-router-dom';

const LoginPage = () => {
  const [id, setId] = useState('');
  const [passwd, setPasswd] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 로그인 요청 전에 디버깅 로그 출력
    console.log("로그인 시도: 아이디 =", id, "비밀번호 =", passwd);

    try {
      // 로그인 API 호출
      const response = await fetch("http://localhost:8080/user/signin", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          id,
          passwd
        }),
      });

      if (response.ok) {
        const token = response.headers.get('Authorization');
        if (token) {
          localStorage.setItem('token', token); // 토큰 저장
          alert("로그인에 성공했습니다!");
          navigate('/');  // MainPage로 이동
        }
      } else {
        alert('아이디나 비밀번호가 잘못되었습니다.');
      }
    } catch (error) {
      console.error("로그인 요청 중 에러 발생:", error);
    }
  };

  return (
    <div className="login-container">
      <h1 className="logo">가지마켓</h1>
      <form className="login-form" onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="id">아이디</label>
          <input
            type="text"
            id="id"
            name="id"
            placeholder="아이디 입력"
            value={id}
            onChange={(e) => setId(e.target.value)}
          />
        </div>
        <div className="input-group">
          <label htmlFor="passwd">비밀번호</label>
          <input
            type="password"
            id="passwd"
            name="passwd"
            placeholder="비밀번호 입력"
            value={passwd}
            onChange={(e) => setPasswd(e.target.value)}
          />
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