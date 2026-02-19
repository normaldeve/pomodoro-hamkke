package com.junwoo.hamkke.domain.room_member.event;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
public record RoomEmptiedEvent(
        UUID roomId,
        Long lastMemberId
) {
}
