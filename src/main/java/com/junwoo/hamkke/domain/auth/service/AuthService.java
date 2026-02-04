package com.junwoo.hamkke.domain.auth.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.auth.dto.AuthDTO;
import com.junwoo.hamkke.domain.auth.dto.UserInfoFromToken;
import com.junwoo.hamkke.domain.auth.exception.AuthException;
import com.junwoo.hamkke.domain.auth.jwt.JwtTokenProvider;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import com.junwoo.hamkke.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenProvider refreshTokenProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public String refreshAccessToken(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        String savedToken = refreshTokenProvider.get(username);
        if (!refreshToken.equals(savedToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        AuthDTO user = userService.findByNickname(username);
        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        log.info("[AuthService] Access Token 재발급 성공 - username={}", username);

        return newAccessToken;
    }

    @Transactional(readOnly = true)
    public UserInfoFromToken getUserInfoFrontToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new AuthException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        Claims claims = jwtTokenProvider.getClaims(accessToken);

        Long userId = claims.get("id", Long.class);
        String nickname = claims.getSubject();
        String role = claims.get("role", String.class);

        log.info("[AuthService] getUserInfoFromToken() : 토큰에서 사용자 정보 추출 - userId: {}, nickname: {}",
                userId, nickname);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.CANNOT_FOUND_USER));

        return UserInfoFromToken.of(user);
    }
}