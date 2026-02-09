package com.junwoo.hamkke.domain.dial.dto.event;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
public record FocusTimeChangedEvent(
        UUID roomId,
        int focusTime
) {
}
