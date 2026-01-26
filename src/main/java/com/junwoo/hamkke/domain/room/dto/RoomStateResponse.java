package com.junwoo.hamkke.domain.room.dto;

import com.junwoo.hamkke.domain.room.entity.RoomStatus;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
public record RoomStateResponse(
        RoomStatus status,
        int focusMinutes
) {
}