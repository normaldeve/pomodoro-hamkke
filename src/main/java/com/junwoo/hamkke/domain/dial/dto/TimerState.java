package com.junwoo.hamkke.domain.dial.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Data
@Builder
public class TimerState {
    private Long roomId;
    private TimerPhase phase;
    private int phaseDurationSeconds;
    private int remainingSeconds;
    private int defaultFocusMinutes;
    private int defaultBreakMinutes;
    private int totalSessions;
    private int currentSession;
    // 방장이 다음 집중 세션의 시간을 수정할 수 있고 이는 필수가 아닙니다.
    private Integer nextFocusMinutes;
    private boolean running;
    private long phaseStartTime;

    public static TimerState createFocus(Long roomId, TimerStartRequest request) {
        return TimerState.builder()
                .roomId(roomId)
                .phase(TimerPhase.FOCUS)
                .phaseDurationSeconds(request.focusMinutes() * 60)
                .remainingSeconds(request.focusMinutes() * 60)
                .defaultFocusMinutes(request.focusMinutes())
                .defaultBreakMinutes(request.breakMinutes())
                .totalSessions(request.totalSessions())
                .currentSession(1)
                .running(true)
                .phaseStartTime(System.currentTimeMillis())
                .build();
    }

    // 상시 운영 방용 정각 기준 타이머 생성
    public static TimerState createPermanent(
            Long roomId,
            int focusMinutes,
            int breakMinutes,
            TimerPhase phase,
            int remainingSeconds,
            long phaseStartTime
    ) {
        return TimerState.builder()
                .roomId(roomId)
                .phase(phase)
                .phaseDurationSeconds(phase == TimerPhase.FOCUS ? focusMinutes * 60 : breakMinutes * 60)
                .remainingSeconds(remainingSeconds)
                .defaultFocusMinutes(focusMinutes)
                .defaultBreakMinutes(breakMinutes)
                .totalSessions(Integer.MAX_VALUE)
                .currentSession(1)
                .running(true)
                .phaseStartTime(phaseStartTime)
                .build();
    }
}
