package com.junwoo.hamkke.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Refresh 토큰을 1분마다 만료되었는지 확인하는 스케줄러
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(fixedRate = 60_000) // 1분마다
    public void cleanup() {
        refreshTokenService.cleanupExpiredTokens();
    }
}
