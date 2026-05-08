-- ============================================================
-- Mock Data for Ticket Lottery System
-- 모든 API 플로우 테스트용
-- ============================================================

-- 1. users (테스트 사용자 3명)
-- 비밀번호: password123 (BCrypt 인코딩)
INSERT INTO users (email, password, username, role, phone, seat_preference) VALUES
('user1@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '홍길동(눈침침)', 'USER', '010-1111-1111', 'EYESIGHT'),
('user2@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '김철수(다리불편)', 'USER', '010-2222-2222', 'LEG'),
('admin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '관리자', 'ADMIN', '010-9999-9999', 'NONE');

-- 2. ticket_events (공연/경기 4개)
INSERT INTO ticket_events (title, category, keyword, venue_name, venue_address, destination_latitude, destination_longitude, description, thumbnail_url, created_at, updated_at) VALUES
('임영웅 전국투어 콘서트 2026', 'CONCERT', '임영웅 콘서트 트로트 전국투어', 'KSPO DOME', '서울특별시 송파구 올림픽로 424', 37.5150176, 127.0729773,
 '대한민국 대표 가수 임영웅의 2026 전국투어 콘서트. 최고의 무대를 경험하세요.',
 'https://khureka-bucket.s3.ap-northeast-2.amazonaws.com/events/concert_lim.jpg', NOW(), NOW()),

('삼성 라이온즈 vs LG 트윈스', 'BASEBALL', '삼성 라이온즈 LG 트윈스 야구 프로야구', '대구삼성라이온즈파크', '대구광역시 수성구 야구전설로 1', 35.8419896, 128.6811467,
 '2026 KBO 리그 삼성 라이온즈 홈경기. 뜨거운 응원과 함께하세요!',
 'https://khureka-bucket.s3.ap-northeast-2.amazonaws.com/events/baseball_samsung.jpg', NOW(), NOW()),

('뮤지컬 오페라의 유령', 'MUSICAL', '오페라의유령 뮤지컬 브로드웨이', '블루스퀘어 신한카드홀', '서울특별시 용산구 이태원로 294', 37.5415021, 126.9936951,
 '세계적인 뮤지컬 오페라의 유령 내한공연. 감동의 무대를 만나보세요.',
 'https://khureka-bucket.s3.ap-northeast-2.amazonaws.com/events/musical_phantom.jpg', NOW(), NOW()),

('T1 vs Gen.G 결승전', 'ESPORTS', 'T1 GenG 롤드컵 이스포츠 LoL', '인천 송도컨벤시아', '인천광역시 연수구 센트럴로 123', 37.3878012, 126.6432053,
 '2026 LoL Champions Korea 결승전. T1과 Gen.G의 최종 대결!',
 'https://khureka-bucket.s3.ap-northeast-2.amazonaws.com/events/esports_lck.jpg', NOW(), NOW()),
('연극 햄릿', 'MUSICAL', '햄릿 연극 명동예술극장 클래식', '명동예술극장', '서울특별시 중구 남대문로 2길 12', 37.5642, 126.9847,
 '셰익스피어의 4대 비극 중 하나인 햄릿. 명동예술극장에서 만나는 고전의 감동.',
 'https://khureka-bucket.s3.ap-northeast-2.amazonaws.com/events/hamlet.jpg', NOW(), NOW()),
('뮤지컬 시카고', 'MUSICAL', '시카고 뮤지컬 재즈 브로드웨이', '디큐브 아트센터', '서울특별시 구로구 경인로 662', 37.5088, 126.8890,
 '올 타임 베스트셀러 뮤지컬 시카고. 화려한 무대와 재즈의 향연.',
 'https://khureka-bucket.s3.ap-northeast-2.amazonaws.com/events/chicago.jpg', NOW(), NOW());

-- 3. event_schedules (이벤트별 일정)
-- 임영웅 콘서트: 2일 공연
INSERT INTO event_schedules (event_id, start_time, end_time, application_open_at, application_close_at, lottery_at, status, created_at, updated_at) VALUES
(1, '2026-06-15 18:00:00', '2026-06-15 21:00:00', '2026-05-01 00:00:00', '2026-06-01 23:59:59', '2026-06-02 12:00:00', 'APPLICATION_OPEN', NOW(), NOW()),
(1, '2026-06-16 18:00:00', '2026-06-16 21:00:00', '2026-05-01 00:00:00', '2026-06-01 23:59:59', '2026-06-02 12:00:00', 'APPLICATION_OPEN', NOW(), NOW());

-- 야구: 1경기
INSERT INTO event_schedules (event_id, start_time, end_time, application_open_at, application_close_at, lottery_at, status, created_at, updated_at) VALUES
(2, '2026-06-20 18:30:00', '2026-06-20 22:00:00', '2026-05-01 00:00:00', '2026-06-10 23:59:59', '2026-06-11 10:00:00', 'APPLICATION_OPEN', NOW(), NOW());

-- 뮤지컬: 2회 공연
INSERT INTO event_schedules (event_id, start_time, end_time, application_open_at, application_close_at, lottery_at, status, created_at, updated_at) VALUES
(3, '2026-07-01 14:00:00', '2026-07-01 17:00:00', '2026-05-01 00:00:00', '2026-06-15 23:59:59', '2026-06-16 12:00:00', 'APPLICATION_OPEN', NOW(), NOW()),
(3, '2026-07-01 19:00:00', '2026-07-01 22:00:00', '2026-05-01 00:00:00', '2026-06-15 23:59:59', '2026-06-16 12:00:00', 'APPLICATION_OPEN', NOW(), NOW());

-- e스포츠: 1경기
INSERT INTO event_schedules (event_id, start_time, end_time, application_open_at, application_close_at, lottery_at, status, created_at, updated_at) VALUES
(4, '2026-07-10 17:00:00', '2026-07-10 22:00:00', '2026-05-01 00:00:00', '2026-06-30 23:59:59', '2026-07-01 12:00:00', 'APPLICATION_OPEN', NOW(), NOW()),

-- 연극 햄릿: 1일 공연
(5, '2026-07-15 19:30:00', '2026-07-15 22:00:00', '2026-06-01 00:00:00', '2026-07-10 23:59:59', '2026-07-11 12:00:00', 'APPLICATION_OPEN', NOW(), NOW()),

-- 뮤지컬 시카고: 1일 공연
(6, '2026-08-01 14:00:00', '2026-08-01 17:00:00', '2026-07-01 00:00:00', '2026-07-25 23:59:59', '2026-07-26 12:00:00', 'APPLICATION_OPEN', NOW(), NOW());

-- 4. seat_zones (일정별 좌석 구역)
-- 임영웅 콘서트 Day1 (schedule_id=1): 4개 구역
INSERT INTO seat_zones (schedule_id, name, price, created_at, updated_at) VALUES
(1, 'VIP석', 220000, NOW(), NOW()),
(1, 'R석', 154000, NOW(), NOW()),
(1, 'S석', 110000, NOW(), NOW()),
(1, 'A석', 77000, NOW(), NOW());

-- 임영웅 콘서트 Day2 (schedule_id=2): 4개 구역
INSERT INTO seat_zones (schedule_id, name, price, created_at, updated_at) VALUES
(2, 'VIP석', 220000, NOW(), NOW()),
(2, 'R석', 154000, NOW(), NOW()),
(2, 'S석', 110000, NOW(), NOW()),
(2, 'A석', 77000, NOW(), NOW());

-- 야구 (schedule_id=3): 5개 구역
INSERT INTO seat_zones (schedule_id, name, price, created_at, updated_at) VALUES
(3, '프리미엄석', 50000, NOW(), NOW()),
(3, '1루 내야석', 30000, NOW(), NOW()),
(3, '3루 내야석', 30000, NOW(), NOW()),
(3, '외야석', 15000, NOW(), NOW()),
(3, '응원석', 12000, NOW(), NOW());

-- 뮤지컬 Day1 (schedule_id=4): 3개 구역
INSERT INTO seat_zones (schedule_id, name, price, created_at, updated_at) VALUES
(4, 'VIP석', 170000, NOW(), NOW()),
(4, 'R석', 140000, NOW(), NOW()),
(4, 'S석', 100000, NOW(), NOW());

-- 뮤지컬 Day2 (schedule_id=5): 3개 구역
INSERT INTO seat_zones (schedule_id, name, price, created_at, updated_at) VALUES
(5, 'VIP석', 170000, NOW(), NOW()),
(5, 'R석', 140000, NOW(), NOW()),
(5, 'S석', 100000, NOW(), NOW());

-- e스포츠 (schedule_id=6): 3개 구역
INSERT INTO seat_zones (schedule_id, name, price, created_at, updated_at) VALUES
(6, '다이아몬드석', 88000, NOW(), NOW()),
(6, '골드석', 55000, NOW(), NOW()),
(6, '일반석', 33000, NOW(), NOW());

-- 5. seats (구역별 실제 좌석)
-- 좌석 생성 프로시저 대신, 핵심 구역만 좌석을 넣습니다.

-- == 임영웅 콘서트 Day1 ==
-- VIP석 (zone_id=1): 3열 x 5좌석 = 15석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(1,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(1,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(1,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(1,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(1,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(1,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(1,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(1,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(1,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(1,'B','5','AVAILABLE',2,5,true,NOW(),NOW()),
(1,'C','1','AVAILABLE',3,1,true,NOW(),NOW()),(1,'C','2','AVAILABLE',3,2,false,NOW(),NOW()),(1,'C','3','AVAILABLE',3,3,false,NOW(),NOW()),(1,'C','4','AVAILABLE',3,4,false,NOW(),NOW()),(1,'C','5','AVAILABLE',3,5,true,NOW(),NOW());

-- R석 (zone_id=2): 3열 x 5좌석 = 15석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(2,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(2,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(2,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(2,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(2,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(2,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(2,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(2,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(2,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(2,'B','5','AVAILABLE',2,5,true,NOW(),NOW()),
(2,'C','1','AVAILABLE',3,1,true,NOW(),NOW()),(2,'C','2','AVAILABLE',3,2,false,NOW(),NOW()),(2,'C','3','AVAILABLE',3,3,false,NOW(),NOW()),(2,'C','4','AVAILABLE',3,4,false,NOW(),NOW()),(2,'C','5','AVAILABLE',3,5,true,NOW(),NOW());

-- S석 (zone_id=3): 3열 x 5좌석 = 15석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(3,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(3,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(3,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(3,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(3,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(3,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(3,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(3,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(3,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(3,'B','5','AVAILABLE',2,5,true,NOW(),NOW()),
(3,'C','1','AVAILABLE',3,1,true,NOW(),NOW()),(3,'C','2','AVAILABLE',3,2,false,NOW(),NOW()),(3,'C','3','AVAILABLE',3,3,false,NOW(),NOW()),(3,'C','4','AVAILABLE',3,4,false,NOW(),NOW()),(3,'C','5','AVAILABLE',3,5,true,NOW(),NOW());

-- A석 (zone_id=4): 3열 x 5좌석 = 15석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(4,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(4,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(4,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(4,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(4,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(4,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(4,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(4,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(4,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(4,'B','5','AVAILABLE',2,5,true,NOW(),NOW()),
(4,'C','1','AVAILABLE',3,1,true,NOW(),NOW()),(4,'C','2','AVAILABLE',3,2,false,NOW(),NOW()),(4,'C','3','AVAILABLE',3,3,false,NOW(),NOW()),(4,'C','4','AVAILABLE',3,4,false,NOW(),NOW()),(4,'C','5','AVAILABLE',3,5,true,NOW(),NOW());

-- == 야구 ==
-- 프리미엄석 (zone_id=9): 2열 x 5좌석 = 10석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(9,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(9,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(9,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(9,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(9,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(9,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(9,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(9,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(9,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(9,'B','5','AVAILABLE',2,5,true,NOW(),NOW());

-- 1루 내야석 (zone_id=10): 2열 x 5좌석 = 10석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(10,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(10,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(10,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(10,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(10,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(10,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(10,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(10,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(10,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(10,'B','5','AVAILABLE',2,5,true,NOW(),NOW());

-- 3루 내야석 (zone_id=11): 2열 x 5좌석 = 10석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(11,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(11,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(11,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(11,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(11,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(11,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(11,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(11,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(11,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(11,'B','5','AVAILABLE',2,5,true,NOW(),NOW());

-- 외야석 (zone_id=12): 2열 x 5좌석 = 10석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(12,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(12,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(12,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(12,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(12,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(12,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(12,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(12,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(12,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(12,'B','5','AVAILABLE',2,5,true,NOW(),NOW());

-- 응원석 (zone_id=13): 2열 x 5좌석 = 10석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(13,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(13,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(13,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(13,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(13,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(13,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(13,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(13,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(13,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(13,'B','5','AVAILABLE',2,5,true,NOW(),NOW());

-- == e스포츠 ==
-- 다이아몬드석 (zone_id=20): 2열 x 5좌석 = 10석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(20,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(20,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(20,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(20,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(20,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(20,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(20,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(20,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(20,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(20,'B','5','AVAILABLE',2,5,true,NOW(),NOW());

-- 골드석 (zone_id=21): 2열 x 5좌석 = 10석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(21,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(21,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(21,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(21,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(21,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(21,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(21,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(21,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(21,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(21,'B','5','AVAILABLE',2,5,true,NOW(),NOW());

-- 일반석 (zone_id=22): 2열 x 5좌석 = 10석
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(22,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(22,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(22,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(22,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(22,'A','5','AVAILABLE',1,5,true,NOW(),NOW()),
(22,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(22,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(22,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(22,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(22,'B','5','AVAILABLE',2,5,true,NOW(),NOW());
