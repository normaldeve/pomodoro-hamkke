package com.junwoo.hamkke.domain.room_member.dto.event;

import java.util.UUID;

/**
 * 멤버가 방을 나갔을 때 발생하는 이벤트
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
public record MemberLeftRoomEvent(
        UUID roomId,
        Long userId,
        boolean wasHost,
        long remainingMembers
) {
}
