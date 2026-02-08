package com.junwoo.hamkke.domain.user.dto;

import com.junwoo.hamkke.domain.user.entity.UserEntity;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 18.
 */
@Builder
public record UserInfo(
        Long userId,
        String nickname,
        String profileUrl,
        LocalDateTime createdAt
) {

    public static UserInfo from(UserEntity user) {
        return new UserInfo(user.getId(), user.getNickname(), user.getProfileUrl(), user.getCreatedAt());
    }
}
