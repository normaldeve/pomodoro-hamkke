package com.junwoo.hamkke.common.dto;

import com.junwoo.hamkke.domain.user.entity.Role;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Builder
public record AuthDTO(
        Long id,
        String username,
        String nickname,
        String profileUrl,
        Role role,
        LocalDateTime createdAt
) {

    public static AuthDTO from(UserEntity user) {
        return AuthDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
