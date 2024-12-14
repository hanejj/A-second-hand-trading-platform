import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AdminManagementPage.css";

const AdminManagementPage = () => {
  const [admins, setAdmins] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태
  const [newAdmin, setNewAdmin] = useState({ id: "", passwd: "", confirmPasswd: "", name: "" }); // 새 관리자 정보
  const navigate = useNavigate(); // 페이지 이동을 위한 hook
  
  // 관리자 목록 가져오기
  const fetchAdminList = () => {
    // 로컬 스토리지에서 isAdmin 값을 가져오기
    const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
    fetch(`http://localhost:8080/admin/get/adminList?isAdmin=${isAdmin}`)
      .then((response) => response.json())
      .then((data) => {
        if (data.code === "1000") {
          setAdmins(data.data.sort((a, b) => a.idx - b.idx)); // idx 기준 정렬
        } else {
          console.error("관리자 목록 불러오기 실패");
        }
      })
      .catch((error) => console.error("에러 발생:", error));
  };

  useEffect(() => {
    fetchAdminList(); // 초기 데이터 로드
  }, []);

  // 관리자 삭제 요청
  const handleDeleteAdmin = (adminIdx, adminName) => {
    const isConfirmed = window.confirm(`${adminName} 계정을 삭제하시겠습니까?`);
    if (isConfirmed) {
      // 로컬 스토리지에서 isAdmin 값을 가져오기
      const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
      fetch(`http://localhost:8080/admin/delete/${adminIdx}?isAdmin=${isAdmin}`, {
        method: "DELETE",
      })
        .then((response) => response.json())
        .then((data) => {
          if (data.code === "1000") {
            alert("관리자 계정 삭제 성공");
            fetchAdminList(); // 삭제 후 목록 갱신
          } else {
            alert("관리자 계정 삭제 실패");
          }
        })
        .catch((error) => {
          console.error("에러 발생:", error);
          alert("계정 삭제 요청 중 오류가 발생했습니다.");
        });
    }
  };

  // 새 관리자 추가 요청
  const handleAddAdmin = () => {
    if (newAdmin.passwd !== newAdmin.confirmPasswd) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    // 로컬 스토리지에서 isAdmin 값을 가져오기
    const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));
    fetch(`http://localhost:8080/admin/add?isAdmin=${isAdmin}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        id: newAdmin.id,
        passwd: newAdmin.passwd,
        name: newAdmin.name,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.code === "1000") {
          alert("관리자 추가 성공");
          setIsModalOpen(false); // 모달 닫기
          setNewAdmin({ id: "", passwd: "", confirmPasswd: "", name: "" }); // 입력 필드 초기화
          fetchAdminList(); // 추가 후 목록 갱신
        } else if (data.code === "500") {
          alert("이미 존재하는 아이디입니다.");
        } else {
          alert("관리자 추가 중 알 수 없는 오류가 발생했습니다.");
        }
      })
      .catch((error) => {
        console.error("에러 발생:", error);
        alert("추가 요청 중 오류가 발생했습니다.");
      });
  };

  // 뒤로 가기 버튼 핸들러
  const handleGoBack = () => {
    navigate(-1); // 이전 페이지로 이동
  };

  return (
    <div className="admin-management-page">
      {/* 뒤로 가기 버튼 */}
      <button className="go-back-button" onClick={handleGoBack}>
        &lt; 뒤로 가기
      </button>
      <h1>관리자 목록</h1>
      <div className="admin-management-page-add-button-section">
        <button className="admin-management-page-add-admin-button" onClick={() => setIsModalOpen(true)}>
          관리자 추가
        </button>
      </div>
      <table className="admin-management-table">
        <thead>
          <tr>
            <th>순번</th>
            <th>아이디</th>
            <th>담당자</th>
            <th>액션</th>
          </tr>
        </thead>
        <tbody>
          {admins.map((admin, index) => (
            <tr key={admin.idx}>
              <td>{index + 1}</td>
              <td>{admin.id}</td>
              <td>{admin.name}</td>
              <td>
                {admin.idx === 1 ? (
                  <span className="admin-management-page-non-deletable">삭제 불가</span>
                ) : (
                  <button
                    className="admin-management-page-action-button admin-delete-button"
                    onClick={() => handleDeleteAdmin(admin.idx, admin.name)}
                  >
                    계정 삭제
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {isModalOpen && (
  <div className="admin-management-page-modal-overlay">
    <div className="admin-management-page-modal">
      <h2>새 관리자 추가</h2>
      <div className="admin-management-page-modal-content">
        <label>
          아이디:
          <input
            type="text"
            value={newAdmin.id}
            onChange={(e) => setNewAdmin({ ...newAdmin, id: e.target.value })}
          />
        </label>
        <label>
          비밀번호:
          <input
            type="password"
            value={newAdmin.passwd}
            onChange={(e) => setNewAdmin({ ...newAdmin, passwd: e.target.value })}
          />
        </label>
        <label>
          비밀번호 확인:
          <input
            type="password"
            value={newAdmin.confirmPasswd}
            onChange={(e) =>
              setNewAdmin({ ...newAdmin, confirmPasswd: e.target.value })
            }
          />
        </label>
        <label>
          담당자 이름:
          <input
            type="text"
            value={newAdmin.name}
            onChange={(e) => setNewAdmin({ ...newAdmin, name: e.target.value })}
          />
        </label>
      </div>
      <div className="admin-management-page-modal-buttons">
        <button
          className="admin-management-page-modal-button"
          onClick={handleAddAdmin}
          disabled={
            !newAdmin.id || !newAdmin.passwd || !newAdmin.confirmPasswd || !newAdmin.name
          }
        >
          추가
        </button>
        <button
          className="admin-management-page-cancel-button"
          onClick={() => setIsModalOpen(false)}
        >
          취소
        </button>
      </div>
    </div>
  </div>
)}

    </div>
  );
};

export default AdminManagementPage;
