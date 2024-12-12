import React, { useEffect, useState } from "react";
import "./UserManagementPage.css";
import { useNavigate, Link } from "react-router-dom";

const UserManagementPage = () => {
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();

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
    // 로컬 스토리지에서 isAdmin 값을 가져오기
    const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
    fetch(`http://localhost:8080/admin/get/userList?isAdmin=${isAdmin}`)
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
      // 로컬 스토리지에서 isAdmin 값을 가져오기
      const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
      fetch(`http://localhost:8080/admin/ban/${userIdx}?isAdmin=${isAdmin}`, {
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

  //매너 포인트 설정 함수
  const handleEditMannerPoint = (userIdx, userName, currentMannerPoint) => {
    const newMannerPoint = prompt(
      `${userName} 회원의 새로운 매너 지수를 입력하세요 (0 ~ 100):`,
      currentMannerPoint
    );
  
    if (newMannerPoint === null) return; // 취소 버튼 클릭 시 아무 작업도 하지 않음
  
    const parsedPoint = parseInt(newMannerPoint, 10);
  
    if (isNaN(parsedPoint) || parsedPoint < 0 || parsedPoint > 100) {
      alert("유효한 매너 지수를 입력해주세요 (0 ~ 100 사이의 숫자)");
      return;
    }

    const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
    fetch(`http://localhost:8080/user/${userIdx}/mannerpoint/update`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ mannerPoint: parsedPoint }), // 새 매너 지수 전송
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.code === 1000) {
          alert("매너 지수 수정 성공");
          fetchUserList(); // 매너 지수 수정 후 회원 목록 갱신
        } else {
          alert("매너 지수 수정 실패");
        }
      })
      .catch((error) => {
        console.error("에러 발생:", error);
        alert("매너 지수 수정 요청 중 에러가 발생했습니다.");
      });
  };   

  // 뒤로 가기 버튼 핸들러
  const handleGoBack = () => {
    navigate(-1); // 이전 페이지로 이동
  };

  return (
    <div className="user-management-page">
      {/* 뒤로 가기 버튼 */}
      <button className="go-back-button" onClick={handleGoBack}>
        &lt; 뒤로 가기
      </button>
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
              <td>
                <Link to={`/author/${user.idx}`}>
                  {user.name}
                </Link>
                </td>
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
                <button
                  className="user-management-page-action-button"
                  onClick={() => handleEditMannerPoint(user.idx, user.name, user.manner_point)}
                  disabled={user.manner_point === -1} // 매너 지수가 -1이면 버튼 비활성화
                >
                  매너 지수 수정
                </button>
                <button
                  className="user-management-page-action-button action-button-danger"
                  onClick={() => handleBanUser(user.idx, user.name)} // 영구 정지 요청
                  disabled={user.manner_point <= 0} // 매너 지수가 -1이면 버튼 비활성화
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
