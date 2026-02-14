package com.junwoo.hamkke.domain.auth.domain.repository;

import com.junwoo.hamkke.domain.auth.domain.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 14.
 */
public interface RefreshTokenRepository {

    RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity);

    void delete(RefreshTokenEntity refreshTokenEntity);

    /**
     * username으로 토큰 조회
     */
    Optional<RefreshTokenEntity> findByUsername(String username);

    /**
     * username으로 토큰 삭제
     */
    void deleteByUsername(String username);

    /**
     * 만료된 토큰 일괄 삭제
     */
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * 만료된 토큰 개수 조회
     */
    long countExpiredTokens(@Param("now") LocalDateTime now);
}
