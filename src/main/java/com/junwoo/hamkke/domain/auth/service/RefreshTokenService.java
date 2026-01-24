package com.junwoo.hamkke.domain.auth.service;

import com.junwoo.hamkke.domain.auth.dto.RefreshTokenEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String PREFIX = "refresh:";

    private final Map<String, RefreshTokenEntry> store = new ConcurrentHashMap<>();

    public void save(String email, String refreshToken, long ttlMillis) {
        long expireAt = System.currentTimeMillis() + ttlMillis;

        store.put(PREFIX + email, new RefreshTokenEntry(refreshToken, expireAt));

        log.info("[RefreshTokenService] Refresh Token 저장 - email={}", email);
    }

    public String get(String email) {
        String key = PREFIX + email;
        RefreshTokenEntry entry = store.get(key);

        if (entry == null) {
            log.info("[RefreshTokenService] Refresh Token 조회 실패 - email={}", email);
            return null;
        }

        if (System.currentTimeMillis() > entry.getExpireAt()) {
            store.remove(key);
            log.info("[RefreshTokenService] Refresh Token 만료 - email={}", email);
            return null;
        }

        log.info("[RefreshTokenService] Refresh Token 조회 성공 - email={}", email);
        return entry.getToken();
    }

    public void delete(String email) {
        RefreshTokenEntry removed = store.remove(PREFIX + email);

        if (removed != null) {
            log.info("[RefreshTokenService] Refresh Token 삭제 - email={}", email);
        } else {
            log.info("[RefreshTokenService] Refresh Token 삭제 요청 (존재하지 않음) - email={}", email);
        }
    }

    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();

        store.entrySet().removeIf(entry ->
                now > entry.getValue().getExpireAt()
        );
    }
}