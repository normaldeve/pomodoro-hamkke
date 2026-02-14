package com.junwoo.hamkke.domain.dial.event;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
public record FocusTimeStartedEvent(
        UUID roomId,
        int currentSessionId
) {
}
