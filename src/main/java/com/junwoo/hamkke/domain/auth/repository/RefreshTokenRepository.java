package com.junwoo.hamkke.domain.auth.repository;

import com.junwoo.hamkke.domain.auth.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

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
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expireAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * 만료된 토큰 개수 조회
     */
    @Query("SELECT COUNT(r) FROM RefreshTokenEntity r WHERE r.expireAt < :now")
    long countExpiredTokens(@Param("now") LocalDateTime now);
}
