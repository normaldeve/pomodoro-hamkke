package com.junwoo.hamkke.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 만료된 Refresh 토큰을 매일 새벽 3시에 일괄 제거
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenProvider refreshTokenProvider;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanup() {
        log.info("[RefreshTokenCleanupScheduler] 만료된 Refresh Token 청소 시작");
        refreshTokenProvider.cleanupExpiredTokens();
    }
}
