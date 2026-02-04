package com.junwoo.hamkke.domain.auth.dto;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public final class SecurityPassUrlList {

    private SecurityPassUrlList() {}

    public static final String[] ALL = {
            "/api/auth/login",
            "/api/users/signup",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/ws/**",
            "/api/notifications/**",
            "/api/study/all",
            "/actuator/health",
            "/api/auth/me"
    };
}
