package com.junwoo.hamkke.domain.auth.dto;

import com.junwoo.hamkke.common.dto.AuthDTO;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public record AccessTokenDTO(
        String accessToken,
        AuthDTO user
) {
}