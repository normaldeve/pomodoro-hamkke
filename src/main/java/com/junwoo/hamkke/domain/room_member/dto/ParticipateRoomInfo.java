package com.junwoo.hamkke.domain.room_member.dto;

import com.junwoo.hamkke.domain.room_member.entity.RoomMemberRole;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
@Builder
public record ParticipateRoomInfo(
        Long lastRoomId,
        RoomMemberRole role,
        LocalDateTime lastRoomJointAt
) {
}
