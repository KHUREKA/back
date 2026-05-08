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
('임영웅 2026 앙코르 콘서트 [IM HERO]', 'CONCERT', '임영웅 콘서트 트로트 영웅시대', '고려대학교 화정체육관', '서울특별시 성북구 안암로 145', 37.590800, 127.027800,
 '어르신들의 영원한 히어로 임영웅의 화정체육관 특별 앙코르 콘서트! 최고의 무대를 경험하세요.',
 'https://khuthon-bucket.s3.ap-northeast-2.amazonaws.com/events/4661632b-9e75-443b-9b61-2562d49c7b61_%EC%9E%84%EC%98%81%EC%9B%85.png', NOW(), NOW()),

('삼성 라이온즈 vs LG 트윈스', 'BASEBALL', '삼성 라이온즈 LG 트윈스 야구 프로야구', '대구삼성라이온즈파크', '대구광역시 수성구 야구전설로 1', 35.8419896, 128.6811467,
 '2026 KBO 리그 삼성 라이온즈 홈경기. 뜨거운 응원과 함께하세요!',
 'https://khuthon-bucket.s3.ap-northeast-2.amazonaws.com/events/ebc52ea7-849c-469f-b553-7dcab3405807_LCK.png', NOW(), NOW()),

('뮤지컬 오페라의 유령', 'MUSICAL', '오페라의유령 뮤지컬 브로드웨이', '블루스퀘어 신한카드홀', '서울특별시 용산구 이태원로 294', 37.5415021, 126.9936951,
 '세계적인 뮤지컬 오페라의 유령 내한공연. 감동의 무대를 만나보세요.',
 'https://khuthon-bucket.s3.ap-northeast-2.amazonaws.com/events/7e7cb371-048f-430a-9d42-72e58d6ed211_%EC%98%A4%ED%8E%98%EB%9D%BC%EC%9D%98+%EC%9C%A0%EB%A0%B9.png', NOW(), NOW()),

('T1 vs Gen.G 결승전', 'ESPORTS', 'T1 GenG 롤드컵 이스포츠 LoL', '인천 송도컨벤시아', '인천광역시 연수구 센트럴로 123', 37.3878012, 126.6432053,
 '2026 LoL Champions Korea 결승전. T1과 Gen.G의 최종 대결!',
 'https://khuthon-bucket.s3.ap-northeast-2.amazonaws.com/events/7e7cb371-048f-430a-9d42-72e58d6ed211_%EC%98%A4%ED%8E%98%EB%9D%BC%EC%9D%98+%EC%9C%A0%EB%A0%B9.png', NOW(), NOW()),
('연극 햄릿', 'MUSICAL', '햄릿 연극 명동예술극장 클래식', '명동예술극장', '서울특별시 중구 남대문로 2길 12', 37.5642, 126.9847,
 '셰익스피어의 4대 비극 중 하나인 햄릿. 명동예술극장에서 만나는 고전의 감동.',
 'https://khuthon-bucket.s3.ap-northeast-2.amazonaws.com/events/8c490851-d0f4-46df-b6c0-2efd90622786_%ED%96%84%EB%A6%BF.png', NOW(), NOW()),
('뮤지컬 시카고', 'MUSICAL', '시카고 뮤지컬 재즈 브로드웨이', '디큐브 아트센터', '서울특별시 구로구 경인로 662', 37.5088, 126.8890,
 '올 타임 베스트셀러 뮤지컬 시카고. 화려한 무대와 재즈의 향연.',
 'https://khuthon-bucket.s3.ap-northeast-2.amazonaws.com/events/72223f87-15f5-40dd-bf2c-7e754b476148_%EC%8B%9C%EC%B9%B4%EA%B3%A0.png', NOW(), NOW());

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

-- 연극 햄릿 (schedule_id=7): 3개 구역
INSERT INTO seat_zones (schedule_id, name, price, created_at, updated_at) VALUES
(7, 'VIP석', 150000, NOW(), NOW()),
(7, 'R석', 120000, NOW(), NOW()),
(7, 'S석', 90000, NOW(), NOW());

-- 뮤지컬 시카고 (schedule_id=8): 3개 구역
INSERT INTO seat_zones (schedule_id, name, price, created_at, updated_at) VALUES
(8, 'VIP석', 160000, NOW(), NOW()),
(8, 'R석', 130000, NOW(), NOW()),
(8, 'S석', 100000, NOW(), NOW());

-- 5. seats (구역별 실제 좌석)
-- 좌석 생성 프로시저 대신, 핵심 구역만 좌석을 넣습니다.

-- == 임영웅 콘서트 Day1 ==
-- VIP석 (FLOOR 구역) 200석, R석 (1층 스탠드) 200석, S석 (2층 스탠드) 200석
-- Zone 1 (10x20 = 200 seats)
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(1,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(1,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(1,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(1,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(1,'A','5','AVAILABLE',1,5,false,NOW(),NOW()),
(1,'A','6','AVAILABLE',1,6,false,NOW(),NOW()),(1,'A','7','AVAILABLE',1,7,false,NOW(),NOW()),(1,'A','8','AVAILABLE',1,8,false,NOW(),NOW()),(1,'A','9','AVAILABLE',1,9,false,NOW(),NOW()),(1,'A','10','AVAILABLE',1,10,true,NOW(),NOW()),
(1,'A','11','AVAILABLE',1,11,true,NOW(),NOW()),(1,'A','12','AVAILABLE',1,12,false,NOW(),NOW()),(1,'A','13','AVAILABLE',1,13,false,NOW(),NOW()),(1,'A','14','AVAILABLE',1,14,false,NOW(),NOW()),(1,'A','15','AVAILABLE',1,15,false,NOW(),NOW()),
(1,'A','16','AVAILABLE',1,16,false,NOW(),NOW()),(1,'A','17','AVAILABLE',1,17,false,NOW(),NOW()),(1,'A','18','AVAILABLE',1,18,false,NOW(),NOW()),(1,'A','19','AVAILABLE',1,19,false,NOW(),NOW()),(1,'A','20','AVAILABLE',1,20,true,NOW(),NOW()),
(1,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(1,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(1,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(1,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(1,'B','5','AVAILABLE',2,5,false,NOW(),NOW()),
(1,'B','6','AVAILABLE',2,6,false,NOW(),NOW()),(1,'B','7','AVAILABLE',2,7,false,NOW(),NOW()),(1,'B','8','AVAILABLE',2,8,false,NOW(),NOW()),(1,'B','9','AVAILABLE',2,9,false,NOW(),NOW()),(1,'B','10','AVAILABLE',2,10,true,NOW(),NOW()),
(1,'B','11','AVAILABLE',2,11,true,NOW(),NOW()),(1,'B','12','AVAILABLE',2,12,false,NOW(),NOW()),(1,'B','13','AVAILABLE',2,13,false,NOW(),NOW()),(1,'B','14','AVAILABLE',2,14,false,NOW(),NOW()),(1,'B','15','AVAILABLE',2,15,false,NOW(),NOW()),
(1,'B','16','AVAILABLE',2,16,false,NOW(),NOW()),(1,'B','17','AVAILABLE',2,17,false,NOW(),NOW()),(1,'B','18','AVAILABLE',2,18,false,NOW(),NOW()),(1,'B','19','AVAILABLE',2,19,false,NOW(),NOW()),(1,'B','20','AVAILABLE',2,20,true,NOW(),NOW()),
(1,'C','1','AVAILABLE',3,1,true,NOW(),NOW()),(1,'C','2','AVAILABLE',3,2,false,NOW(),NOW()),(1,'C','3','AVAILABLE',3,3,false,NOW(),NOW()),(1,'C','4','AVAILABLE',3,4,false,NOW(),NOW()),(1,'C','5','AVAILABLE',3,5,false,NOW(),NOW()),
(1,'C','6','AVAILABLE',3,6,false,NOW(),NOW()),(1,'C','7','AVAILABLE',3,7,false,NOW(),NOW()),(1,'C','8','AVAILABLE',3,8,false,NOW(),NOW()),(1,'C','9','AVAILABLE',3,9,false,NOW(),NOW()),(1,'C','10','AVAILABLE',3,10,true,NOW(),NOW()),
(1,'C','11','AVAILABLE',3,11,true,NOW(),NOW()),(1,'C','12','AVAILABLE',3,12,false,NOW(),NOW()),(1,'C','13','AVAILABLE',3,13,false,NOW(),NOW()),(1,'C','14','AVAILABLE',3,14,false,NOW(),NOW()),(1,'C','15','AVAILABLE',3,15,false,NOW(),NOW()),
(1,'C','16','AVAILABLE',3,16,false,NOW(),NOW()),(1,'C','17','AVAILABLE',3,17,false,NOW(),NOW()),(1,'C','18','AVAILABLE',3,18,false,NOW(),NOW()),(1,'C','19','AVAILABLE',3,19,false,NOW(),NOW()),(1,'C','20','AVAILABLE',3,20,true,NOW(),NOW()),
(1,'D','1','AVAILABLE',4,1,true,NOW(),NOW()),(1,'D','2','AVAILABLE',4,2,false,NOW(),NOW()),(1,'D','3','AVAILABLE',4,3,false,NOW(),NOW()),(1,'D','4','AVAILABLE',4,4,false,NOW(),NOW()),(1,'D','5','AVAILABLE',4,5,false,NOW(),NOW()),
(1,'D','6','AVAILABLE',4,6,false,NOW(),NOW()),(1,'D','7','AVAILABLE',4,7,false,NOW(),NOW()),(1,'D','8','AVAILABLE',4,8,false,NOW(),NOW()),(1,'D','9','AVAILABLE',4,9,false,NOW(),NOW()),(1,'D','10','AVAILABLE',4,10,true,NOW(),NOW()),
(1,'D','11','AVAILABLE',4,11,true,NOW(),NOW()),(1,'D','12','AVAILABLE',4,12,false,NOW(),NOW()),(1,'D','13','AVAILABLE',4,13,false,NOW(),NOW()),(1,'D','14','AVAILABLE',4,14,false,NOW(),NOW()),(1,'D','15','AVAILABLE',4,15,false,NOW(),NOW()),
(1,'D','16','AVAILABLE',4,16,false,NOW(),NOW()),(1,'D','17','AVAILABLE',4,17,false,NOW(),NOW()),(1,'D','18','AVAILABLE',4,18,false,NOW(),NOW()),(1,'D','19','AVAILABLE',4,19,false,NOW(),NOW()),(1,'D','20','AVAILABLE',4,20,true,NOW(),NOW()),
(1,'E','1','AVAILABLE',5,1,true,NOW(),NOW()),(1,'E','2','AVAILABLE',5,2,false,NOW(),NOW()),(1,'E','3','AVAILABLE',5,3,false,NOW(),NOW()),(1,'E','4','AVAILABLE',5,4,false,NOW(),NOW()),(1,'E','5','AVAILABLE',5,5,false,NOW(),NOW()),
(1,'E','6','AVAILABLE',5,6,false,NOW(),NOW()),(1,'E','7','AVAILABLE',5,7,false,NOW(),NOW()),(1,'E','8','AVAILABLE',5,8,false,NOW(),NOW()),(1,'E','9','AVAILABLE',5,9,false,NOW(),NOW()),(1,'E','10','AVAILABLE',5,10,true,NOW(),NOW()),
(1,'E','11','AVAILABLE',5,11,true,NOW(),NOW()),(1,'E','12','AVAILABLE',5,12,false,NOW(),NOW()),(1,'E','13','AVAILABLE',5,13,false,NOW(),NOW()),(1,'E','14','AVAILABLE',5,14,false,NOW(),NOW()),(1,'E','15','AVAILABLE',5,15,false,NOW(),NOW()),
(1,'E','16','AVAILABLE',5,16,false,NOW(),NOW()),(1,'E','17','AVAILABLE',5,17,false,NOW(),NOW()),(1,'E','18','AVAILABLE',5,18,false,NOW(),NOW()),(1,'E','19','AVAILABLE',5,19,false,NOW(),NOW()),(1,'E','20','AVAILABLE',5,20,true,NOW(),NOW()),
(1,'F','1','AVAILABLE',6,1,true,NOW(),NOW()),(1,'F','2','AVAILABLE',6,2,false,NOW(),NOW()),(1,'F','3','AVAILABLE',6,3,false,NOW(),NOW()),(1,'F','4','AVAILABLE',6,4,false,NOW(),NOW()),(1,'F','5','AVAILABLE',6,5,false,NOW(),NOW()),
(1,'F','6','AVAILABLE',6,6,false,NOW(),NOW()),(1,'F','7','AVAILABLE',6,7,false,NOW(),NOW()),(1,'F','8','AVAILABLE',6,8,false,NOW(),NOW()),(1,'F','9','AVAILABLE',6,9,false,NOW(),NOW()),(1,'F','10','AVAILABLE',6,10,true,NOW(),NOW()),
(1,'F','11','AVAILABLE',6,11,true,NOW(),NOW()),(1,'F','12','AVAILABLE',6,12,false,NOW(),NOW()),(1,'F','13','AVAILABLE',6,13,false,NOW(),NOW()),(1,'F','14','AVAILABLE',6,14,false,NOW(),NOW()),(1,'F','15','AVAILABLE',6,15,false,NOW(),NOW()),
(1,'F','16','AVAILABLE',6,16,false,NOW(),NOW()),(1,'F','17','AVAILABLE',6,17,false,NOW(),NOW()),(1,'F','18','AVAILABLE',6,18,false,NOW(),NOW()),(1,'F','19','AVAILABLE',6,19,false,NOW(),NOW()),(1,'F','20','AVAILABLE',6,20,true,NOW(),NOW()),
(1,'G','1','AVAILABLE',7,1,true,NOW(),NOW()),(1,'G','2','AVAILABLE',7,2,false,NOW(),NOW()),(1,'G','3','AVAILABLE',7,3,false,NOW(),NOW()),(1,'G','4','AVAILABLE',7,4,false,NOW(),NOW()),(1,'G','5','AVAILABLE',7,5,false,NOW(),NOW()),
(1,'G','6','AVAILABLE',7,6,false,NOW(),NOW()),(1,'G','7','AVAILABLE',7,7,false,NOW(),NOW()),(1,'G','8','AVAILABLE',7,8,false,NOW(),NOW()),(1,'G','9','AVAILABLE',7,9,false,NOW(),NOW()),(1,'G','10','AVAILABLE',7,10,true,NOW(),NOW()),
(1,'G','11','AVAILABLE',7,11,true,NOW(),NOW()),(1,'G','12','AVAILABLE',7,12,false,NOW(),NOW()),(1,'G','13','AVAILABLE',7,13,false,NOW(),NOW()),(1,'G','14','AVAILABLE',7,14,false,NOW(),NOW()),(1,'G','15','AVAILABLE',7,15,false,NOW(),NOW()),
(1,'G','16','AVAILABLE',7,16,false,NOW(),NOW()),(1,'G','17','AVAILABLE',7,17,false,NOW(),NOW()),(1,'G','18','AVAILABLE',7,18,false,NOW(),NOW()),(1,'G','19','AVAILABLE',7,19,false,NOW(),NOW()),(1,'G','20','AVAILABLE',7,20,true,NOW(),NOW()),
(1,'H','1','AVAILABLE',8,1,true,NOW(),NOW()),(1,'H','2','AVAILABLE',8,2,false,NOW(),NOW()),(1,'H','3','AVAILABLE',8,3,false,NOW(),NOW()),(1,'H','4','AVAILABLE',8,4,false,NOW(),NOW()),(1,'H','5','AVAILABLE',8,5,false,NOW(),NOW()),
(1,'H','6','AVAILABLE',8,6,false,NOW(),NOW()),(1,'H','7','AVAILABLE',8,7,false,NOW(),NOW()),(1,'H','8','AVAILABLE',8,8,false,NOW(),NOW()),(1,'H','9','AVAILABLE',8,9,false,NOW(),NOW()),(1,'H','10','AVAILABLE',8,10,true,NOW(),NOW()),
(1,'H','11','AVAILABLE',8,11,true,NOW(),NOW()),(1,'H','12','AVAILABLE',8,12,false,NOW(),NOW()),(1,'H','13','AVAILABLE',8,13,false,NOW(),NOW()),(1,'H','14','AVAILABLE',8,14,false,NOW(),NOW()),(1,'H','15','AVAILABLE',8,15,false,NOW(),NOW()),
(1,'H','16','AVAILABLE',8,16,false,NOW(),NOW()),(1,'H','17','AVAILABLE',8,17,false,NOW(),NOW()),(1,'H','18','AVAILABLE',8,18,false,NOW(),NOW()),(1,'H','19','AVAILABLE',8,19,false,NOW(),NOW()),(1,'H','20','AVAILABLE',8,20,true,NOW(),NOW()),
(1,'I','1','AVAILABLE',9,1,true,NOW(),NOW()),(1,'I','2','AVAILABLE',9,2,false,NOW(),NOW()),(1,'I','3','AVAILABLE',9,3,false,NOW(),NOW()),(1,'I','4','AVAILABLE',9,4,false,NOW(),NOW()),(1,'I','5','AVAILABLE',9,5,false,NOW(),NOW()),
(1,'I','6','AVAILABLE',9,6,false,NOW(),NOW()),(1,'I','7','AVAILABLE',9,7,false,NOW(),NOW()),(1,'I','8','AVAILABLE',9,8,false,NOW(),NOW()),(1,'I','9','AVAILABLE',9,9,false,NOW(),NOW()),(1,'I','10','AVAILABLE',9,10,true,NOW(),NOW()),
(1,'I','11','AVAILABLE',9,11,true,NOW(),NOW()),(1,'I','12','AVAILABLE',9,12,false,NOW(),NOW()),(1,'I','13','AVAILABLE',9,13,false,NOW(),NOW()),(1,'I','14','AVAILABLE',9,14,false,NOW(),NOW()),(1,'I','15','AVAILABLE',9,15,false,NOW(),NOW()),
(1,'I','16','AVAILABLE',9,16,false,NOW(),NOW()),(1,'I','17','AVAILABLE',9,17,false,NOW(),NOW()),(1,'I','18','AVAILABLE',9,18,false,NOW(),NOW()),(1,'I','19','AVAILABLE',9,19,false,NOW(),NOW()),(1,'I','20','AVAILABLE',9,20,true,NOW(),NOW()),
(1,'J','1','AVAILABLE',10,1,true,NOW(),NOW()),(1,'J','2','AVAILABLE',10,2,false,NOW(),NOW()),(1,'J','3','AVAILABLE',10,3,false,NOW(),NOW()),(1,'J','4','AVAILABLE',10,4,false,NOW(),NOW()),(1,'J','5','AVAILABLE',10,5,false,NOW(),NOW()),
(1,'J','6','AVAILABLE',10,6,false,NOW(),NOW()),(1,'J','7','AVAILABLE',10,7,false,NOW(),NOW()),(1,'J','8','AVAILABLE',10,8,false,NOW(),NOW()),(1,'J','9','AVAILABLE',10,9,false,NOW(),NOW()),(1,'J','10','AVAILABLE',10,10,true,NOW(),NOW()),
(1,'J','11','AVAILABLE',10,11,true,NOW(),NOW()),(1,'J','12','AVAILABLE',10,12,false,NOW(),NOW()),(1,'J','13','AVAILABLE',10,13,false,NOW(),NOW()),(1,'J','14','AVAILABLE',10,14,false,NOW(),NOW()),(1,'J','15','AVAILABLE',10,15,false,NOW(),NOW()),
(1,'J','16','AVAILABLE',10,16,false,NOW(),NOW()),(1,'J','17','AVAILABLE',10,17,false,NOW(),NOW()),(1,'J','18','AVAILABLE',10,18,false,NOW(),NOW()),(1,'J','19','AVAILABLE',10,19,false,NOW(),NOW()),(1,'J','20','AVAILABLE',10,20,true,NOW(),NOW());

-- Zone 2 (10x20 = 200 seats)
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(2,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(2,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(2,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(2,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(2,'A','5','AVAILABLE',1,5,false,NOW(),NOW()),
(2,'A','6','AVAILABLE',1,6,false,NOW(),NOW()),(2,'A','7','AVAILABLE',1,7,false,NOW(),NOW()),(2,'A','8','AVAILABLE',1,8,false,NOW(),NOW()),(2,'A','9','AVAILABLE',1,9,false,NOW(),NOW()),(2,'A','10','AVAILABLE',1,10,true,NOW(),NOW()),
(2,'A','11','AVAILABLE',1,11,true,NOW(),NOW()),(2,'A','12','AVAILABLE',1,12,false,NOW(),NOW()),(2,'A','13','AVAILABLE',1,13,false,NOW(),NOW()),(2,'A','14','AVAILABLE',1,14,false,NOW(),NOW()),(2,'A','15','AVAILABLE',1,15,false,NOW(),NOW()),
(2,'A','16','AVAILABLE',1,16,false,NOW(),NOW()),(2,'A','17','AVAILABLE',1,17,false,NOW(),NOW()),(2,'A','18','AVAILABLE',1,18,false,NOW(),NOW()),(2,'A','19','AVAILABLE',1,19,false,NOW(),NOW()),(2,'A','20','AVAILABLE',1,20,true,NOW(),NOW()),
(2,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(2,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(2,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(2,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(2,'B','5','AVAILABLE',2,5,false,NOW(),NOW()),
(2,'B','6','AVAILABLE',2,6,false,NOW(),NOW()),(2,'B','7','AVAILABLE',2,7,false,NOW(),NOW()),(2,'B','8','AVAILABLE',2,8,false,NOW(),NOW()),(2,'B','9','AVAILABLE',2,9,false,NOW(),NOW()),(2,'B','10','AVAILABLE',2,10,true,NOW(),NOW()),
(2,'B','11','AVAILABLE',2,11,true,NOW(),NOW()),(2,'B','12','AVAILABLE',2,12,false,NOW(),NOW()),(2,'B','13','AVAILABLE',2,13,false,NOW(),NOW()),(2,'B','14','AVAILABLE',2,14,false,NOW(),NOW()),(2,'B','15','AVAILABLE',2,15,false,NOW(),NOW()),
(2,'B','16','AVAILABLE',2,16,false,NOW(),NOW()),(2,'B','17','AVAILABLE',2,17,false,NOW(),NOW()),(2,'B','18','AVAILABLE',2,18,false,NOW(),NOW()),(2,'B','19','AVAILABLE',2,19,false,NOW(),NOW()),(2,'B','20','AVAILABLE',2,20,true,NOW(),NOW()),
(2,'C','1','AVAILABLE',3,1,true,NOW(),NOW()),(2,'C','2','AVAILABLE',3,2,false,NOW(),NOW()),(2,'C','3','AVAILABLE',3,3,false,NOW(),NOW()),(2,'C','4','AVAILABLE',3,4,false,NOW(),NOW()),(2,'C','5','AVAILABLE',3,5,false,NOW(),NOW()),
(2,'C','6','AVAILABLE',3,6,false,NOW(),NOW()),(2,'C','7','AVAILABLE',3,7,false,NOW(),NOW()),(2,'C','8','AVAILABLE',3,8,false,NOW(),NOW()),(2,'C','9','AVAILABLE',3,9,false,NOW(),NOW()),(2,'C','10','AVAILABLE',3,10,true,NOW(),NOW()),
(2,'C','11','AVAILABLE',3,11,true,NOW(),NOW()),(2,'C','12','AVAILABLE',3,12,false,NOW(),NOW()),(2,'C','13','AVAILABLE',3,13,false,NOW(),NOW()),(2,'C','14','AVAILABLE',3,14,false,NOW(),NOW()),(2,'C','15','AVAILABLE',3,15,false,NOW(),NOW()),
(2,'C','16','AVAILABLE',3,16,false,NOW(),NOW()),(2,'C','17','AVAILABLE',3,17,false,NOW(),NOW()),(2,'C','18','AVAILABLE',3,18,false,NOW(),NOW()),(2,'C','19','AVAILABLE',3,19,false,NOW(),NOW()),(2,'C','20','AVAILABLE',3,20,true,NOW(),NOW()),
(2,'D','1','AVAILABLE',4,1,true,NOW(),NOW()),(2,'D','2','AVAILABLE',4,2,false,NOW(),NOW()),(2,'D','3','AVAILABLE',4,3,false,NOW(),NOW()),(2,'D','4','AVAILABLE',4,4,false,NOW(),NOW()),(2,'D','5','AVAILABLE',4,5,false,NOW(),NOW()),
(2,'D','6','AVAILABLE',4,6,false,NOW(),NOW()),(2,'D','7','AVAILABLE',4,7,false,NOW(),NOW()),(2,'D','8','AVAILABLE',4,8,false,NOW(),NOW()),(2,'D','9','AVAILABLE',4,9,false,NOW(),NOW()),(2,'D','10','AVAILABLE',4,10,true,NOW(),NOW()),
(2,'D','11','AVAILABLE',4,11,true,NOW(),NOW()),(2,'D','12','AVAILABLE',4,12,false,NOW(),NOW()),(2,'D','13','AVAILABLE',4,13,false,NOW(),NOW()),(2,'D','14','AVAILABLE',4,14,false,NOW(),NOW()),(2,'D','15','AVAILABLE',4,15,false,NOW(),NOW()),
(2,'D','16','AVAILABLE',4,16,false,NOW(),NOW()),(2,'D','17','AVAILABLE',4,17,false,NOW(),NOW()),(2,'D','18','AVAILABLE',4,18,false,NOW(),NOW()),(2,'D','19','AVAILABLE',4,19,false,NOW(),NOW()),(2,'D','20','AVAILABLE',4,20,true,NOW(),NOW()),
(2,'E','1','AVAILABLE',5,1,true,NOW(),NOW()),(2,'E','2','AVAILABLE',5,2,false,NOW(),NOW()),(2,'E','3','AVAILABLE',5,3,false,NOW(),NOW()),(2,'E','4','AVAILABLE',5,4,false,NOW(),NOW()),(2,'E','5','AVAILABLE',5,5,false,NOW(),NOW()),
(2,'E','6','AVAILABLE',5,6,false,NOW(),NOW()),(2,'E','7','AVAILABLE',5,7,false,NOW(),NOW()),(2,'E','8','AVAILABLE',5,8,false,NOW(),NOW()),(2,'E','9','AVAILABLE',5,9,false,NOW(),NOW()),(2,'E','10','AVAILABLE',5,10,true,NOW(),NOW()),
(2,'E','11','AVAILABLE',5,11,true,NOW(),NOW()),(2,'E','12','AVAILABLE',5,12,false,NOW(),NOW()),(2,'E','13','AVAILABLE',5,13,false,NOW(),NOW()),(2,'E','14','AVAILABLE',5,14,false,NOW(),NOW()),(2,'E','15','AVAILABLE',5,15,false,NOW(),NOW()),
(2,'E','16','AVAILABLE',5,16,false,NOW(),NOW()),(2,'E','17','AVAILABLE',5,17,false,NOW(),NOW()),(2,'E','18','AVAILABLE',5,18,false,NOW(),NOW()),(2,'E','19','AVAILABLE',5,19,false,NOW(),NOW()),(2,'E','20','AVAILABLE',5,20,true,NOW(),NOW()),
(2,'F','1','AVAILABLE',6,1,true,NOW(),NOW()),(2,'F','2','AVAILABLE',6,2,false,NOW(),NOW()),(2,'F','3','AVAILABLE',6,3,false,NOW(),NOW()),(2,'F','4','AVAILABLE',6,4,false,NOW(),NOW()),(2,'F','5','AVAILABLE',6,5,false,NOW(),NOW()),
(2,'F','6','AVAILABLE',6,6,false,NOW(),NOW()),(2,'F','7','AVAILABLE',6,7,false,NOW(),NOW()),(2,'F','8','AVAILABLE',6,8,false,NOW(),NOW()),(2,'F','9','AVAILABLE',6,9,false,NOW(),NOW()),(2,'F','10','AVAILABLE',6,10,true,NOW(),NOW()),
(2,'F','11','AVAILABLE',6,11,true,NOW(),NOW()),(2,'F','12','AVAILABLE',6,12,false,NOW(),NOW()),(2,'F','13','AVAILABLE',6,13,false,NOW(),NOW()),(2,'F','14','AVAILABLE',6,14,false,NOW(),NOW()),(2,'F','15','AVAILABLE',6,15,false,NOW(),NOW()),
(2,'F','16','AVAILABLE',6,16,false,NOW(),NOW()),(2,'F','17','AVAILABLE',6,17,false,NOW(),NOW()),(2,'F','18','AVAILABLE',6,18,false,NOW(),NOW()),(2,'F','19','AVAILABLE',6,19,false,NOW(),NOW()),(2,'F','20','AVAILABLE',6,20,true,NOW(),NOW()),
(2,'G','1','AVAILABLE',7,1,true,NOW(),NOW()),(2,'G','2','AVAILABLE',7,2,false,NOW(),NOW()),(2,'G','3','AVAILABLE',7,3,false,NOW(),NOW()),(2,'G','4','AVAILABLE',7,4,false,NOW(),NOW()),(2,'G','5','AVAILABLE',7,5,false,NOW(),NOW()),
(2,'G','6','AVAILABLE',7,6,false,NOW(),NOW()),(2,'G','7','AVAILABLE',7,7,false,NOW(),NOW()),(2,'G','8','AVAILABLE',7,8,false,NOW(),NOW()),(2,'G','9','AVAILABLE',7,9,false,NOW(),NOW()),(2,'G','10','AVAILABLE',7,10,true,NOW(),NOW()),
(2,'G','11','AVAILABLE',7,11,true,NOW(),NOW()),(2,'G','12','AVAILABLE',7,12,false,NOW(),NOW()),(2,'G','13','AVAILABLE',7,13,false,NOW(),NOW()),(2,'G','14','AVAILABLE',7,14,false,NOW(),NOW()),(2,'G','15','AVAILABLE',7,15,false,NOW(),NOW()),
(2,'G','16','AVAILABLE',7,16,false,NOW(),NOW()),(2,'G','17','AVAILABLE',7,17,false,NOW(),NOW()),(2,'G','18','AVAILABLE',7,18,false,NOW(),NOW()),(2,'G','19','AVAILABLE',7,19,false,NOW(),NOW()),(2,'G','20','AVAILABLE',7,20,true,NOW(),NOW()),
(2,'H','1','AVAILABLE',8,1,true,NOW(),NOW()),(2,'H','2','AVAILABLE',8,2,false,NOW(),NOW()),(2,'H','3','AVAILABLE',8,3,false,NOW(),NOW()),(2,'H','4','AVAILABLE',8,4,false,NOW(),NOW()),(2,'H','5','AVAILABLE',8,5,false,NOW(),NOW()),
(2,'H','6','AVAILABLE',8,6,false,NOW(),NOW()),(2,'H','7','AVAILABLE',8,7,false,NOW(),NOW()),(2,'H','8','AVAILABLE',8,8,false,NOW(),NOW()),(2,'H','9','AVAILABLE',8,9,false,NOW(),NOW()),(2,'H','10','AVAILABLE',8,10,true,NOW(),NOW()),
(2,'H','11','AVAILABLE',8,11,true,NOW(),NOW()),(2,'H','12','AVAILABLE',8,12,false,NOW(),NOW()),(2,'H','13','AVAILABLE',8,13,false,NOW(),NOW()),(2,'H','14','AVAILABLE',8,14,false,NOW(),NOW()),(2,'H','15','AVAILABLE',8,15,false,NOW(),NOW()),
(2,'H','16','AVAILABLE',8,16,false,NOW(),NOW()),(2,'H','17','AVAILABLE',8,17,false,NOW(),NOW()),(2,'H','18','AVAILABLE',8,18,false,NOW(),NOW()),(2,'H','19','AVAILABLE',8,19,false,NOW(),NOW()),(2,'H','20','AVAILABLE',8,20,true,NOW(),NOW()),
(2,'I','1','AVAILABLE',9,1,true,NOW(),NOW()),(2,'I','2','AVAILABLE',9,2,false,NOW(),NOW()),(2,'I','3','AVAILABLE',9,3,false,NOW(),NOW()),(2,'I','4','AVAILABLE',9,4,false,NOW(),NOW()),(2,'I','5','AVAILABLE',9,5,false,NOW(),NOW()),
(2,'I','6','AVAILABLE',9,6,false,NOW(),NOW()),(2,'I','7','AVAILABLE',9,7,false,NOW(),NOW()),(2,'I','8','AVAILABLE',9,8,false,NOW(),NOW()),(2,'I','9','AVAILABLE',9,9,false,NOW(),NOW()),(2,'I','10','AVAILABLE',9,10,true,NOW(),NOW()),
(2,'I','11','AVAILABLE',9,11,true,NOW(),NOW()),(2,'I','12','AVAILABLE',9,12,false,NOW(),NOW()),(2,'I','13','AVAILABLE',9,13,false,NOW(),NOW()),(2,'I','14','AVAILABLE',9,14,false,NOW(),NOW()),(2,'I','15','AVAILABLE',9,15,false,NOW(),NOW()),
(2,'I','16','AVAILABLE',9,16,false,NOW(),NOW()),(2,'I','17','AVAILABLE',9,17,false,NOW(),NOW()),(2,'I','18','AVAILABLE',9,18,false,NOW(),NOW()),(2,'I','19','AVAILABLE',9,19,false,NOW(),NOW()),(2,'I','20','AVAILABLE',9,20,true,NOW(),NOW()),
(2,'J','1','AVAILABLE',10,1,true,NOW(),NOW()),(2,'J','2','AVAILABLE',10,2,false,NOW(),NOW()),(2,'J','3','AVAILABLE',10,3,false,NOW(),NOW()),(2,'J','4','AVAILABLE',10,4,false,NOW(),NOW()),(2,'J','5','AVAILABLE',10,5,false,NOW(),NOW()),
(2,'J','6','AVAILABLE',10,6,false,NOW(),NOW()),(2,'J','7','AVAILABLE',10,7,false,NOW(),NOW()),(2,'J','8','AVAILABLE',10,8,false,NOW(),NOW()),(2,'J','9','AVAILABLE',10,9,false,NOW(),NOW()),(2,'J','10','AVAILABLE',10,10,true,NOW(),NOW()),
(2,'J','11','AVAILABLE',10,11,true,NOW(),NOW()),(2,'J','12','AVAILABLE',10,12,false,NOW(),NOW()),(2,'J','13','AVAILABLE',10,13,false,NOW(),NOW()),(2,'J','14','AVAILABLE',10,14,false,NOW(),NOW()),(2,'J','15','AVAILABLE',10,15,false,NOW(),NOW()),
(2,'J','16','AVAILABLE',10,16,false,NOW(),NOW()),(2,'J','17','AVAILABLE',10,17,false,NOW(),NOW()),(2,'J','18','AVAILABLE',10,18,false,NOW(),NOW()),(2,'J','19','AVAILABLE',10,19,false,NOW(),NOW()),(2,'J','20','AVAILABLE',10,20,true,NOW(),NOW());

-- Zone 3 (10x20 = 200 seats)
INSERT INTO seats (seat_zone_id, row_label, seat_number, status, row_num, col_num, is_aisle, created_at, updated_at) VALUES
(3,'A','1','AVAILABLE',1,1,true,NOW(),NOW()),(3,'A','2','AVAILABLE',1,2,false,NOW(),NOW()),(3,'A','3','AVAILABLE',1,3,false,NOW(),NOW()),(3,'A','4','AVAILABLE',1,4,false,NOW(),NOW()),(3,'A','5','AVAILABLE',1,5,false,NOW(),NOW()),
(3,'A','6','AVAILABLE',1,6,false,NOW(),NOW()),(3,'A','7','AVAILABLE',1,7,false,NOW(),NOW()),(3,'A','8','AVAILABLE',1,8,false,NOW(),NOW()),(3,'A','9','AVAILABLE',1,9,false,NOW(),NOW()),(3,'A','10','AVAILABLE',1,10,true,NOW(),NOW()),
(3,'A','11','AVAILABLE',1,11,true,NOW(),NOW()),(3,'A','12','AVAILABLE',1,12,false,NOW(),NOW()),(3,'A','13','AVAILABLE',1,13,false,NOW(),NOW()),(3,'A','14','AVAILABLE',1,14,false,NOW(),NOW()),(3,'A','15','AVAILABLE',1,15,false,NOW(),NOW()),
(3,'A','16','AVAILABLE',1,16,false,NOW(),NOW()),(3,'A','17','AVAILABLE',1,17,false,NOW(),NOW()),(3,'A','18','AVAILABLE',1,18,false,NOW(),NOW()),(3,'A','19','AVAILABLE',1,19,false,NOW(),NOW()),(3,'A','20','AVAILABLE',1,20,true,NOW(),NOW()),
(3,'B','1','AVAILABLE',2,1,true,NOW(),NOW()),(3,'B','2','AVAILABLE',2,2,false,NOW(),NOW()),(3,'B','3','AVAILABLE',2,3,false,NOW(),NOW()),(3,'B','4','AVAILABLE',2,4,false,NOW(),NOW()),(3,'B','5','AVAILABLE',2,5,false,NOW(),NOW()),
(3,'B','6','AVAILABLE',2,6,false,NOW(),NOW()),(3,'B','7','AVAILABLE',2,7,false,NOW(),NOW()),(3,'B','8','AVAILABLE',2,8,false,NOW(),NOW()),(3,'B','9','AVAILABLE',2,9,false,NOW(),NOW()),(3,'B','10','AVAILABLE',2,10,true,NOW(),NOW()),
(3,'B','11','AVAILABLE',2,11,true,NOW(),NOW()),(3,'B','12','AVAILABLE',2,12,false,NOW(),NOW()),(3,'B','13','AVAILABLE',2,13,false,NOW(),NOW()),(3,'B','14','AVAILABLE',2,14,false,NOW(),NOW()),(3,'B','15','AVAILABLE',2,15,false,NOW(),NOW()),
(3,'B','16','AVAILABLE',2,16,false,NOW(),NOW()),(3,'B','17','AVAILABLE',2,17,false,NOW(),NOW()),(3,'B','18','AVAILABLE',2,18,false,NOW(),NOW()),(3,'B','19','AVAILABLE',2,19,false,NOW(),NOW()),(3,'B','20','AVAILABLE',2,20,true,NOW(),NOW()),
(3,'C','1','AVAILABLE',3,1,true,NOW(),NOW()),(3,'C','2','AVAILABLE',3,2,false,NOW(),NOW()),(3,'C','3','AVAILABLE',3,3,false,NOW(),NOW()),(3,'C','4','AVAILABLE',3,4,false,NOW(),NOW()),(3,'C','5','AVAILABLE',3,5,false,NOW(),NOW()),
(3,'C','6','AVAILABLE',3,6,false,NOW(),NOW()),(3,'C','7','AVAILABLE',3,7,false,NOW(),NOW()),(3,'C','8','AVAILABLE',3,8,false,NOW(),NOW()),(3,'C','9','AVAILABLE',3,9,false,NOW(),NOW()),(3,'C','10','AVAILABLE',3,10,true,NOW(),NOW()),
(3,'C','11','AVAILABLE',3,11,true,NOW(),NOW()),(3,'C','12','AVAILABLE',3,12,false,NOW(),NOW()),(3,'C','13','AVAILABLE',3,13,false,NOW(),NOW()),(3,'C','14','AVAILABLE',3,14,false,NOW(),NOW()),(3,'C','15','AVAILABLE',3,15,false,NOW(),NOW()),
(3,'C','16','AVAILABLE',3,16,false,NOW(),NOW()),(3,'C','17','AVAILABLE',3,17,false,NOW(),NOW()),(3,'C','18','AVAILABLE',3,18,false,NOW(),NOW()),(3,'C','19','AVAILABLE',3,19,false,NOW(),NOW()),(3,'C','20','AVAILABLE',3,20,true,NOW(),NOW()),
(3,'D','1','AVAILABLE',4,1,true,NOW(),NOW()),(3,'D','2','AVAILABLE',4,2,false,NOW(),NOW()),(3,'D','3','AVAILABLE',4,3,false,NOW(),NOW()),(3,'D','4','AVAILABLE',4,4,false,NOW(),NOW()),(3,'D','5','AVAILABLE',4,5,false,NOW(),NOW()),
(3,'D','6','AVAILABLE',4,6,false,NOW(),NOW()),(3,'D','7','AVAILABLE',4,7,false,NOW(),NOW()),(3,'D','8','AVAILABLE',4,8,false,NOW(),NOW()),(3,'D','9','AVAILABLE',4,9,false,NOW(),NOW()),(3,'D','10','AVAILABLE',4,10,true,NOW(),NOW()),
(3,'D','11','AVAILABLE',4,11,true,NOW(),NOW()),(3,'D','12','AVAILABLE',4,12,false,NOW(),NOW()),(3,'D','13','AVAILABLE',4,13,false,NOW(),NOW()),(3,'D','14','AVAILABLE',4,14,false,NOW(),NOW()),(3,'D','15','AVAILABLE',4,15,false,NOW(),NOW()),
(3,'D','16','AVAILABLE',4,16,false,NOW(),NOW()),(3,'D','17','AVAILABLE',4,17,false,NOW(),NOW()),(3,'D','18','AVAILABLE',4,18,false,NOW(),NOW()),(3,'D','19','AVAILABLE',4,19,false,NOW(),NOW()),(3,'D','20','AVAILABLE',4,20,true,NOW(),NOW()),
(3,'E','1','AVAILABLE',5,1,true,NOW(),NOW()),(3,'E','2','AVAILABLE',5,2,false,NOW(),NOW()),(3,'E','3','AVAILABLE',5,3,false,NOW(),NOW()),(3,'E','4','AVAILABLE',5,4,false,NOW(),NOW()),(3,'E','5','AVAILABLE',5,5,false,NOW(),NOW()),
(3,'E','6','AVAILABLE',5,6,false,NOW(),NOW()),(3,'E','7','AVAILABLE',5,7,false,NOW(),NOW()),(3,'E','8','AVAILABLE',5,8,false,NOW(),NOW()),(3,'E','9','AVAILABLE',5,9,false,NOW(),NOW()),(3,'E','10','AVAILABLE',5,10,true,NOW(),NOW()),
(3,'E','11','AVAILABLE',5,11,true,NOW(),NOW()),(3,'E','12','AVAILABLE',5,12,false,NOW(),NOW()),(3,'E','13','AVAILABLE',5,13,false,NOW(),NOW()),(3,'E','14','AVAILABLE',5,14,false,NOW(),NOW()),(3,'E','15','AVAILABLE',5,15,false,NOW(),NOW()),
(3,'E','16','AVAILABLE',5,16,false,NOW(),NOW()),(3,'E','17','AVAILABLE',5,17,false,NOW(),NOW()),(3,'E','18','AVAILABLE',5,18,false,NOW(),NOW()),(3,'E','19','AVAILABLE',5,19,false,NOW(),NOW()),(3,'E','20','AVAILABLE',5,20,true,NOW(),NOW()),
(3,'F','1','AVAILABLE',6,1,true,NOW(),NOW()),(3,'F','2','AVAILABLE',6,2,false,NOW(),NOW()),(3,'F','3','AVAILABLE',6,3,false,NOW(),NOW()),(3,'F','4','AVAILABLE',6,4,false,NOW(),NOW()),(3,'F','5','AVAILABLE',6,5,false,NOW(),NOW()),
(3,'F','6','AVAILABLE',6,6,false,NOW(),NOW()),(3,'F','7','AVAILABLE',6,7,false,NOW(),NOW()),(3,'F','8','AVAILABLE',6,8,false,NOW(),NOW()),(3,'F','9','AVAILABLE',6,9,false,NOW(),NOW()),(3,'F','10','AVAILABLE',6,10,true,NOW(),NOW()),
(3,'F','11','AVAILABLE',6,11,true,NOW(),NOW()),(3,'F','12','AVAILABLE',6,12,false,NOW(),NOW()),(3,'F','13','AVAILABLE',6,13,false,NOW(),NOW()),(3,'F','14','AVAILABLE',6,14,false,NOW(),NOW()),(3,'F','15','AVAILABLE',6,15,false,NOW(),NOW()),
(3,'F','16','AVAILABLE',6,16,false,NOW(),NOW()),(3,'F','17','AVAILABLE',6,17,false,NOW(),NOW()),(3,'F','18','AVAILABLE',6,18,false,NOW(),NOW()),(3,'F','19','AVAILABLE',6,19,false,NOW(),NOW()),(3,'F','20','AVAILABLE',6,20,true,NOW(),NOW()),
(3,'G','1','AVAILABLE',7,1,true,NOW(),NOW()),(3,'G','2','AVAILABLE',7,2,false,NOW(),NOW()),(3,'G','3','AVAILABLE',7,3,false,NOW(),NOW()),(3,'G','4','AVAILABLE',7,4,false,NOW(),NOW()),(3,'G','5','AVAILABLE',7,5,false,NOW(),NOW()),
(3,'G','6','AVAILABLE',7,6,false,NOW(),NOW()),(3,'G','7','AVAILABLE',7,7,false,NOW(),NOW()),(3,'G','8','AVAILABLE',7,8,false,NOW(),NOW()),(3,'G','9','AVAILABLE',7,9,false,NOW(),NOW()),(3,'G','10','AVAILABLE',7,10,true,NOW(),NOW()),
(3,'G','11','AVAILABLE',7,11,true,NOW(),NOW()),(3,'G','12','AVAILABLE',7,12,false,NOW(),NOW()),(3,'G','13','AVAILABLE',7,13,false,NOW(),NOW()),(3,'G','14','AVAILABLE',7,14,false,NOW(),NOW()),(3,'G','15','AVAILABLE',7,15,false,NOW(),NOW()),
(3,'G','16','AVAILABLE',7,16,false,NOW(),NOW()),(3,'G','17','AVAILABLE',7,17,false,NOW(),NOW()),(3,'G','18','AVAILABLE',7,18,false,NOW(),NOW()),(3,'G','19','AVAILABLE',7,19,false,NOW(),NOW()),(3,'G','20','AVAILABLE',7,20,true,NOW(),NOW()),
(3,'H','1','AVAILABLE',8,1,true,NOW(),NOW()),(3,'H','2','AVAILABLE',8,2,false,NOW(),NOW()),(3,'H','3','AVAILABLE',8,3,false,NOW(),NOW()),(3,'H','4','AVAILABLE',8,4,false,NOW(),NOW()),(3,'H','5','AVAILABLE',8,5,false,NOW(),NOW()),
(3,'H','6','AVAILABLE',8,6,false,NOW(),NOW()),(3,'H','7','AVAILABLE',8,7,false,NOW(),NOW()),(3,'H','8','AVAILABLE',8,8,false,NOW(),NOW()),(3,'H','9','AVAILABLE',8,9,false,NOW(),NOW()),(3,'H','10','AVAILABLE',8,10,true,NOW(),NOW()),
(3,'H','11','AVAILABLE',8,11,true,NOW(),NOW()),(3,'H','12','AVAILABLE',8,12,false,NOW(),NOW()),(3,'H','13','AVAILABLE',8,13,false,NOW(),NOW()),(3,'H','14','AVAILABLE',8,14,false,NOW(),NOW()),(3,'H','15','AVAILABLE',8,15,false,NOW(),NOW()),
(3,'H','16','AVAILABLE',8,16,false,NOW(),NOW()),(3,'H','17','AVAILABLE',8,17,false,NOW(),NOW()),(3,'H','18','AVAILABLE',8,18,false,NOW(),NOW()),(3,'H','19','AVAILABLE',8,19,false,NOW(),NOW()),(3,'H','20','AVAILABLE',8,20,true,NOW(),NOW()),
(3,'I','1','AVAILABLE',9,1,true,NOW(),NOW()),(3,'I','2','AVAILABLE',9,2,false,NOW(),NOW()),(3,'I','3','AVAILABLE',9,3,false,NOW(),NOW()),(3,'I','4','AVAILABLE',9,4,false,NOW(),NOW()),(3,'I','5','AVAILABLE',9,5,false,NOW(),NOW()),
(3,'I','6','AVAILABLE',9,6,false,NOW(),NOW()),(3,'I','7','AVAILABLE',9,7,false,NOW(),NOW()),(3,'I','8','AVAILABLE',9,8,false,NOW(),NOW()),(3,'I','9','AVAILABLE',9,9,false,NOW(),NOW()),(3,'I','10','AVAILABLE',9,10,true,NOW(),NOW()),
(3,'I','11','AVAILABLE',9,11,true,NOW(),NOW()),(3,'I','12','AVAILABLE',9,12,false,NOW(),NOW()),(3,'I','13','AVAILABLE',9,13,false,NOW(),NOW()),(3,'I','14','AVAILABLE',9,14,false,NOW(),NOW()),(3,'I','15','AVAILABLE',9,15,false,NOW(),NOW()),
(3,'I','16','AVAILABLE',9,16,false,NOW(),NOW()),(3,'I','17','AVAILABLE',9,17,false,NOW(),NOW()),(3,'I','18','AVAILABLE',9,18,false,NOW(),NOW()),(3,'I','19','AVAILABLE',9,19,false,NOW(),NOW()),(3,'I','20','AVAILABLE',9,20,true,NOW(),NOW()),
(3,'J','1','AVAILABLE',10,1,true,NOW(),NOW()),(3,'J','2','AVAILABLE',10,2,false,NOW(),NOW()),(3,'J','3','AVAILABLE',10,3,false,NOW(),NOW()),(3,'J','4','AVAILABLE',10,4,false,NOW(),NOW()),(3,'J','5','AVAILABLE',10,5,false,NOW(),NOW()),
(3,'J','6','AVAILABLE',10,6,false,NOW(),NOW()),(3,'J','7','AVAILABLE',10,7,false,NOW(),NOW()),(3,'J','8','AVAILABLE',10,8,false,NOW(),NOW()),(3,'J','9','AVAILABLE',10,9,false,NOW(),NOW()),(3,'J','10','AVAILABLE',10,10,true,NOW(),NOW()),
(3,'J','11','AVAILABLE',10,11,true,NOW(),NOW()),(3,'J','12','AVAILABLE',10,12,false,NOW(),NOW()),(3,'J','13','AVAILABLE',10,13,false,NOW(),NOW()),(3,'J','14','AVAILABLE',10,14,false,NOW(),NOW()),(3,'J','15','AVAILABLE',10,15,false,NOW(),NOW()),
(3,'J','16','AVAILABLE',10,16,false,NOW(),NOW()),(3,'J','17','AVAILABLE',10,17,false,NOW(),NOW()),(3,'J','18','AVAILABLE',10,18,false,NOW(),NOW()),(3,'J','19','AVAILABLE',10,19,false,NOW(),NOW()),(3,'J','20','AVAILABLE',10,20,true,NOW(),NOW());
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
