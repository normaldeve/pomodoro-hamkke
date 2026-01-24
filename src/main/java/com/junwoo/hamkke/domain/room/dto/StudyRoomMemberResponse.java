package com.junwoo.hamkke.domain.room.dto;

import com.junwoo.hamkke.domain.room.entity.RoomMemberRole;
import com.junwoo.hamkke.domain.room.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import lombok.Builder;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Builder
public record StudyRoomMemberResponse(
        Long userId,
        String nickname,
        String profileUrl,
        RoomMemberRole role
) {

    public static StudyRoomMemberResponse from(StudyRoomMemberEntity member, UserEntity user) {
        return StudyRoomMemberResponse.builder()
                .userId(member.getUserId())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .role(member.getRole())
                .build();
    }
}
