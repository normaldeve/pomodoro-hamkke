package com.junwoo.hamkke.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Refresh 토큰을 1분마다 만료되었는지 확인하는 스케줄러
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(fixedRate = 300_000) // 5분마다
    public void cleanup() {
        log.debug("[RefreshTokenCleanUpScheduler] 만료된 Refresh Token 청소합니다]");
        refreshTokenService.cleanupExpiredTokens();
    }
}
