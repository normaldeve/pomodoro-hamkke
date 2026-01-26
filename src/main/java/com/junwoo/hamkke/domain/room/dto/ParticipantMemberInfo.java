package com.junwoo.hamkke.domain.room.dto;

import com.junwoo.hamkke.domain.user.entity.UserEntity;
import lombok.Builder;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Builder
public record ParticipantMemberInfo(
        Long userId,
        String nickname,
        String profileUrl
) {

    public static ParticipantMemberInfo from(UserEntity user) {
        return ParticipantMemberInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .build();
    }
}
