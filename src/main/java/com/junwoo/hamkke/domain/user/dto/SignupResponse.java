package com.junwoo.hamkke.domain.user.dto;

import com.junwoo.hamkke.domain.user.entity.Role;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public record SignupResponse(
        Long id,
        String nickname,
        String email,
        String profileUrl,
        Role role,
        LocalDateTime createdAt
) {
}
