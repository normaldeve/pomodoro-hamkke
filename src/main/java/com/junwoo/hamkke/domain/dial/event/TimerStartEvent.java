package com.junwoo.hamkke.domain.dial.event;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 10.
 */
public record TimerStartEvent(
        UUID roomId,
        int focusTime
) {
}
