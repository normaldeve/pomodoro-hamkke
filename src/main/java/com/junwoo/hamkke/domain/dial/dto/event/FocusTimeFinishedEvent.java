package com.junwoo.hamkke.domain.dial.dto.event;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
public record FocusTimeFinishedEvent(
        Long roomId,
        int focusTime,
        int currentSessionId
) {
}
