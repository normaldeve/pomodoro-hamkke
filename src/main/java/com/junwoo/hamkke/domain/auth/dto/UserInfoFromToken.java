package com.junwoo.hamkke.domain.auth.dto;

import com.junwoo.hamkke.domain.user.entity.Role;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
@Builder
public record UserInfoFromToken(
        Long id,
        String username,
        String nickname,
        String profileUrl,
        Role role,
        LocalDateTime createdAt
) {

    public static UserInfoFromToken of(UserEntity entity) {
        return UserInfoFromToken.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .nickname(entity.getNickname())
                .profileUrl(entity.getProfileUrl())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
