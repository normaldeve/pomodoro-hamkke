package com.junwoo.hamkke.domain.room.dto;

import com.junwoo.hamkke.domain.room.entity.RoomStatus;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 10.
 */
public record RoomInfoMessage(
        RoomStatus status,
        int currentSession,
        int totalSessions
) {
}
