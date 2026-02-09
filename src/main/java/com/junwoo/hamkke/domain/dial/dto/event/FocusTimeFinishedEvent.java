package com.junwoo.hamkke.domain.dial.dto.event;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
public record FocusTimeFinishedEvent(
        UUID roomId,
        int focusTime,
        int currentSessionId
) {
}
