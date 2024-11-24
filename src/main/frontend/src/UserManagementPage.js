import React, { useEffect, useState } from "react";
import "./UserManagementPage.css";

const UserManagementPage = () => {
  const [users, setUsers] = useState([]);

  // 매너 지수에 따라 하트 색상 계산
  const calculateHeartColor = (mannerPoint) => {
    const maxColor = { r:139, g: 0, b: 139 }; // 보라색 (#800080)
    const minColor = { r: 240, g: 240, b: 240 }; // 흰색 (#ffffff)

    const ratio = mannerPoint / 100;
    const r = Math.round(minColor.r + (maxColor.r - minColor.r) * ratio);
    const g = Math.round(minColor.g + (maxColor.g - minColor.g) * ratio);
    const b = Math.round(minColor.b + (maxColor.b - minColor.b) * ratio);

    return `rgb(${r}, ${g}, ${b})`;
  };

  // 서버에서 회원 목록 불러오기
  const fetchUserList = () => {
    fetch("http://localhost:8080/admin/get/userList")
      .then((response) => response.json())
      .then((data) => {
        if (data.code === "1000") {
          setUsers(data.data.sort((a, b) => a.idx - b.idx)); // idx 순 정렬
        } else {
          console.error("회원 목록 불러오기 실패");
        }
      })
      .catch((error) => console.error("에러 발생:", error));
  };

  useEffect(() => {
    fetchUserList(); // 초기 데이터 로드
  }, []);

  // 영구 정지 처리 함수
  const handleBanUser = (userIdx, userName) => {
    const isConfirmed = window.confirm(`${userName} 회원을 영구 정지하시겠습니까?`);

    if (isConfirmed) {
      fetch(`http://localhost:8080/admin/ban/${userIdx}`, {
        method: "PATCH", 
      })
        .then((response) => response.json())
        .then((data) => {
          if (data.code === "1000") {
            alert("회원 영구 정지 성공");
            fetchUserList(); // 영구 정지 후 회원 목록 갱신
          } else {
            alert("회원 영구 정지 실패");
          }
        })
        .catch((error) => {
          console.error("에러 발생:", error);
          alert("회원 영구 정지 요청 중 에러가 발생했습니다.");
        });
    }
  };

  return (
    <div className="user-management-page">
      <h1>회원 관리</h1>
      <table className="user-management-page-user-table">
        <thead>
          <tr>
            <th>순번</th>
            <th>ID</th>
            <th>닉네임</th>
            <th>매너 지수</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {users.map((user, index) => (
            <tr key={user.idx}>
              <td>{index + 1}</td>
              <td>{user.id}</td>
              <td>{user.name}</td>
              <td>
              {user.manner_point === -1 ? (
                  <span className="banned-status">영구 정지됨</span>
                ) : (
                  <>
                    {user.manner_point}
                    <span
                      className="manner-heart"
                      style={{
                        color: calculateHeartColor(user.manner_point),
                      }}
                    >
                      ♥
                    </span>
                  </>
                )}
              </td>
              <td>
                <button className="user-management-page-action-button">매너 지수 수정</button>
                <button
                  className="user-management-page-action-button action-button-danger"
                  onClick={() => handleBanUser(user.idx, user.name)} // 영구 정지 요청
                  disabled={user.manner_point === -1} // 매너 지수가 -1이면 버튼 비활성화
                >
                  영구 정지
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UserManagementPage;
