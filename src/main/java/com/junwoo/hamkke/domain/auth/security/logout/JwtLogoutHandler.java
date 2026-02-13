package com.junwoo.hamkke.domain.auth.security.logout;

import com.junwoo.hamkke.domain.auth.jwt.JwtTokenProvider;
import com.junwoo.hamkke.domain.auth.service.RefreshTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * 로그아웃 핸들러 구현
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 15.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final RefreshTokenProvider refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request);
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        refreshTokenService.delete(username);

        response.addHeader("Set-Cookie", jwtTokenProvider.createRefreshTokenClearCookieHeader(request));

        log.info("로그아웃 완료 - username: {}", username);
    }
}
