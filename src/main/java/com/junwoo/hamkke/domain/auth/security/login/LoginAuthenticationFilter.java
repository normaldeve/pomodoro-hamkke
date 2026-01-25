package com.junwoo.hamkke.domain.auth.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.common.exception.ErrorResponse;
import com.junwoo.hamkke.domain.auth.dto.AuthDTO;
import com.junwoo.hamkke.domain.auth.dto.LoginRequest;
import com.junwoo.hamkke.domain.auth.dto.TokenResponse;
import com.junwoo.hamkke.domain.auth.exception.AuthException;
import com.junwoo.hamkke.domain.auth.jwt.JwtTokenProvider;
import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.auth.service.RefreshTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Slf4j
@RequiredArgsConstructor
public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    public LoginAuthenticationFilter(
            AuthenticationManager authenticationManager,
            ObjectMapper objectMapper,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenProvider refreshTokenProvider
    ) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            log.info("로그인을 시도합니다 - username: {}", loginRequest.username());

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Request parsing failed", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        AuthDTO user = userDetails.getUser();

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.nickname());

        refreshTokenProvider.save(user.nickname(), refreshToken, jwtTokenProvider.getRefreshTokenValidityInMilliseconds());

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/api/auth");
        refreshTokenCookie.setMaxAge((int) (jwtTokenProvider.getRefreshTokenValidityInMilliseconds() / 1000));

        response.addCookie(refreshTokenCookie);

        log.info("로그인에 성공했습니다 - nickname: {}, role: {}", user.nickname(), user.role());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(new TokenResponse(accessToken, user)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        log.error("로그인 실패: {}", failed.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        AuthException authException = new AuthException(ErrorCode.UNSUCCESSFUL_AUTHENTICATION);
        String errorMessage = objectMapper.writeValueAsString(new ErrorResponse(authException));

        response.getWriter().write(errorMessage);
    }
}