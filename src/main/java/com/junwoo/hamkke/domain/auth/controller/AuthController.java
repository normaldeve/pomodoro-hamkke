package com.junwoo.hamkke.domain.auth.controller;

import com.junwoo.hamkke.domain.auth.dto.UserInfoFromToken;
import com.junwoo.hamkke.domain.auth.jwt.JwtTokenProvider;
import com.junwoo.hamkke.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            HttpServletRequest request
    ) {

        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request);

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(newAccessToken);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoFromToken> getInfoFromToken(
            HttpServletRequest request
    ) {
        // Authorization 헤더에서 토큰 추출
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            log.error("[AuthController] getUserInfoFromToken() : Authorization 헤더가 없거나 형식이 잘못됨");
            return ResponseEntity.badRequest().build();
        }

        String accessToken = bearerToken.substring(7);

        UserInfoFromToken userInfo = authService.getUserInfoFrontToken(accessToken);

        return ResponseEntity.ok(userInfo);
    }
}
