-- Refresh Token 테이블 생성
CREATE TABLE refresh_tokens (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                username VARCHAR(100) NOT NULL UNIQUE,
                                token VARCHAR(500) NOT NULL,
                                expire_at DATETIME NOT NULL,
                                created_at DATETIME NOT NULL,
                                INDEX idx_username (username),
                                INDEX idx_expire_at (expire_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;