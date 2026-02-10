package com.junwoo.hamkke.domain.dial.dto.event;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;

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
