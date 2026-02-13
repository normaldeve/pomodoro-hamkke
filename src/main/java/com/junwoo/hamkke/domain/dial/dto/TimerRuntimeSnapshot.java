package com.junwoo.hamkke.domain.dial.dto;

/**
 * 집중 시간 정산을 위해 현재 타이머 상태를 조회할 때 사용하는 스냅샷.
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 13.
 */
public record TimerRuntimeSnapshot(
        TimerPhase phase,
        int currentSessionId,
        int elapsedSecondsInCurrentPhase,
        int focusDurationSeconds
) {
}
