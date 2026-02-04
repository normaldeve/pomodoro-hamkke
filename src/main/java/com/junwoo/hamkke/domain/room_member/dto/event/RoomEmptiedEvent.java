package com.junwoo.hamkke.domain.room_member.dto.event;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
public record RoomEmptiedEvent(
        Long roomId,
        Long lastMemberId
) {
}
