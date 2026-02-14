package com.junwoo.hamkke.common.dto;

import com.junwoo.hamkke.domain.user.entity.UserEntity;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 18.
 */
@Builder
public record UserDTO(
        Long userId,
        String nickname,
        String profileUrl,
        LocalDateTime createdAt
) {

    public static UserDTO from(UserEntity user) {
        return new UserDTO(user.getId(), user.getNickname(), user.getProfileUrl(), user.getCreatedAt());
    }
}
