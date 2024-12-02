import React, { useState, useEffect } from "react";
import axios from "axios";
import "./QuestionUploadPage.css";
import { useNavigate } from "react-router-dom";

const QuestionUploadPage = () => {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [image, setImage] = useState(null);
  const [publicStatus, setPublicStatus] = useState("y");
  const [userIdx, setUserIdx] = useState(null);
  const [isFormValid, setIsFormValid] = useState(false);
  const isAdmin = JSON.parse(localStorage.getItem("isAdmin"));

  // 로그인한 사용자 정보 가져오기
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      axios
        .get("http://localhost:8080/user/profile", {
          headers: {
            Authorization: "Bearer " + token,
          },
        })
        .then((response) => {
          if (response.data && response.data.user) {
            setUserIdx(response.data.user.userIdx);
          }
        })
        .catch((error) => {
          console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        });
    }
  }, []);

  const handlePublicStatusChange = (status) => {
    setPublicStatus(status);
  };

  const handleImageChange = (event) => {
    setImage(event.target.files[0]);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    formData.append("publicStatus", publicStatus); 
    formData.append("user_idx", userIdx);

    if (image) {
      formData.append("image", image); // 이미지 파일이 있으면 추가
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/ask/write",
        formData,
        {
          params: { isAdmin: isAdmin }, // isAdmin은 쿼리 파라미터로 전송
          headers: {
            "Content-Type": "multipart/form-data", // 멀티파트 폼 데이터 처리
          },
        },
      );

      if (response.data.code === "1000") {
        alert("문의글 작성 성공");
        // 성공 시 해당 문의글로 이동
        navigate(`/inquiry/question/${response.data.data.question_idx}`);
      } else {
        alert("문의글 작성 실패");
      }
    } catch (error) {
      console.error("문의글 작성 실패:", error);
      alert("문의글 작성 중 오류가 발생했습니다.");
    }
  };

  useEffect(() => {
    // 필수 입력 필드 검증
    const isValid = title.trim() !== "" && content.trim() !== "";
    setIsFormValid(isValid);
  }, [title, content]);

  return (
    <div className="question-upload-page">
      <h2 className="QuestionUploadPage-title">문의글 작성</h2>
      <form onSubmit={handleSubmit} className="QuestionUploadPage-upload-form">
        <div className="QuestionUploadPage-form-group">
          <label>제목</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            className="input-field"
          />
        </div>

        <div className="QuestionUploadPage-form-group">
          <label>내용</label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
            className="input-field"
          />
        </div>

        <div className="QuestionUploadPage-form-group">
          <label>이미지</label>
          <input
            type="file"
            onChange={handleImageChange}
            className="input-field"
          />
        </div>

        <div className="QuestionUploadPage-form-group">
          <label>공개 여부</label>
          <div className="QuestionUploadPage-public-status-buttons">
            <button
              type="button"
              onClick={() => handlePublicStatusChange("y")}
              className={publicStatus === "y" ? "active" : ""}
            >
              공개
            </button>
            <button
              type="button"
              onClick={() => handlePublicStatusChange("n")}
              className={publicStatus === "n" ? "active" : ""}
            >
              비공개
            </button>
          </div>
        </div>

        {!isFormValid && (
          <p className="QuestionUploadPage-error-message">
            모든 필수 항목을 입력해주세요!
          </p>
        )}

        <button
          type="submit"
          className={`QuestionUploadPage-submit-button ${isFormValid ? "active" : "disabled"}`}
          disabled={!isFormValid}
        >
          문의글 작성
        </button>
      </form>
    </div>
  );
};

export default QuestionUploadPage;
