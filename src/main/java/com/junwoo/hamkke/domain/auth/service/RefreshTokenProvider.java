package com.junwoo.hamkke.domain.auth.service;

import com.junwoo.hamkke.domain.auth.dto.RefreshTokenEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenProvider {

    private static final String PREFIX = "refresh:";

    private final Map<String, RefreshTokenEntry> store = new ConcurrentHashMap<>();

    public void save(String username, String refreshToken, long ttlMillis) {
        long expireAt = System.currentTimeMillis() + ttlMillis;

        store.put(PREFIX + username, new RefreshTokenEntry(refreshToken, expireAt));

        log.info("[RefreshTokenService] Refresh Token 저장 - username={}", username);
    }

    public String get(String username) {
        String key = PREFIX + username;
        RefreshTokenEntry entry = store.get(key);

        if (entry == null) {
            log.info("[RefreshTokenService] Refresh Token 조회 실패 - username={}", username);
            return null;
        }

        if (System.currentTimeMillis() > entry.getExpireAt()) {
            store.remove(key);
            log.info("[RefreshTokenService] Refresh Token 만료 - username={}", username);
            return null;
        }

        log.info("[RefreshTokenService] Refresh Token 조회 성공 - username={}", username);
        return entry.getToken();
    }

    public void delete(String username) {
        RefreshTokenEntry removed = store.remove(PREFIX + username);

        if (removed != null) {
            log.info("[RefreshTokenService] Refresh Token 삭제 - username={}", username);
        } else {
            log.info("[RefreshTokenService] Refresh Token 삭제 요청 (존재하지 않음) - username={}", username);
        }
    }

    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();

        int totalBefore = store.size();

        // 삭제 대상 수집
        Map<String, RefreshTokenEntry> expiredTokens = new ConcurrentHashMap<>();

        store.forEach((key, entry) -> {
            if (now > entry.getExpireAt()) {
                expiredTokens.put(key, entry);
            }
        });

        if (expiredTokens.isEmpty()) {
            log.debug("[RefreshTokenCleanup] 만료된 토큰 없음 (total={})", totalBefore);
            return;
        }

        // 삭제 대상 로그 (너무 많으면 일부만)
        expiredTokens.entrySet().stream()
                .limit(5)
                .forEach(e -> {
                    String username = e.getKey().replace(PREFIX, "");
                    RefreshTokenEntry entry = e.getValue();

                    log.debug("[RefreshTokenCleanup] 삭제 대상 - username={}, expireAt={}", username, entry.getExpireAt());
                });

        // 실제 삭제
        expiredTokens.keySet().forEach(store::remove);

        int deletedCount = expiredTokens.size();
        int totalAfter = store.size();

        log.info(
                "[RefreshTokenCleanup] 완료 - before={}, deleted={}, after={}",
                totalBefore,
                deletedCount,
                totalAfter
        );
    }
}