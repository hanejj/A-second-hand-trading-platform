import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Chat.css';

const Chat = () => {
  const { productIdx } = useParams();
  const navigate = useNavigate();
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState('');
  const [userId, setUserId] = useState();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태
  const [transferAmount, setTransferAmount] = useState(''); // 송금 금액
  const [productTitle, setProductTitle] = useState('');
  const [writerId, setWriterId] = useState();

  // 사용자 정보 및 상품 제목 가져오기
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('로그인이 필요합니다.');
          navigate('/login');
          return;
        }

        // 사용자 ID 가져오기
        const userResponse = await axios.get('http://localhost:8080/user/profile', {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (userResponse.data && userResponse.data.code === 1000) {
          setUserId(userResponse.data.user.userIdx);
        } else {
          throw new Error('사용자 정보를 가져오는 중 오류 발생');
        }

        // 상품 제목 가져오기
        const productResponse = await axios.get(`http://localhost:8080/product/${productIdx}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (
          productResponse.data &&
          productResponse.data.data &&
          productResponse.data.data.product
        ) {
          setProductTitle(productResponse.data.data.product.title);
          setWriterId(productResponse.data.data.product.writerIdx);
        } else {
          throw new Error('상품 제목을 가져오는 중 오류 발생');
        }
      } catch (err) {
        console.error('데이터 요청 오류:', err);
        alert('데이터를 가져오는 데 실패했습니다. 다시 로그인해주세요.');
        navigate('/login');
      }
    };

    fetchInitialData();
  }, [navigate, productIdx]);

  useEffect(() => {
    if (productIdx && userId) {
      const fetchChats = async () => {
        setLoading(true);
        try {
          const response = await axios.get(`http://localhost:8080/chat/${productIdx}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
          });
  
          const data = response.data;
          if (data.chats) {
            setMessages(data.chats);
          } else {
            setMessages([]); // 채팅 기록이 없으면 빈 배열 설정
          }
        } catch (err) {
          setError('채팅 데이터를 불러오는 중 오류가 발생했습니다.');
          console.error(err);
        } finally {
          setLoading(false);
        }
      };
  
      fetchChats();
    }
  }, [productIdx, userId]);  

  const sendMessage = () => {
    if (!message.trim()) return;
    if (!userId) {
      alert('사용자 정보를 확인할 수 없습니다.');
      return;
    }

    axios
      .get(`http://localhost:8080/product/${productIdx}/writer`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      })
      .then((response) => {
        const recipientId = response.data.writerIdx;

        const newMessage = {
          senderId: userId,
          recipientId,
          messageContent: message,
          relatedProductId: parseInt(productIdx, 10),
          image: null,
        };

        axios
          .post('http://localhost:8080/chat/send', newMessage, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
          })
          .then((response) => {
            setMessages(response.data);
            setMessage('');
          })
          .catch((err) => {
            setError('메시지 전송 중 오류가 발생했습니다.');
            console.error(err);
          });
      })
      .catch((err) => {
        console.error('Writer fetch error:', err);
        alert('상품 작성자를 가져오는 데 실패했습니다.');
      });
  };

  const sendMoney = () => {
    if (!transferAmount.trim()) {
      alert('송금할 금액을 입력해주세요.');
      return;
    }
  
    const amount = parseInt(transferAmount, 10);
    if (isNaN(amount) || amount <= 0) {
      alert('올바른 금액을 입력하세요.');
      return;
    }
  
    axios
      .get(`http://localhost:8080/product/${productIdx}/writer`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      })
      .then((response) => {
        const recipientId = response.data.writerIdx;
  
        const transferData = {
          senderId: userId,
          recipientId,
          amount,
          productId: productIdx,
        };
  
        axios
          .post('http://localhost:8080/chat/transaction/transfer', transferData, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
          })
          .then((response) => {
            // 서버의 응답 데이터를 확인
            if (response.data.code === 1000) {
              alert('송금이 완료되었습니다.');
              setTransferAmount('');
              setIsModalOpen(false); // 모달 닫기
            } else {
              alert(response.data.message || '송금에 실패했습니다.');
            }
          })
          .catch((err) => {
            alert('송금 중 문제가 발생했습니다.');
            console.error(err);
          });
      })
      .catch((err) => {
        console.error('Writer fetch error:', err);
        alert('상품 작성자를 가져오는 데 실패했습니다.');
      });
  };  

  const completeTransaction = () => {
    const token = localStorage.getItem('token');
    axios
      .put(`http://localhost:8080/product/${productIdx}/complete`, null, {
        headers: { Authorization: `Bearer ${token}` },
        params: { buyer_idx: userId }, // 구매자 ID 전달
      })
      .then((response) => {
        if (response.data.code === "1000") {
          alert('거래가 완료되었습니다.');
        } else {
          alert(response.data.message || '거래 완료 처리에 실패했습니다.');
        }
      })
      .catch((err) => {
        console.error('거래 완료 처리 오류:', err);
        alert('거래 완료 처리 중 오류가 발생했습니다.');
      });
  };  

  const toggleModal = () => setIsModalOpen(!isModalOpen); // 모달 열기/닫기

  useEffect(() => {
    const chatContainer = document.querySelector('.chat-messages');
    if (chatContainer) {
      chatContainer.scrollTop = chatContainer.scrollHeight;
    }
  }, [messages]);

  if (error) return <div className="error-message">{error}</div>;
  if (loading) return <div className="loading-message">로딩 중...</div>;

  return (
    <div className="chat-container">
      <h2>상품 {productTitle} 채팅</h2>
      <div className="chat-messages">
        {messages.length > 0 ? (
          messages.map((msg, index) => (
            <div
              key={index}
              className={`chat-message ${msg.senderId === userId ? 'own-message' : ''}`}
            >
              <div className="chat-message-content">
                <b>{msg.senderId === userId ? '나' : msg.senderNickname || '알 수 없음'}</b>: {msg.messageContent}
                <div className="message-time">
                  {new Date(msg.sentAt).toLocaleTimeString()}
                </div>
                {msg.image && (
                  <img src={msg.image} alt="message-image" className="message-image" />
                )}
              </div>
            </div>
          ))
        ) : (
          <div className="no-messages">채팅 메시지가 없습니다.</div>
        )}
      </div>
      <div className="chat-input-section">
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="메시지를 입력하세요..."
          className="chat-input"
        />
        <button onClick={sendMessage} className="send-button">
          보내기
        </button>
        <button onClick={toggleModal} className="send-button">
          송금
        </button>
        {/* 판매자가 아닐 때만 거래 완료 버튼 표시 */}
        {userId !== writerId && (
          <button onClick={completeTransaction} className="send-button complete-button">
            거래 완료
          </button>
        )}
      </div>
      {isModalOpen && (
        <div className="modal">
          <div className="modal-content">
            <h3>송금 금액 입력</h3>
            <input
              type="text"
              value={transferAmount}
              onChange={(e) => setTransferAmount(e.target.value)}
              placeholder="송금 금액"
              className="modal-input"
            />
            <button onClick={sendMoney} className="modal-button">
              확인
            </button>
            <button onClick={toggleModal} className="modal-button modal-close">
              닫기
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Chat;