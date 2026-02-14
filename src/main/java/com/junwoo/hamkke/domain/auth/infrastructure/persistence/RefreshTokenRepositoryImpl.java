package com.junwoo.hamkke.domain.auth.infrastructure.persistence;

import com.junwoo.hamkke.domain.auth.domain.entity.RefreshTokenEntity;
import com.junwoo.hamkke.domain.auth.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 14.
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity) {
        return refreshTokenJpaRepository.save(refreshTokenEntity);
    }

    @Override
    public void delete(RefreshTokenEntity refreshTokenEntity) {
        refreshTokenJpaRepository.delete(refreshTokenEntity);
    }

    @Override
    public Optional<RefreshTokenEntity> findByUsername(String username) {
        return refreshTokenJpaRepository.findByUsername(username);
    }

    @Override
    public void deleteByUsername(String username) {
        refreshTokenJpaRepository.deleteByUsername(username);
    }

    @Override
    public int deleteExpiredTokens(LocalDateTime now) {
        return refreshTokenJpaRepository.deleteExpiredTokens(now);
    }

    @Override
    public long countExpiredTokens(LocalDateTime now) {
        return refreshTokenJpaRepository.countExpiredTokens(now);
    }
}
