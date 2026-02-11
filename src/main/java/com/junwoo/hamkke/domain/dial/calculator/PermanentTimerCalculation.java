package com.junwoo.hamkke.domain.dial.calculator;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 11.
 */
public record PermanentTimerCalculation(
        TimerPhase phase,
        int remainingSeconds,
        long phaseStartTime
) {}
