package com.junwoo.hamkke.domain.auth.service;

import com.junwoo.hamkke.domain.auth.entity.RefreshTokenEntity;
import com.junwoo.hamkke.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Refresh Token 관리 서비스 (DB 기반)
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Refresh Token 저장 또는 갱신
     */
    @Transactional
    public void save(String username, String refreshToken, long ttlMillis) {
        LocalDateTime expireAt = LocalDateTime.now().plusSeconds(ttlMillis / 1000);

        refreshTokenRepository.findByUsername(username)
                .ifPresentOrElse(
                        // 기존 토큰이 있으면 갱신
                        existingToken -> {
                            existingToken.updateToken(refreshToken, expireAt);
                            log.info("[RefreshTokenProvider] Refresh Token 갱신 - username={}", username);
                        },
                        // 없으면 새로 생성
                        () -> {
                            RefreshTokenEntity newToken = RefreshTokenEntity.builder()
                                    .username(username)
                                    .token(refreshToken)
                                    .expireAt(expireAt)
                                    .build();
                            refreshTokenRepository.save(newToken);
                            log.info("[RefreshTokenProvider] Refresh Token 저장 - username={}", username);
                        }
                );
    }

    /**
     * Refresh Token 조회
     */
    @Transactional(readOnly = true)
    public String get(String username) {
        return refreshTokenRepository.findByUsername(username)
                .filter(token -> !token.isExpired())
                .map(RefreshTokenEntity::getToken)
                .orElseGet(() -> {
                    log.info("[RefreshTokenProvider] Refresh Token 조회 실패 또는 만료 - username={}", username);
                    return null;
                });
    }

    /**
     * Refresh Token 삭제
     */
    @Transactional
    public void delete(String username) {
        refreshTokenRepository.findByUsername(username)
                .ifPresentOrElse(
                        token -> {
                            refreshTokenRepository.delete(token);
                            log.info("[RefreshTokenProvider] Refresh Token 삭제 - username={}", username);
                        },
                        () -> log.info("[RefreshTokenProvider] Refresh Token 삭제 요청 (존재하지 않음) - username={}", username)
                );
    }

    /**
     * 만료된 토큰 정리
     */
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();

        long expiredCount = refreshTokenRepository.countExpiredTokens(now);

        if (expiredCount == 0) {
            log.debug("[RefreshTokenCleanup] 만료된 토큰 없음");
            return;
        }

        int deletedCount = refreshTokenRepository.deleteExpiredTokens(now);

        log.info("[RefreshTokenCleanup] 완료 - deleted={}", deletedCount);
    }
}