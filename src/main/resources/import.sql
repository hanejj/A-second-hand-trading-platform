
use gajimarket;

SET FOREIGN_KEY_CHECKS = 0;
truncate table user;
truncate table admin;
truncate table answer;
truncate table chat;
truncate table keyword;
truncate table notice;
truncate table point;
truncate table point_history;
truncate table product;
truncate table question;
truncate table report;
truncate table review;
truncate table user;
truncate table wishlist;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO user (id, passwd, name, birth, sex, phone, nickname, location, image, message, manner_point)
VALUES
('user1@gmail.com', 'passwd1', '홍길동', '1990-05-15', 'M', '010-1234-5678', '길동이', '서울특별시', '/uploads/user.png', '안녕하세요!', 50),
('user2@gmail.com', 'passwd2', '김철수', '1995-10-20', 'M', '010-9876-5432', '짱구', '부산광역시', '/uploads/user.png', '좋은 하루 되세요!', 50),
('user3@gmail.com', 'passwd3', '이소영', '1988-03-12', 'F', '010-5555-4444', '소영이', '대구광역시', '/uploads/user.png', '오늘도 화이팅!', 50),
('user4@gmail.com', 'passwd4', '한은진', '2000-07-25', 'F', '010-6666-3333', '은진이', '인천광역시', '/uploads/user.png', '따뜻한 하루!', 50),
('user5@gmail.com', 'passwd5', '강백호', '1993-01-30', 'M', '010-2222-1111', '슬램덩크', '광주광역시', '/uploads/user.png', '행복하세요~', 50);


INSERT INTO admin (id, passwd, name)
values
('admin1@gaji.com','admin1','김관리'),
('admin2@gaji.com','admin2','나관리');

-- Electronics 카테고리 데이터
INSERT INTO product (category, title, content, price, created_at, location, chat_num, heart_num, selling, image, writer_idx, writer_name, status)
VALUES
('Electronics', '아이폰 13 미니 팝니다', '거의 새 제품입니다. 정품 박스 포함.', 850000, DATE_ADD(NOW(), INTERVAL -1 DAY), '부산광역시', 5, 10, 'sell', '/uploads/iphone13mini.jpg', 1, '길동이', 'active'),
('Electronics', '아이패드 10세대 판매', '정품, 거의 새 제품입니다.', 500000, DATE_ADD(NOW(), INTERVAL -2 DAY), '서울특별시', 3, 7, 'sell', '/uploads/ipad10.jpg', 1, '길동이', 'completed'),
('Electronics', '삼성 모니터 팔아요', '24인치 LED 모니터, 상태 양호.', 70000, DATE_ADD(NOW(), INTERVAL -3 DAY), '광주광역시', 0, 2, 'sell', '/uploads/monitor.jpg', 3, '소영이', 'removed'),
('Electronics', 'PS5 팝니다', '플레이스테이션 5, 게임 포함.', 450000, DATE_ADD(NOW(), INTERVAL -5 DAY), '서울특별시 노원구', 6, 12, 'sell', '/uploads/ps5.jpg', 2, '짱구', 'active'),
('Electronics', '맥북 구해요', '간절히 필요합니다.', 200000, DATE_ADD(NOW(), INTERVAL -3 DAY), '광주광역시', 0, 2, 'get', '/uploads/macbook.jpg', 3, '소영이', 'active'),
('Electronics', '스위치 구해요', '닌텐도 스위치 구해요', 450000, DATE_ADD(NOW(), INTERVAL -5 DAY), '서울특별시 노원구', 6, 12, 'get', '/uploads/nintendo.jpg', 2, '짱구', 'active');

INSERT INTO keyword(product_idx, keyword)
VALUES
(1,'핸드폰'),(1,'아이폰'),(1,'애플'),
(2,'아이패드'),(2,'애플'),
(3,'컴퓨터'),(3,'삼성'),
(4,'게임기'),
(5,'컴퓨터'),(5,'노트북'),
(6,'게임기');

-- Fashion 카테고리 데이터
INSERT INTO product (category, title, content, price, created_at, location, chat_num, heart_num, selling, image, writer_idx, writer_name, status)
VALUES
('Fashion', '남성 정장 세트 판매', '사이즈 100, 1회 착용.', 50000, DATE_ADD(NOW(), INTERVAL -1 DAY), '인천광역시', 3, 6, 'sell', '/uploads/suit.jpg', 1, '길동이', 'active'),
('Fashion', '여성 겨울 코트 판매', '사이즈 M, 따뜻하고 가벼워요.', 40000, DATE_ADD(NOW(), INTERVAL -2 DAY), '수원시', 4, 8, 'sell', '/uploads/winter_coat.jpg', 1, '길동이', 'active'),
('Fashion', '여성 자켓 팝니다', '봄, 가을에 입기 좋은 자켓.', 25000, DATE_ADD(NOW(), INTERVAL -4 DAY), '대전광역시', 2, 4, 'sell', '/uploads/winter_jacket.jpg', 2, '짱구', 'active'),
('Fashion', '남성 티셔츠 판매', '사이즈 L, 상태 좋음.', 15000, DATE_ADD(NOW(), INTERVAL -6 DAY), '서울특별시', 5, 9, 'sell', '/uploads/mens_tshirt.jpg', 1, '길동이', 'active');

INSERT INTO keyword(product_idx, keyword)
VALUES
(7,'정장'),(7,'남성복'),(7,'겨울옷'),
(8,'아우터'),(8,'여성복'),(8,'겨울옷'),
(9,'여성복'),(9,'아우터'),
(10,'남성복');

-- Books 카테고리 데이터
INSERT INTO product (category, title, content, price, created_at, location, chat_num, heart_num, selling, image, writer_idx, writer_name, status)
VALUES
('Books', '코딩 입문서 팔아요', '초보자용 코딩 입문서, 상태 좋음.', 15000, DATE_ADD(NOW(), INTERVAL -1 DAY), '대전광역시', 1, 1, 'sell', '/uploads/coding_book.jpg', 1, '길동이', 'active'),
('Books', '영어 회화 책 팝니다', '영어 실력 향상에 좋아요.', 12000, DATE_ADD(NOW(), INTERVAL -3 DAY), '울산광역시', 1, 3, 'sell', '/uploads/english_book.jpg', 2, '짱구', 'active'),
('Books', '자기계발서 팔아요', '직장인을 위한 자기계발서.', 18000, DATE_ADD(NOW(), INTERVAL -5 DAY), '서울특별시', 2, 4, 'sell', '/uploads/self_help_book.jpg', 2, '짱구', 'active'),
('Books', '소설 책 팔아요', '재미있는 소설, 상태 좋음.', 10000, DATE_ADD(NOW(), INTERVAL -7 DAY), '인천광역시', 0, 2, 'sell', '/uploads/novel_book.jpg', 4, '은진이', 'active'),
('Books', '채식주의자 구해요', '읽고 싶어요.', 18000, DATE_ADD(NOW(), INTERVAL -5 DAY), '서울특별시', 2, 4, 'get', '/uploads/채식주의자.jpg', 2, '짱구', 'active'),
('Books', '소년이 온다 구해요', '읽고 싶어요.', 10000, DATE_ADD(NOW(), INTERVAL -7 DAY), '인천광역시', 0, 2, 'get', '/uploads/소년이온다.jpg', 4, '은진이', 'active');

INSERT INTO keyword(product_idx, keyword)
VALUES
(11,'코딩'),(11,'공부');

-- Furniture 카테고리 데이터
INSERT INTO product (category, title, content, price, created_at, location, chat_num, heart_num, selling, image, writer_idx, writer_name, status)
VALUES
('Furniture', '2인용 소파 판매', '사용감 있으나 깨끗합니다.', 30000, DATE_ADD(NOW(), INTERVAL -2 DAY), '서울특별시 강남구', 2, 4, 'sell', '/uploads/sofa.jpg', 3, '소영이', 'active'),
('Furniture', '침대 프레임 판매', '싱글 사이즈, 상태 양호.', 50000, DATE_ADD(NOW(), INTERVAL -4 DAY), '대구광역시', 2, 5, 'sell', '/uploads/bed_frame.jpg', 2, '짱구', 'active'),
('Furniture', '책상 팝니다', '원목 책상, 상태 좋습니다.', 100000, DATE_ADD(NOW(), INTERVAL -6 DAY), '광명시', 4, 8, 'sell', '/uploads/desk.jpg', 4, '은진이', 'active'),
('Furniture', '의자 판매', '편안한 의자, 여러 개 보유', 15000, DATE_ADD(NOW(), INTERVAL -8 DAY), '서울특별시', 3, 6, 'sell', '/uploads/chair.jpg', 5, '슬램덩크', 'active');

-- Other 카테고리 데이터
INSERT INTO product (category, title, content, price, created_at, location, chat_num, heart_num, selling, image, writer_idx, writer_name, status)
VALUES
('Other', '자전거 팔아요', '사용감 있으나 양호 상태', 150000, DATE_ADD(NOW(), INTERVAL -1 DAY), '대전광역시', 2, 3, 'sell', '/uploads/bike.jpg', 2, '짱구', 'active'),
('Other', '스포츠 용품 팝니다', '여러 가지 스포츠 용품 판매', 50000, DATE_ADD(NOW(), INTERVAL -3 DAY), '서울특별시', 1, 4, 'sell', '/uploads/sports_gear.jpg', 3, '소영이', 'active'),
('Other', '캠핑 장비 판매', '캠핑용 텐트와 용품들', 80000, DATE_ADD(NOW(), INTERVAL -5 DAY), '인천광역시', 3, 6, 'sell', '/uploads/camping_gear.jpg', 4, '은진이', 'active'),
('Other', '고양이 장난감 팝니다', '고양이를 위한 다양한 장난감', 15000, DATE_ADD(NOW(), INTERVAL -7 DAY), '광주광역시', 2, 5, 'sell', '/uploads/cat_toy.jpg', 5, '슬램덩크', 'active');

SET FOREIGN_KEY_CHECKS = 0;
truncate table review;
SET FOREIGN_KEY_CHECKS = 1;

SET FOREIGN_KEY_CHECKS = 0;
truncate table report;
SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO `Report` (`title`, `content`, `reporting_idx`, `status`, `reported_user`, `reported_product`, `created_at`)
VALUES
('상품 사기 신고', '이 상품은 사기를 치고 있는 것으로 보입니다. 구매자에게 피해를 입히고 있습니다.', 1, 'pending', 2, 5, '2024-11-29 10:30:00'),
('욕설 및 불법 내용 포함', '해당 사용자가 게시글에서 욕설을 사용하고 있습니다.', 3, 'resolved', 4, NULL, '2024-11-28 15:45:00'),
('거래 조건 불이행', '구매자가 약속된 거래 조건을 지키지 않았습니다.', 5, 'rejected', 2, 10, '2024-11-27 14:00:00'),
('허위 광고 신고', '해당 상품이 과장 광고로 판매되고 있습니다.', 4, 'pending', 1, 15, '2024-11-26 12:15:00'),
('상품 이미지 도용', '다른 판매자의 상품 이미지를 무단으로 사용한 것으로 보입니다.', 2, 'resolved', 3, 20, '2024-11-25 09:00:00');

SET FOREIGN_KEY_CHECKS = 0;
truncate table wishlist;
SET FOREIGN_KEY_CHECKS = 1;

-- Question 테이블에 샘플 데이터 삽입
INSERT INTO `Question` (title, content, created_at, public, user_idx, image)
VALUES
('첫 번째 질문', '이것은 첫 번째 질문 내용입니다.', DATE_ADD(NOW(), INTERVAL -7 DAY), 'y', 1, NULL),
('두 번째 질문', '이것은 두 번째 질문 내용입니다.', DATE_ADD(NOW(), INTERVAL -5 DAY), 'n', 2, '/uploads/novel_book.jpg'),
('세 번째 질문', '이것은 세 번째 질문 내용입니다.', DATE_ADD(NOW(), INTERVAL -3 DAY), 'y', 3, NULL),
('네 번째 질문', '이것은 네 번째 질문 내용입니다.', DATE_ADD(NOW(), INTERVAL -2 DAY), 'n', 4, '/uploads/novel_book.jpg'),
('다섯 번째 질문', '이것은 다섯 번째 질문 내용입니다.', DATE_ADD(NOW(), INTERVAL -1 DAY), 'y', 5, NULL);

-- Answer 테이블에 샘플 데이터 삽입
INSERT INTO `Answer` (title, content, created_at, public, question_idx, admin_index, image)
VALUES
('Re: 첫 번째 질문', '첫 번째 질문에 대한 답변입니다.', NOW(), 'y', 1, 1, '/uploads/novel_book.jpg'),
('Re: 네 번째 질문', '네 번째 질문에 대한 답변입니다.', NOW(), 'n', 4, 1, NULL);

INSERT INTO `Point_history` (user_idx, change_amount, transaction_type, created_at)
VALUES
(2, 1000, 'charge', '2024-11-19 10:00:00'),
(1, 1000, 'charge', '2024-11-19 12:00:00'),
(4, 1000, 'withdraw', '2024-11-19 14:00:00'),
(4, 1000, 'charge', '2024-11-19 16:00:00'),
(3, 1000, 'charge', '2024-11-19 18:00:00'),
(2, 2000, 'charge', '2024-11-19 20:00:00'),
(3, 2000, 'charge', '2024-11-19 22:00:00'),
(2, 1000, 'withdraw', '2024-11-20 00:00:00'),
(4, 1000, 'withdraw', '2024-11-20 02:00:00'),
(1, 1000, 'withdraw', '2024-11-20 04:00:00'),
(5, 1000, 'charge', '2024-11-20 06:00:00'),
(4, 2000, 'charge', '2024-11-20 08:00:00'),
(3, 1000, 'withdraw', '2024-11-20 10:00:00'),
(3, 2000, 'charge', '2024-11-20 12:00:00'),
(5, 2000, 'charge', '2024-11-21 10:00:00'),
(2, 3000, 'charge', '2024-11-21 12:00:00'),
(5, 3000, 'charge', '2024-11-21 14:00:00'),
(2, 1000, 'withdraw', '2024-11-22 10:00:00'),
(3, 1000, 'charge', '2024-11-22 12:00:00'),
(2, 1000, 'withdraw', '2024-11-22 14:00:00'),
(1, 1000, 'charge', '2024-11-23 10:00:00'),
(4, 1000, 'withdraw', '2024-11-23 12:00:00'),
(3, 1000, 'charge', '2024-11-23 14:00:00'),
(2, 2000, 'charge', '2024-11-24 10:00:00'),
(4, 1000, 'withdraw', '2024-11-24 12:00:00'),
(5, 1000, 'charge', '2024-11-24 14:00:00'),
(1, 1000, 'withdraw', '2024-11-25 10:00:00'),
(2, 1000, 'charge', '2024-11-25 12:00:00'),
(3, 2000, 'charge', '2024-11-25 14:00:00'),
(4, 1000, 'withdraw', '2024-11-26 10:00:00'),
(2, 1000, 'charge', '2024-11-26 12:00:00'),
(5, 2000, 'charge', '2024-11-26 14:00:00'),
(3, 1000, 'withdraw', '2024-11-27 10:00:00'),
(1, 1000, 'charge', '2024-11-27 12:00:00'),
(4, 2000, 'charge', '2024-11-27 14:00:00'),
(5, 2000, 'charge', '2024-11-28 10:00:00'),
(2, 2000, 'withdraw', '2024-11-28 12:00:00'),
(3, 2000, 'charge', '2024-11-28 14:00:00'),
(1, 2000, 'withdraw', '2024-11-29 10:00:00'),
(4, 1000, 'charge', '2024-11-29 12:00:00'),
(2, 2000, 'charge', '2024-11-29 14:00:00'),
(5, 1000, 'withdraw', '2024-11-30 10:00:00'),
(3, 2000, 'charge', '2024-11-30 12:00:00'),
(4, 2000, 'withdraw', '2024-11-30 14:00:00'),
(2, 3000, 'charge', '2024-12-01 10:00:00'),
(1, 1000, 'withdraw', '2024-12-01 12:00:00'),
(5, 2000, 'charge', '2024-12-01 14:00:00'),
(3, 1000, 'withdraw', '2024-12-02 10:00:00'),
(1, 2000, 'charge', '2024-12-02 12:00:00'),
(4, 1000, 'withdraw', '2024-12-02 14:00:00'),
(2, 4000, 'charge', '2024-12-03 10:00:00'),
(5, 1000, 'withdraw', '2024-12-03 12:00:00'),
(3, 2000, 'charge', '2024-12-03 14:00:00');

