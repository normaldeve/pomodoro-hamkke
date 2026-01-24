package com.junwoo.hamkke.domain.auth.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.auth.dto.AuthDTO;
import com.junwoo.hamkke.domain.auth.exception.AuthException;
import com.junwoo.hamkke.domain.auth.jwt.JwtTokenProvider;
import com.junwoo.hamkke.domain.user.service.UserService;
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

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Transactional
    public String refreshAccessToken(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String nickname = jwtTokenProvider.getNicknameFromToken(refreshToken);

        String savedToken = refreshTokenService.get(nickname);
        if (!refreshToken.equals(savedToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        AuthDTO user = userService.findByNickname(nickname);
        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        log.info("[AuthService] Access Token 재발급 성공 - nickname={}", nickname);

        return newAccessToken;
    }
}
