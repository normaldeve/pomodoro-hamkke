package com.junwoo.hamkke.domain.user.dto;

import com.junwoo.hamkke.domain.user.entity.UserEntity;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 14.
 */
public record UserSearchResponse(
        Long userId,
        String nickname,
        String profileUrl
) {

    public static UserSearchResponse from(UserEntity user) {
        return new UserSearchResponse(user.getId(), user.getNickname(), user.getProfileUrl());
    }

}
