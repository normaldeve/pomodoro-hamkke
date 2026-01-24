package com.junwoo.hamkke.domain.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junwoo.hamkke.common.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String TOKEN_ERROR = "TOKEN_ERROR";
    private static final String EXPIRED = "EXPIRED";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        ErrorResponse error;

        Object tokenError = request.getAttribute(TOKEN_ERROR);

        if (EXPIRED.equals(tokenError)) {
            error = new ErrorResponse(
                    403,
                    LocalDateTime.now().toString(),
                    "Access Token이 만료되었습니다.",
                    null
            );
        } else {
            error = new ErrorResponse(
                    403,
                    LocalDateTime.now().toString(),
                    "로그인이 필요합니다.",
                    null
            );
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(error));
    }
}