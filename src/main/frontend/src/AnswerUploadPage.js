import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import "./QuestionUploadPage.css";

const AnswerUploadPage = () => {
  const { question_idx } = useParams(); // URL에서 question_idx 추출
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [image, setImage] = useState(null);
  const [publicStatus, setPublicStatus] = useState("y"); // 공개 여부
  const [question, setQuestion] = useState(null); // 질문 데이터를 저장할 상태
  const navigate = useNavigate();
  const [isFormValid, setIsFormValid] = useState(false);
  const [isAdmin, setIsAdmin] = useState(null);
  const [adminIdx, setAdminIdx] = useState(null);

  // 문의글 정보 가져오기
  const fetchQuestionDetail = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/ask/question/${question_idx}`
      );
      const data = await response.json();
      if (data.code === "1000") {
        const questionData = data.data;
        setQuestion(questionData); // 질문 데이터를 상태로 설정
        setTitle(`Re: ${questionData.questionTitle}`); // 제목 자동 설정
        setPublicStatus(questionData.questionPublic ? "y" : "n"); // 공개 여부 자동 설정
      }
    } catch (error) {
      console.error("문의글 조회 실패:", error);
    }
  };

  useEffect(() => {
    fetchQuestionDetail();
    // isAdmin 초기 설정
    const isAdminStored = localStorage.getItem("isAdmin");
    const isAdminParsed = JSON.parse(isAdminStored);
    setIsAdmin(isAdminParsed);
    // 현재 로그인한 관리자 정보 가져오기
    const token = localStorage.getItem("token");
    if (token) {
      axios
        .get("http://localhost:8080/admin/profile", {
          headers: {
            Authorization: "Bearer " + token, // JWT 토큰을 Authorization 헤더에 추가
          },
          params: {
            isAdmin: isAdminParsed,
          },
        })
        .then((response) => {
          if (response.data) {
            setAdminIdx(response.data.data.admin_idx);
          }
        })
        .catch((error) => {
          console.error("관리자 정보를 가져오는 중 오류 발생:", error);
        });
    }
  }, [question_idx]); // question_idx가 변경되면 다시 호출


  const handlePublicStatusChange = (status) => {
    setPublicStatus(status);
  };

  const handleImageChange = (event) => {
    setImage(event.target.files[0]);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    
    const formData = new FormData();
    formData.append("question_idx", question_idx);
    formData.append("title", title);
    formData.append("content", content);
    formData.append("publicStatus", publicStatus);
    formData.append("admin_idx", adminIdx);
    if (image) {
      formData.append("image", image); // 이미지 파일이 있으면 추가
    }
    try {
      const response = await axios.post(
        `http://localhost:8080/ask/${question_idx}/answer`,
        formData,
        {
          params: { isAdmin: isAdmin },
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      if (response.data.code === "1000") {
        alert("답글 작성 성공");
        // 성공 시 해당 답글로 이동
        navigate(`/inquiry/answer/${response.data.data}`);
      } else {
        alert("문의글 작성 실패");
      }
    } catch (error) {
      console.error("답글 등록 중 오류 발생:", error);
      alert("오류가 발생했습니다.");
    }
  };

  useEffect(() => {
    // 필수 입력 필드 검증
    const isValid = title.trim() !== "" && content.trim() !== "";
    setIsFormValid(isValid);
  }, [title, content]);

  if (!question) {
    return <div>Loading...</div>; // 문의글 데이터를 로딩 중일 때
  }

  return (
    <div className="question-upload-page">
      <h2 className="QuestionUploadPage-title">답글 작성</h2>
      <form onSubmit={handleSubmit} className="QuestionUploadPage-upload-form">
        <div className="QuestionUploadPage-form-group">
          <label>제목</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            className="QuestionUploadPage-form-group-input-field"
          />
        </div>

        <div className="QuestionUploadPage-form-group">
          <label>내용</label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
            className="QuestionUploadPage-form-group-input-field"
          />
        </div>

        <div className="QuestionUploadPage-form-group">
          <label>이미지</label>
          <input
            type="file"
            onChange={handleImageChange}
            className="QuestionUploadPage-form-group-input-field"
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
          답글 작성
        </button>
      </form>
    </div>
  );
};

export default AnswerUploadPage;
