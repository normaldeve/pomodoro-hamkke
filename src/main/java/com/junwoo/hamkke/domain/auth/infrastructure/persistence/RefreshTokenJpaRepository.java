package com.junwoo.hamkke.domain.auth.infrastructure.persistence;

import com.junwoo.hamkke.domain.auth.domain.entity.RefreshTokenEntity;
import com.junwoo.hamkke.domain.auth.domain.repository.RefreshTokenRepository;
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
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByUsername(String username);

    void deleteByUsername(String username);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expireAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(r) FROM RefreshTokenEntity r WHERE r.expireAt < :now")
    long countExpiredTokens(@Param("now") LocalDateTime now);
}
