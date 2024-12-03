import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './MyPage.css';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';

const MyPage = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [products, setProducts] = useState([]);
  const [wishlist, setWishlist] = useState([]);
  const [chatList, setChatList] = useState([]); // 채팅 목록
  const [activeTab, setActiveTab] = useState('판매내역');
  const [isPointModalOpen, setIsPointModalOpen] = useState(false);
  const [pointAmount, setPointAmount] = useState(0);
  const [isWithdrawModalOpen, setIsWithdrawModalOpen] = useState(false);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

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
        </div>
        <div className="points-section">
          <div className="points-box">
            <div className="points-value">{userInfo.points || 0}</div>
            <div className="points-label">포인트</div>
            <button className="point-button" onClick={handleOpenPointModal}>포인트 충전</button>
            <button className="point-button" onClick={handleOpenWithdrawModal}>포인트 인출</button>
          </div>
          <div className="points-box">
            <div className="points-value">{userInfo.mannerPoint}</div>
            <div className="points-label">매너 지수</div>
          </div>
        </div>
        <button className="edit-button" onClick={handleOpenAuthModal}>내 정보 수정</button>
        {/*<button className="logout-button" onClick={handleLogout}>로그아웃</button>*/}
      </div>

      <div className="tabs-section">
        {['판매내역', '구매내역', '채팅', '찜 목록', '문의 내역', '신고 내역'].map((tab) => (
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
          <h3>
            {/* 상품 ID를 클릭하면 상품 상세 페이지로 이동 */}
            <Link to={`/product/${productId}`} style={{ textDecoration: 'none', color: '#000' }}>
              상품 ID: {productId}
            </Link>
          </h3>
          <div className="chat-messages">
            {chatList[productId].map((chat, index) => (
              <div key={index} className="chat-card">
                <div
                  className={`chat-message ${
                    chat.senderId === userInfo.userIdx
                      ? 'own-message'
                      : chat.receiverId === userInfo.userIdx
                      ? 'receiver-message'
                      : 'other-message'
                  }`}
                >
                  <strong>
                    {chat.senderId === userInfo.userIdx
                      ? '나'
                      : chat.receiverId === userInfo.userIdx
                      ? '다른 사람'
                      : chat.senderId}
                    :
                  </strong>
                  <span>{chat.messageContent}</span>
                </div>
                <div className="chat-time">
                  {new Date(chat.sentAt).toLocaleString()}
                </div>
              </div>
            ))}
          </div>
        </div>
      ))
    ) : (
      <div>채팅 내역이 없습니다.</div>
    )}
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
