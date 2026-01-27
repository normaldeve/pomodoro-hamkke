package com.junwoo.hamkke.domain.dial.dto.event;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
public record FocusTimeStartedEvent(
        Long roomId,
        int currentSessionId
) {
}
