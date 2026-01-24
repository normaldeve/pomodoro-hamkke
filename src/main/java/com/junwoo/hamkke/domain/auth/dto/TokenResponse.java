package com.junwoo.hamkke.domain.auth.dto;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public record TokenResponse(
        String accessToken,
        AuthDTO user
) {
}
