package com.junwoo.hamkke.domain.dial.dto.event;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
public record TimerPhaseChangeEvent(
        Long roomId,
        TimerPhase phase
) {
}
