-- =====================================================================
-- 성능 측정용 데이터 생성 (studyhub_perf 스키마)
--
-- 실행 전 준비
--   1. root 계정으로 스키마 생성과 권한 부여
--        CREATE DATABASE IF NOT EXISTS studyhub_perf DEFAULT CHARACTER SET utf8mb4;
--        GRANT ALL PRIVILEGES ON studyhub_perf.* TO 'studyhub'@'%';
--   2. perf 프로필로 애플리케이션을 실행해 테이블 생성 (ddl-auto: update)
--   3. 회원가입 API로 측정용 계정 생성
--        - perf-owner@test.com  (OWNER)  : 카페의 사장님
--        - perf-member@test.com (MEMBER) : 예약, AI 검색 측정용
--
-- 생성 결과
--   - 카페 10,000개 (10개 지역 × 1,000개)
--   - 좌석 300,000개 (카페당 30개, A1 ~ A30)
--   - 이미지 30,000개 (카페당 3개, sort_order 0 ~ 2)
--
-- 같은 접속(세션)에서 위에서부터 순서대로 실행해야 함 (@owner_id 변수 사용)
-- =====================================================================

USE studyhub_perf;

-- 사장님 계정 id
SET @owner_id = (SELECT id FROM member WHERE email = 'perf-owner@test.com');
SELECT @owner_id;

-- 재귀 CTE 깊이 제한 (기본 1000 → 1만 개 생성을 위해 늘림)
SET SESSION cte_max_recursion_depth = 20000;


-- ---------------------------------------------------------------------
-- 카페 10,000개
--   - 이름: 5개 중 1개에 '조용한' 포함 (키워드 검색 측정용, 약 2,000개)
--   - 지역: 10개 지역에 번갈아 배정 (지역당 1,000개)
--   - 운영 시간: 절반은 24시간, 절반은 09:00 ~ 18:00 (운영 중 필터 측정용)
-- ---------------------------------------------------------------------
INSERT INTO cafe (name, address, region, region_code, latitude, longitude,
                  open_time, close_time, owner_id, status, created_date, last_modified_date)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 10000
)
SELECT
    CONCAT(IF(n % 5 = 0, '조용한 스터디카페 ', '스터디카페 '), n),
    CONCAT(ELT(n % 10 + 1, '서울 강남구', '서울 금천구', '서울 관악구', '서울 구로구', '서울 마포구',
               '서울 송파구', '서울 영등포구', '서울 종로구', '서울 서초구', '서울 동작구'),
           ' 테스트로 ', n),
    ELT(n % 10 + 1, '서울 강남구', '서울 금천구', '서울 관악구', '서울 구로구', '서울 마포구',
        '서울 송파구', '서울 영등포구', '서울 종로구', '서울 서초구', '서울 동작구'),
    ELT(n % 10 + 1, '11680', '11545', '11620', '11530', '11440',
        '11710', '11560', '11110', '11650', '11590'),
    37.45 + (n % 100) * 0.002,
    126.85 + (n % 150) * 0.002,
    IF(n % 2 = 0, '00:00:00', '09:00:00'),
    IF(n % 2 = 0, '23:59:59', '18:00:00'),
    @owner_id,
    'ACTIVE',
    NOW(),
    NOW()
FROM seq;


-- ---------------------------------------------------------------------
-- 좌석 300,000개 (카페당 30개, A1 ~ A30)
--   - 좌석 번호 형식은 서비스가 만드는 형식과 같게 ('A' + 번호)
-- ---------------------------------------------------------------------
INSERT INTO seat (cafe_id, seat_number, status, created_date, last_modified_date)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 30
)
SELECT
    c.id,
    CONCAT('A', s.n),
    'AVAILABLE',
    NOW(),
    NOW()
FROM cafe c
         CROSS JOIN seq s;


-- ---------------------------------------------------------------------
-- 이미지 30,000개 (카페당 3개, sort_order 0 ~ 2)
-- ---------------------------------------------------------------------
INSERT INTO cafe_image (cafe_id, image_url, sort_order, created_date, last_modified_date)
WITH RECURSIVE seq AS (
    SELECT 0 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 2
)
SELECT
    c.id,
    CONCAT('https://example.com/cafe/', c.id, '/', s.n, '.jpg'),
    s.n,
    NOW(),
    NOW()
FROM cafe c
         CROSS JOIN seq s;


-- ---------------------------------------------------------------------
-- 확인
-- ---------------------------------------------------------------------
SELECT COUNT(*) FROM cafe;                              -- 10000
SELECT COUNT(*) FROM seat;                              -- 300000
SELECT COUNT(*) FROM cafe_image;                        -- 30000
SELECT region, COUNT(*) FROM cafe GROUP BY region;      -- 10개 지역, 각 1000
SELECT COUNT(*) FROM cafe WHERE name LIKE '%조용한%';    -- 2000


-- ---------------------------------------------------------------------
-- 측정 후 정리 (필요할 때만 root 계정으로 실행)
-- ---------------------------------------------------------------------
-- DROP DATABASE studyhub_perf;