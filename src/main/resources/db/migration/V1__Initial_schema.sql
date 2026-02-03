-- Users 테이블
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       nickname VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       profile_url VARCHAR(500),
                       role VARCHAR(20) NOT NULL,
                       created_at DATETIME NOT NULL,
                       INDEX idx_username (username),
                       INDEX idx_nickname (nickname)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Study Room 테이블
CREATE TABLE study_room (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            hashtags JSON,
                            focus_minutes INT NOT NULL DEFAULT 0,
                            break_minutes INT NOT NULL,
                            current_session INT NOT NULL DEFAULT 1,
                            total_sessions INT NOT NULL,
                            current_participants INT NOT NULL DEFAULT 0,
                            max_participants INT NOT NULL,
                            secret BOOLEAN NOT NULL DEFAULT FALSE,
                            password VARCHAR(255),
                            timer_type VARCHAR(20) NOT NULL,
                            host_id BIGINT NOT NULL,
                            status VARCHAR(20) NOT NULL,
                            created_at DATETIME NOT NULL,
                            updated_at DATETIME,
                            INDEX idx_status (status),
                            INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Study Room Member 테이블
CREATE TABLE study_room_member (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   study_room_id BIGINT NOT NULL,
                                   user_id BIGINT NOT NULL,
                                   current_session_id INT NOT NULL DEFAULT 0,
                                   role VARCHAR(20) NOT NULL,
                                   created_at DATETIME NOT NULL,
                                   UNIQUE KEY uk_room_user (study_room_id, user_id),
                                   INDEX idx_study_room_id (study_room_id),
                                   INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Study Goal 테이블
CREATE TABLE study_goal (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            study_room_id BIGINT NOT NULL,
                            user_id BIGINT NOT NULL,
                            content VARCHAR(500) NOT NULL,
                            completed BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at DATETIME NOT NULL,
                            updated_at DATETIME,
                            INDEX idx_room_user (study_room_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Messages 테이블
CREATE TABLE messages (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          room_id BIGINT NOT NULL,
                          sender_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          created_at DATETIME NOT NULL,
                          INDEX idx_room_id (room_id),
                          INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Session Reflection 테이블
CREATE TABLE session_reflection (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    study_room_id BIGINT NOT NULL,
                                    user_id BIGINT NOT NULL,
                                    session_id BIGINT NOT NULL,
                                    image_url VARCHAR(500),
                                    content VARCHAR(1000),
                                    focus_score INT,
                                    created_at DATETIME NOT NULL,
                                    UNIQUE KEY uk_room_user_session (study_room_id, user_id, session_id),
                                    INDEX idx_study_room_id (study_room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Room Focus Time 테이블
CREATE TABLE room_focus_time (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 study_room_id BIGINT NOT NULL,
                                 focus_date DATE NOT NULL,
                                 total_focus_minutes INT NOT NULL DEFAULT 0,
                                 created_at DATETIME NOT NULL,
                                 UNIQUE KEY uk_user_room_date (user_id, study_room_id, focus_date),
                                 INDEX idx_user_date (user_id, focus_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User Daily Study Stat 테이블
CREATE TABLE user_daily_study_stat (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id BIGINT NOT NULL,
                                       study_date DATE NOT NULL,
                                       total_minutes INT NOT NULL DEFAULT 0,
                                       level INT NOT NULL DEFAULT 0,
                                       created_at DATETIME NOT NULL,
                                       UNIQUE KEY uk_user_date (user_id, study_date),
                                       INDEX idx_user_date (user_id, study_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Point Log 테이블
CREATE TABLE point_log (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           type VARCHAR(20) NOT NULL,
                           amount INT NOT NULL,
                           ref_id BIGINT NOT NULL,
                           created_at DATETIME NOT NULL,
                           UNIQUE KEY uk_point_type_ref (type, ref_id),
                           INDEX idx_point_user (user_id),
                           INDEX idx_point_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;