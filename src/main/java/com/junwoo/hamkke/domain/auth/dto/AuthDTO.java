package com.junwoo.hamkke.domain.auth.dto;

import com.junwoo.hamkke.domain.user.entity.Role;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import lombok.Builder;

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
        Role role
) {

    public static AuthDTO from(UserEntity user) {
        return AuthDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .role(user.getRole())
                .build();
    }
}
