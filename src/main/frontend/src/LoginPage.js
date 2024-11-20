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
    credentials: "include",  // 세션을 쿠키로 포함시키기 위해 credentials 설정
})
    .then((response) => {
        if (!response.ok) {
            throw new Error("서버 응답 에러: " + response.status);
        }
        return response.json(); // JSON 파싱
    })
    .then((data) => {
        console.log("서버 응답:", data);
        if (data.message === "로그인 성공") {
            alert("로그인에 성공했습니다!");
            navigate('/');  // MainPage로 이동
        } else {
            alert(data.message);
        }
    })
    .catch((error) => {
        console.error("로그인 요청 중 에러 발생:", error);
        alert("로그인 중 문제가 발생했습니다.");
    });



      if (response.ok) {
        console.log("로그인 성공");
        navigate('/');  // MainPage로 이동
      } else {
        console.log("로그인 실패", response.status);
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
            type="varchar(50)"
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
            type="varchar(50)"
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
