package com.junwoo.hamkke.domain.dial.dto;

import lombok.Builder;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 10.
 */
@Builder
public record TimerTickMessage(
        UUID roomId,
        TimerPhase phase,
        int remainingSeconds,
        boolean running
) {

    public static TimerTickMessage from(TimerState state, int remainingSeconds) {
        return TimerTickMessage.builder()
                .roomId(state.getRoomId())
                .phase(state.getPhase())
                .remainingSeconds(remainingSeconds)
                .running(state.isRunning())
                .build();
    }
}
