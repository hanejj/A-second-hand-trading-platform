
use gajimarket;

SET FOREIGN_KEY_CHECKS = 0;
truncate table user;
SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO `user` (id, passwd, name, birth, sex, phone, nickname, location, image, message, manner_point)
VALUES
('user1@gmail.com', 'passwd1', '홍길동', '1990-05-15', 'M', '010-1234-5678', '길동이', '서울특별시', '/uploads/user.png', '안녕하세요!', 50),
('user2@gmail.com', 'passwd2', '김철수', '1995-10-20', 'M', '010-9876-5432', '짱구', '부산광역시', '/uploads/user.png', '좋은 하루 되세요!', 50),
('user3@gmail.com', 'passwd3', '이소영', '1988-03-12', 'F', '010-5555-4444', '소영이', '대구광역시', '/uploads/user.png', '오늘도 화이팅!', 50),
('user4@gmail.com', 'passwd4', '한은진', '2000-07-25', 'F', '010-6666-3333', '은진이', '인천광역시', '/uploads/user.png', '따뜻한 하루!', 50),
('user5@gmail.com', 'passwd5', '강백호', '1993-01-30', 'M', '010-2222-1111', '슬램덩크', '광주광역시', '/uploads/user.png', '행복하세요~', 50);

SET FOREIGN_KEY_CHECKS = 0;
truncate table admin;
SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO admin (id, passwd, name)
values
('admin1@gaji.com','admin1','김관리');

SET FOREIGN_KEY_CHECKS = 0;
truncate table product;
truncate table keyword;
SET FOREIGN_KEY_CHECKS = 1;
-- Electronics 카테고리 데이터
INSERT INTO product (category, title, content, price, created_at, location, chat_num, heart_num, selling, image, writer_idx, writer_name, status)
VALUES
('Electronics', '아이폰 13 미니 팝니다', '거의 새 제품입니다. 정품 박스 포함.', 850000, DATE_ADD(NOW(), INTERVAL -1 DAY), '부산광역시', 5, 10, 'sell', '/uploads/iphone13mini.jpg', 1, '길동이', 'active'),
('Electronics', '아이패드 10세대 판매', '정품, 거의 새 제품입니다.', 500000, DATE_ADD(NOW(), INTERVAL -2 DAY), '서울특별시', 3, 7, 'sell', '/uploads/ipad10.jpg', 1, '길동이', 'active'),
('Electronics', '삼성 모니터 팔아요', '24인치 LED 모니터, 상태 양호.', 70000, DATE_ADD(NOW(), INTERVAL -3 DAY), '광주광역시', 0, 2, 'sell', '/uploads/monitor.jpg', 3, '소영이', 'active'),
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

-- Books 카테고리 데이터
INSERT INTO product (category, title, content, price, created_at, location, chat_num, heart_num, selling, image, writer_idx, writer_name, status)
VALUES
('Books', '코딩 입문서 팔아요', '초보자용 코딩 입문서, 상태 좋음.', 15000, DATE_ADD(NOW(), INTERVAL -1 DAY), '대전광역시', 1, 1, 'sell', '/uploads/coding_book.jpg', 1, '길동이', 'active'),
('Books', '영어 회화 책 팝니다', '영어 실력 향상에 좋아요.', 12000, DATE_ADD(NOW(), INTERVAL -3 DAY), '울산광역시', 1, 3, 'sell', '/uploads/english_book.jpg', 2, '짱구', 'active'),
('Books', '자기계발서 팔아요', '직장인을 위한 자기계발서.', 18000, DATE_ADD(NOW(), INTERVAL -5 DAY), '서울특별시', 2, 4, 'sell', '/uploads/self_help_book.jpg', 2, '짱구', 'active'),
('Books', '소설 책 팔아요', '재미있는 소설, 상태 좋음.', 10000, DATE_ADD(NOW(), INTERVAL -7 DAY), '인천광역시', 0, 2, 'sell', '/uploads/novel_book.jpg', 4, '은진이', 'active'),
('Books', '채식주의자 구해요', '읽고 싶어요.', 18000, DATE_ADD(NOW(), INTERVAL -5 DAY), '서울특별시', 2, 4, 'get', '/uploads/채식주의자.jpg', 2, '짱구', 'active'),
('Books', '소년이 온다 구해요', '읽고 싶어요.', 10000, DATE_ADD(NOW(), INTERVAL -7 DAY), '인천광역시', 0, 2, 'get', '/uploads/소년이온다.jpg', 4, '은진이', 'active');

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

INSERT INTO Wishlist (user_idx, product_idx, created_at)
VALUES
    ((SELECT user_idx FROM User WHERE id = 'user1@gmail.com'), 1, NOW()),
    ((SELECT user_idx FROM User WHERE id = 'user1@gmail.com'), 2, NOW()),
    ((SELECT user_idx FROM User WHERE id = 'user2@gmail.com'), 3, NOW());
