import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './MyPage.css';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';

const MyPage = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [products, setProducts] = useState([]);
  const [wishlist, setWishlist] = useState([]);
  const [activeTab, setActiveTab] = useState('판매내역');
  const [purchasedProducts, setPurchasedProducts] = useState([]); // 구매 내역
  const [isPointModalOpen, setIsPointModalOpen] = useState(false);
  const [pointAmount, setPointAmount] = useState(0);
  const [isWithdrawModalOpen, setIsWithdrawModalOpen] = useState(false);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [password, setPassword] = useState('');
  const [inquiries, setInquiries] = useState([]);
  const navigate = useNavigate();
  const [openChats, setOpenChats] = useState({}); // 채팅창 열림/닫힘 상태 관리
  const [chatList, setChatList] = useState([]); // 전체 채팅 목록
const [loadingChats, setLoadingChats] = useState(true); // 채팅 로딩 상태
const [chatError, setChatError] = useState(null); // 채팅 로드 에러

useEffect(() => {
  if (userInfo) {
    const fetchChatList = async () => {
      setLoadingChats(true);
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8080/chat/get/chatList', {
          params: { userId: userInfo.userIdx },
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.data?.chats) {
          setChatList(response.data.chats);
        } else {
          setChatList([]); // 데이터가 없으면 빈 배열 설정
        }
      } catch (err) {
        setChatError('채팅 데이터를 불러오는 중 오류가 발생했습니다.');
        console.error(err);
      } finally {
        setLoadingChats(false);
      }
    };

    fetchChatList();
  }
}, [userInfo]);


const toggleChat = (productId) => {
  setOpenChats((prevState) => ({
    ...prevState,
    [productId]: !prevState[productId], // 이전 상태를 반전
  }));
};

  // 공개 여부를 반환하는 함수
  const getPublicStatus = (publicFlag) => {
    return publicFlag === true || publicFlag === "y" ? "공개" : "비공개";
  };

  // 매너 지수에 따라 하트 색상 계산
  const calculateHeartColor = (mannerPoint) => {
    const maxColor = { r: 139, g: 0, b: 139 }; // 보라색 (#800080)
    const minColor = { r: 240, g: 240, b: 240 }; // 흰색 (#ffffff)

    const ratio = mannerPoint / 100;
    const r = Math.round(minColor.r + (maxColor.r - minColor.r) * ratio);
    const g = Math.round(minColor.g + (maxColor.g - minColor.g) * ratio);
    const b = Math.round(minColor.b + (maxColor.b - minColor.b) * ratio);

    return `rgb(${r}, ${g}, ${b})`;
  };

  useEffect(() => {
    if (userInfo) {
      const fetchInquiries = async () => {
        try {
          const response = await axios.get("http://localhost:8080/ask", {
            params: {
              isAdmin: false, // MyPage에서 사용자로 요청
              userIdx: userInfo.userIdx,
            },
          });
          if (response.data.code === "1000") {
            setInquiries(response.data.data || []);
          } else {
            console.error("문의 내역을 가져오는 데 실패했습니다.");
          }
        } catch (error) {
          console.error("문의 내역 조회 중 오류 발생:", error);
        }
      };

      fetchInquiries();
    }
  }, [userInfo]);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    // 사용자 프로필 정보에서 이메일을 추출해서 특정 사용자 정보 요청
    const getUserProfile = async () => {
      try {
        const profileResponse = await axios.get('http://localhost:8080/user/profile', {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (profileResponse.data?.code === 1000) {
          const userEmail = profileResponse.data.user.id;
          const encodedEmail = encodeURIComponent(userEmail); // 이메일 인코딩

          const userResponse = await axios.get(`http://localhost:8080/user/${encodedEmail}/get`, {
            headers: { Authorization: `Bearer ${token}` },
          });

          const pointResponse = await axios.get(`http://localhost:8080/user/${encodedEmail}/point`, {
            headers: { Authorization: `Bearer ${token}` },
          });

          const sellingResponse = await axios.get(`http://localhost:8080/user/${encodedEmail}/selling`, {
            headers: { Authorization: `Bearer ${token}` },
          });

          const wishlistResponse = await axios.get(`http://localhost:8080/user/${encodedEmail}/get/wishlist`, {
            headers: { Authorization: `Bearer ${token}` },
          });

          setUserInfo({
            ...userResponse.data.user,
            points: pointResponse.data.point || 0,
            image: userResponse.data.user.image
              ? `http://localhost:8080/image?image=${userResponse.data.user.image}`
              : 'default-avatar.png',
          });
          setProducts(sellingResponse.data.products || []);
          setWishlist(wishlistResponse.data.wishlist || []);
        } else {
          throw new Error('사용자 정보를 가져오는 중 오류 발생');
        }
      } catch (error) {
        console.error('사용자 정보를 가져오는 중 오류 발생:', error);
        if (error.response?.status === 401) {
          alert('로그인 정보가 만료되었습니다. 다시 로그인 해주세요.');
          localStorage.removeItem('token');
          navigate('/login');
        }
      }
    };

    getUserProfile();
  }, [navigate]);

  // 채팅 목록 불러오기
  useEffect(() => {
    if (userInfo) {
      const token = localStorage.getItem('token');
      const userId = userInfo.userIdx; // 로그인된 유저의 ID를 가져옴
      axios
        .get('http://localhost:8080/chat/get/chatList', {
          params: { userId: userId }, // userId를 쿼리 파라미터로 보내기
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          if (response.data && response.data.chats) {
            setChatList(response.data.chats || []); // 채팅 내역을 채팅 목록에 저장
          } else {
            setChatList([]); // 채팅이 없는 경우 빈 배열로 설정
          }
        })
        .catch((error) => {
          console.error('채팅 목록을 불러오는 중 오류 발생:', error);
        });
    }
  }, [userInfo]);

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    alert('로그아웃 되었습니다.');
    navigate('/login');
  };

  const handleOpenPointModal = () => {
    setIsPointModalOpen(true);
  };

  const handleClosePointModal = () => {
    setIsPointModalOpen(false);
  };

  const handleOpenWithdrawModal = () => {
    setIsWithdrawModalOpen(true);
  };

  const handleCloseWithdrawModal = () => {
    setIsWithdrawModalOpen(false);
  };

  const handleOpenAuthModal = () => {
    setIsAuthModalOpen(true);
  };

  const handleCloseAuthModal = () => {
    setIsAuthModalOpen(false);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };

  const handleAuthSubmit = () => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    axios.post(`http://localhost:8080/user/${encodeURIComponent(userInfo.id)}/auth`, {
      password: password
    }, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    .then(response => {
      if (response.data.authenticated) {
        alert('인증에 성공했습니다. 정보를 수정할 수 있습니다.');
        navigate(`/user/${userInfo.id}/edit`);
      } else {
        alert('비밀번호가 틀렸습니다. 다시 시도해주세요.');
      }
      handleCloseAuthModal();
    })
    .catch(error => {
      console.error('인증 중 오류 발생:', error);
      alert('인증 중 오류가 발생했습니다.');
    });
  };

  const handlePointChange = (e) => {
    setPointAmount(e.target.value);
  };

  const handlePointSubmit = (isWithdraw) => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    const amount = parseInt(pointAmount, 10);
    if (isWithdraw && userInfo.points < amount) {
      alert('포인트가 부족합니다. 인출할 수 없습니다.');
      return;
    }
    const finalAmount = isWithdraw ? -amount : amount;

    axios.post(`http://localhost:8080/user/${encodeURIComponent(userInfo.id)}/point/update`, {
      amount: finalAmount
    }, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    .then(response => {
      alert(response.data.message);
      setUserInfo(prevState => ({
        ...prevState,
        points: prevState.points + finalAmount
      }));
      if (isWithdraw) {
        handleCloseWithdrawModal();
      } else {
        handleClosePointModal();
      }
    })
    .catch(error => {
      console.error('포인트 업데이트 중 오류 발생:', error);
      alert('포인트 업데이트 중 오류가 발생했습니다.');
    });
  };

  const getPurchaseHistory = async () => {
    const token = localStorage.getItem('token');
    const userIdx = userInfo.userIdx;
  
    try {
      const response = await axios.get('http://localhost:8080/product/purchases', {
        params: { user_idx: userIdx },
        headers: { Authorization: `Bearer ${token}` },
      });
  
      if (response.data.code === 1000) {
        setPurchasedProducts(response.data.purchases || []);
      } else {
        console.error('구매 내역 조회 실패:', response.data.message);
      }
    } catch (error) {
      console.error('구매 내역 조회 중 오류 발생:', error);
    }
  };  

  //구매내역 불러오기
  useEffect(() => {
    if (userInfo) {
      getPurchaseHistory();
    }
  }, [userInfo]);  

  if (!userInfo) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="my-page">
      <div className="header-section">
        <div className="user-info">
        <div className="user-avatar">
        <img
              src={userInfo.image}
              alt="User Avatar"
              className="avatar-img"
            />
</div>

          <div className="user-details">
            <div className="user-email">{userInfo.id}</div>
            <div className="user-name">{userInfo.name}</div>
            <div className="user-message">{userInfo.message}</div>
          </div>
          <div className="points-section">
          <div className="points-box">
            <div className="points-value">{userInfo.points || 0}</div>
            <div className="points-label">포인트</div>
          </div>
          <div className="points-button-box">
          <button className="point-button" onClick={handleOpenPointModal}>포인트 충전</button>
          <button className="point-button" onClick={handleOpenWithdrawModal}>포인트 인출</button>
          </div>
          <div className="points-box">
          <div className="points-value">
            {userInfo.mannerPoint}
            <span
              style={{
                color: calculateHeartColor(userInfo.mannerPoint),
                marginLeft: "5px",
              }}
            >
              ♥
            </span>
          </div>
          <div className="points-label">매너 지수</div>
        </div>

        </div>
        </div>
        
        <button className="edit-button" onClick={handleOpenAuthModal}>내 정보 수정</button>
        {/*<button className="logout-button" onClick={handleLogout}>로그아웃</button>*/}
      </div>

      <div className="tabs-section">
        {['판매내역', '구매내역', '채팅', '찜 목록', '문의 내역'].map((tab) => (
          <div
            key={tab}
            className={`tab ${activeTab === tab ? 'active' : ''}`}
            onClick={() => handleTabClick(tab)}
          >
            {tab}
          </div>
        ))}
      </div>

      {activeTab === '판매내역' && (
  <div className="product-list">
    {products.length > 0 ? (
      [...products]
        .filter(product => product.status !== 'removed') // status가 removed인 상품 제외
        .sort((a, b) => new Date(b.created_at) - new Date(a.created_at)) // 최신 순 정렬
        .map(product => (
          <Link 
            to={`/product/${product.product_idx}`} 
            className="product-card" 
            key={product.product_idx}
          >
            <div className="product-info">
              <h3>{product.title}</h3>
              <p>{product.price}원</p>
              <p>{product.location}</p>
              <p>♡ {product.heart_num} 💬 {product.chat_num}</p>
            </div>
            <img 
              src={`http://localhost:8080/image?image=${product.image}`} 
              alt={product.title} 
              className="product-image"
            />
          </Link>
        ))
    ) : (
      <div>판매한 상품이 없습니다.</div>
    )}
  </div>
)}

{activeTab === '찜 목록' && (
  <div className="product-list">
    {wishlist.length > 0 ? (
      [...wishlist]
        .filter(product => product.status !== 'removed') // status가 removed인 상품 제외
        .sort((a, b) => new Date(b.created_at) - new Date(a.created_at)) // 최신 순 정렬
        .map(item => (
          <Link 
            to={`/product/${item.product_idx}`} 
            className="product-card" 
            key={item.product_idx}
          >
            <div className="product-info">
              <h3>{item.title}</h3>
              <p>{item.price}원</p>
              <p>{item.location}</p>
              <p>♡ {item.heart_num} 💬 {item.chat_num}</p>
            </div>
            <img 
              src={`http://localhost:8080/image?image=${item.image}`} 
              alt={item.title} 
              className="product-image"
            />
          </Link>
        ))
    ) : (
      <div>찜한 상품이 없습니다.</div>
    )}
  </div>
)}

{activeTab === '채팅' && (
  <div className="chat-list">
    {Object.keys(chatList).length > 0 ? (
      Object.keys(chatList).map((productId) => (
        <div key={productId} className="product-chat-group">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3
              onClick={() => toggleChat(productId)}
              style={{ cursor: 'pointer', color: openChats[productId] ? 'blue' : 'black', margin: 0 }}
            >
              상품 ID: {productId} {openChats[productId] ? '▲' : '▼'}
            </h3>
            <div style={{ display: 'flex', gap: '10px' }}>
              <Link
                to={`/product/${productId}`}
                className="product-link"
                style={{ textDecoration: 'none', color: '#007BFF' }}
              >
                상품으로 이동
              </Link>
              <Link
                to={`/product/${productId}/chat`}
                className="chat-link"
                style={{ textDecoration: 'none', color: '#007BFF' }}
              >
                채팅으로 이동
              </Link>
            </div>
          </div>
          {openChats[productId] && (
            <div className="chat-messages">
              {chatList[productId].map((chat, index) => (
                <div
                  key={index}
                  className={`chat-message ${
                    chat.senderId === userInfo.userIdx ? 'own-message' : 'other-message'
                  }`}
                >
                  <div className="chat-message-content">
                    <strong>
                      {chat.senderId === userInfo.userIdx
                        ? '나'
                        : chat.senderNickname || '알 수 없음'}
                      :
                    </strong>
                    <span>{chat.messageContent}</span>
                    <div className="message-time">
                      {new Date(chat.sentAt).toLocaleString()}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      ))
    ) : (
      <div>채팅 내역이 없습니다.</div>
    )}
  </div>
)}

{activeTab === '구매내역' && (
  <div className="product-list">
    {purchasedProducts.length > 0 ? (
      [...purchasedProducts]
        .filter(product => product.status !== 'removed') // status가 removed인 상품 제외
        .sort((a, b) => new Date(b.created_at) - new Date(a.created_at)) // 최신 순 정렬
        .map(product => (
          <Link 
            to={`/product/${product.productIdx}`} 
            className="product-card" 
            key={product.productIdx}
          >
            <div className="product-info">
              <h3>{product.title}</h3>
              <p>가격: {product.price.toLocaleString()}원</p>
              <p>위치: {product.location}</p>
              <p>♡ {product.heartNum} 💬 {product.chatNum}</p>
            </div>
            <img 
              src={`http://localhost:8080/image?image=${product.image}`} 
              alt={product.title} 
              className="product-image"
            />
          </Link>
        ))
    ) : (
      <div>구매한 상품이 없습니다.</div>
    )}
  </div>
)}

{activeTab === "문의 내역" && (
        <div className="inquiries-list">
          <h1>문의 내역</h1>
          <table className="inquiries-table">
            <thead>
              <tr>
                <th>순번</th>
                <th>제목</th>
                <th>날짜</th>
                <th>공개여부</th>
              </tr>
            </thead>
            <tbody>
              {inquiries
                .sort((a, b) => new Date(b.questionCreatedAt) - new Date(a.questionCreatedAt))
                .map((inquiry, index) => (
                  <React.Fragment key={inquiry.questionIdx}>
                    <tr>
                      <td>{inquiries.length - index}</td>
                      <td>
                        <Link
                          to={`/Inquiry/question/${inquiry.questionIdx}`}
                          className="inquiry-link"
                        >
                          {inquiry.questionTitle}
                        </Link>
                      </td>
                      <td>{new Date(inquiry.questionCreatedAt).toLocaleDateString()}</td>
                      <td>{getPublicStatus(inquiry.questionPublic)}</td>
                    </tr>
                    {inquiry.answer && (
                      <tr className="answer-row">
                        <td></td>
                        <td>
                          <Link
                            to={`/Inquiry/answer/${inquiry.answer.answerIdx}`}
                            className="inquiry-link"
                          >
                            ㄴ {inquiry.answer.answerTitle}
                          </Link>
                        </td>
                        <td>{new Date(inquiry.answer.answerCreatedAt).toLocaleDateString()}</td>
                        <td>{getPublicStatus(inquiry.answer.answerPublic)}</td>
                      </tr>
                    )}
                  </React.Fragment>
                ))}
            </tbody>
          </table>
          {inquiries.length === 0 && <p>문의 내역이 없습니다.</p>}
        </div>
      )}

      {isAuthModalOpen && (
        <div className="auth-modal" style={{ top: '50%', left: '50%', transform: 'translate(-50%, -50%)', position: 'fixed', zIndex: '1000', width: '300px', padding: '20px', backgroundColor: 'white', borderRadius: '10px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)' }}>
          <div className="auth-modal-content">
            <h2>비밀번호 인증</h2>
            <p>내 정보를 수정하려면 비밀번호를 입력해주세요.</p>
            <input type="password" value={password} onChange={handlePasswordChange} style={{ width: '100%', marginBottom: '10px' }} />
            <button onClick={handleAuthSubmit} style={{ marginRight: '10px' }}>인증</button>
            <button onClick={handleCloseAuthModal}>닫기</button>
          </div>
        </div>
      )}

      {isPointModalOpen && (
        <div className="point-modal" style={{ top: '50%', left: '50%', transform: 'translate(-50%, -50%)', position: 'fixed', zIndex: '1000', width: '300px', padding: '20px', backgroundColor: 'white', borderRadius: '10px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)' }}>
          <div className="point-modal-content">
            <h2>포인트 충전</h2>
            <p>현재 포인트: {userInfo.points || 0}</p>
            <p>충전할 금액을 입력해주세요.</p>
            <input type="number" value={pointAmount} onChange={handlePointChange} style={{ width: '100%', marginBottom: '10px' }} />
            <button onClick={() => handlePointSubmit(false)} style={{ marginRight: '10px' }}>충전</button>
            <button onClick={handleClosePointModal}>닫기</button>
          </div>
        </div>
      )}

      {isWithdrawModalOpen && (
        <div className="point-modal" style={{ top: '50%', left: '50%', transform: 'translate(-50%, -50%)', position: 'fixed', zIndex: '1000', width: '300px', padding: '20px', backgroundColor: 'white', borderRadius: '10px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)' }}>
          <div className="point-modal-content">
            <h2>포인트 인출</h2>
            <p>현재 포인트: {userInfo.points || 0}</p>
            <p>인출할 금액을 입력해주세요.</p>
            <input type="number" value={pointAmount} onChange={handlePointChange} style={{ width: '100%', marginBottom: '10px' }} />
            <button onClick={() => handlePointSubmit(true)} style={{ marginRight: '10px' }}>인출</button>
            <button onClick={handleCloseWithdrawModal}>닫기</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyPage;
