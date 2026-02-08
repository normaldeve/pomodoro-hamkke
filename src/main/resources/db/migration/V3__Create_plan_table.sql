-- Plan 테이블
CREATE TABLE plan (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      title VARCHAR(100) NOT NULL,
                      plan_date DATE NOT NULL,
                      start_time TIME NOT NULL,
                      end_time TIME NOT NULL,
                      color VARCHAR(20) NOT NULL,
                      completed BOOLEAN NOT NULL DEFAULT FALSE,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME,
                      INDEX idx_plan_user_date (user_id, plan_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;