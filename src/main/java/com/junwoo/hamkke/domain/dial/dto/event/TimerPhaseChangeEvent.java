package com.junwoo.hamkke.domain.dial.dto.event;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
public record TimerPhaseChangeEvent(
        UUID roomId,
        TimerPhase phase
) {
}
