/* =========================================================
   STEP 0. 사전 전제
   - MySQL 8.x
   - 기존 study_room.id : BIGINT
   - UUID 저장 방식 : BINARY(16)
   ========================================================= */


/* =========================================================
   STEP 1. study_room에 UUID 컬럼 추가 및 값 생성
   ========================================================= */

ALTER TABLE study_room
    ADD COLUMN uuid BINARY(16) NOT NULL;

UPDATE study_room
SET uuid = UUID_TO_BIN(UUID());


/* =========================================================
   STEP 2. room을 참조하는 모든 테이블에 UUID 컬럼 추가
   ========================================================= */

ALTER TABLE study_room_member
    ADD COLUMN study_room_uuid BINARY(16);

ALTER TABLE study_goal
    ADD COLUMN study_room_uuid BINARY(16);

ALTER TABLE messages
    ADD COLUMN room_uuid BINARY(16);

ALTER TABLE session_reflection
    ADD COLUMN study_room_uuid BINARY(16);

ALTER TABLE room_focus_time
    ADD COLUMN study_room_uuid BINARY(16);


/* =========================================================
   STEP 3. BIGINT → UUID 매핑 데이터 이관
   ========================================================= */

UPDATE study_room_member m
    JOIN study_room r ON m.study_room_id = r.id
    SET m.study_room_uuid = r.uuid;

UPDATE study_goal g
    JOIN study_room r ON g.study_room_id = r.id
    SET g.study_room_uuid = r.uuid;

UPDATE messages msg
    JOIN study_room r ON msg.room_id = r.id
    SET msg.room_uuid = r.uuid;

UPDATE session_reflection sr
    JOIN study_room r ON sr.study_room_id = r.id
    SET sr.study_room_uuid = r.uuid;

UPDATE room_focus_time rft
    JOIN study_room r ON rft.study_room_id = r.id
    SET rft.study_room_uuid = r.uuid;


/* =========================================================
   STEP 4. UUID 컬럼 NOT NULL 제약 적용
   ========================================================= */

ALTER TABLE study_room_member
    MODIFY study_room_uuid BINARY(16) NOT NULL;

ALTER TABLE study_goal
    MODIFY study_room_uuid BINARY(16) NOT NULL;

ALTER TABLE messages
    MODIFY room_uuid BINARY(16) NOT NULL;

ALTER TABLE session_reflection
    MODIFY study_room_uuid BINARY(16) NOT NULL;

ALTER TABLE room_focus_time
    MODIFY study_room_uuid BINARY(16) NOT NULL;


/* =========================================================
   STEP 5. 기존 BIGINT 컬럼 제거
   ========================================================= */

ALTER TABLE study_room_member
DROP COLUMN study_room_id;

ALTER TABLE study_goal
DROP COLUMN study_room_id;

ALTER TABLE messages
DROP COLUMN room_id;

ALTER TABLE session_reflection
DROP COLUMN study_room_id;

ALTER TABLE room_focus_time
DROP COLUMN study_room_id;


/* =========================================================
   STEP 6. study_room PK 교체 (BIGINT → UUID)
   ========================================================= */

ALTER TABLE study_room
DROP PRIMARY KEY;

ALTER TABLE study_room
DROP COLUMN id;

ALTER TABLE study_room
    CHANGE COLUMN uuid id BINARY(16) NOT NULL;

ALTER TABLE study_room
    ADD PRIMARY KEY (id);


/* =========================================================
   STEP 7. 연관 테이블 UUID 컬럼 이름 정리
   ========================================================= */

ALTER TABLE study_room_member
    CHANGE COLUMN study_room_uuid study_room_id BINARY(16) NOT NULL;

ALTER TABLE study_goal
    CHANGE COLUMN study_room_uuid study_room_id BINARY(16) NOT NULL;

ALTER TABLE messages
    CHANGE COLUMN room_uuid room_id BINARY(16) NOT NULL;

ALTER TABLE session_reflection
    CHANGE COLUMN study_room_uuid study_room_id BINARY(16) NOT NULL;

ALTER TABLE room_focus_time
    CHANGE COLUMN study_room_uuid study_room_id BINARY(16) NOT NULL;


/* =========================================================
   STEP 8. 인덱스 재생성
   ========================================================= */

CREATE INDEX idx_study_room_member_room
    ON study_room_member (study_room_id);

CREATE INDEX idx_study_goal_room_user
    ON study_goal (study_room_id, user_id);

CREATE INDEX idx_messages_room
    ON messages (room_id);

CREATE INDEX idx_session_reflection_room
    ON session_reflection (study_room_id);

CREATE INDEX idx_room_focus_time_user_room_date
    ON room_focus_time (user_id, study_room_id, focus_date);