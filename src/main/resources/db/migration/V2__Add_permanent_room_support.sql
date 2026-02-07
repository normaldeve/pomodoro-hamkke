-- Study Room 테이블에 permanent 컬럼 추가
ALTER TABLE study_room
    ADD COLUMN permanent BOOLEAN NOT NULL DEFAULT FALSE AFTER status;

-- permanent 컬럼에 인덱스 추가 (상시 운영 방 조회 성능 향상)
CREATE INDEX idx_permanent ON study_room(permanent);

-- 복합 인덱스 추가 (일반 방 조회 최적화)
CREATE INDEX idx_status_permanent ON study_room(status, permanent);

-- 기존 데이터는 모두 일반 방이므로 permanent = FALSE (이미 DEFAULT로 설정됨)
-- 별도 UPDATE 불필요