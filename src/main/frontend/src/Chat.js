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
  const [recipientId, setRecipientId] = useState(null); // 현재 대화 상대 ID
  const [productStatus, setProductStatus] = useState(''); // 상품 상태 (e.g., available, completed)
  const [showClickWarning, setShowClickWarning] = useState(false); // 닉네임 클릭 경고 상태

  // 사용자 정보 및 상품 정보 가져오기
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

        // 상품 정보 가져오기
        const productResponse = await axios.get(`http://localhost:8080/product/${productIdx}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (
          productResponse.data &&
          productResponse.data.data &&
          productResponse.data.data.product
        ) {
          const product = productResponse.data.data.product;
          setProductTitle(product.title);
          setWriterId(product.writerIdx);
          setProductStatus(product.status); // 상품 상태 설정
        } else {
          throw new Error('상품 정보를 가져오는 중 오류 발생');
        }
      } catch (err) {
        console.error('데이터 요청 오류:', err);
        alert('데이터를 가져오는 데 실패했습니다. 다시 로그인해주세요.');
        navigate('/login');
      }
    };

    fetchInitialData();
  }, [navigate, productIdx]);

  // 채팅 메시지 가져오기
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
            setRecipientId(writerId); // 메시지 기록이 없으면 writerId를 recipientId로 설정
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
  }, [productIdx, userId, writerId]);

  // 채팅 메시지 클릭 시 대화 상대 설정 (닉네임 기반)
  const handleMessageClick = (senderId, senderNickname) => {
    if (senderId !== userId) {
      setRecipientId(senderId); // 상대방 ID 설정
      alert(`${senderNickname}님과 대화 상대가 설정되었습니다.`); // 선택된 상대방 알림
      setShowClickWarning(false); // 경고 메시지 숨기기
    }
  };

  // 메시지 전송
  const sendMessage = () => {
    if (!message.trim()) return;
    if (!userId) {
      alert('사용자 정보를 확인할 수 없습니다.');
      return;
    }

    // recipientId가 설정되지 않은 경우 writerId를 기본값으로 사용
    const recipient = recipientId || writerId;

    if (!recipient) {
      alert('메시지를 보낼 대상이 설정되지 않았습니다.');
      return;
    }

    const newMessage = {
      senderId: userId,
      recipientId: recipient, // recipient 설정
      messageContent: message,
      relatedProductId: parseInt(productIdx, 10),
      image: null,
    };

    axios
      .post('http://localhost:8080/chat/send', newMessage, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      })
      .then((response) => {
        setMessages(response.data); // 메시지 리스트 업데이트
        setMessage(''); // 입력 필드 초기화
        if (!recipientId) setRecipientId(writerId); // writerId를 recipientId로 설정
      })
      .catch((err) => {
        setError('메시지 전송 중 오류가 발생했습니다.');
        console.error(err);
      });
  };

  // 송금
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
  
    const transferData = {
      senderId: userId,
      recipientId: writerId,
      amount,
    };
  
    console.log('송금 요청 데이터:', transferData);
  
    axios
      .post('http://localhost:8080/chat/transaction/transfer', transferData, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      })
      .then((response) => {
        console.log('송금 응답 데이터:', response.data);
        if (response.data.code === 1000) {
          alert('송금이 완료되었습니다.');
          setTransferAmount('');
          setIsModalOpen(false); // 모달 닫기
        } else {
          alert(response.data.message || '송금에 실패했습니다.');
        }
      })
      .catch((err) => {
        console.error('송금 요청 오류:', err);
        alert('송금 중 문제가 발생했습니다.');
      });
  };  

  // 거래 완료
  const completeTransaction = () => {
    if (!recipientId) {
      alert('거래 상대방이 선택되지 않았습니다. 채팅 메시지를 클릭하여 구매자를 선택하세요.');
      return;
    }

    const token = localStorage.getItem('token');

    axios
      .put(`http://localhost:8080/product/${productIdx}/complete`, null, {
        headers: { Authorization: `Bearer ${token}` },
        params: { buyer_idx: recipientId }, // recipientId를 구매자로 설정
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
      <p>메세지를 보내기 전에 상대방 닉네임을 눌러 주세요.<br></br>송금 및 거래 완료를 진행하기 전에 상대방 닉네임을 눌러 주세요.</p>
      <div className="chat-messages">
        {messages.length > 0 ? (
          messages.map((msg, index) => (
            <div
              key={index}
              className={`chat-message ${msg.senderId === userId ? 'own-message' : ''}`}
              onClick={() => handleMessageClick(msg.senderId, msg.senderNickname)} // 메시지 클릭 시 상대방 ID와 닉네임 전달
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
      {showClickWarning && messages.length > 0 && (
        <div className="warning-message">
          채팅을 보내기 전에 상대방의 닉네임을 클릭해주세요.
        </div>
      )}
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
        {/* 판매자일 때만 거래 완료 버튼 표시 & 상품 상태가 completed가 아닐 때만 */}
        {userId === writerId && productStatus !== 'completed' && (
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
