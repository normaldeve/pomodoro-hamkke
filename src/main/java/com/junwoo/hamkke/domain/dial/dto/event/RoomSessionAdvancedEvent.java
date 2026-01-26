package com.junwoo.hamkke.domain.dial.dto.event;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
public record RoomSessionAdvancedEvent(
        Long roomId,
        int currentSession
) {}
