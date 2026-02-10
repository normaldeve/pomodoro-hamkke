package com.junwoo.hamkke.domain.dial.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Data
@Builder
public class TimerState {

    private UUID roomId;
    private TimerPhase phase;
    private int phaseDurationSeconds;
    private int remainingSeconds;
    private int defaultFocusMinutes;
    private int defaultBreakMinutes;
    private int totalSessions;
    private int currentSession;
    private Integer nextFocusMinutes;
    private boolean running;
    private long phaseStartTime;

    public static TimerState createFocus(UUID roomId, TimerStartRequest req) {
        return TimerState.builder()
                .roomId(roomId)
                .phase(TimerPhase.FOCUS)
                .phaseDurationSeconds(req.focusMinutes() * 60)
                .remainingSeconds(req.focusMinutes() * 60)
                .defaultFocusMinutes(req.focusMinutes())
                .defaultBreakMinutes(req.breakMinutes())
                .totalSessions(req.totalSessions())
                .currentSession(1)
                .running(true)
                .phaseStartTime(System.currentTimeMillis())
                .build();
    }

    public static TimerState createPermanent(
            UUID roomId,
            int focusMinutes,
            int breakMinutes,
            TimerPhase phase,
            int remainingSeconds,
            long phaseStartTime
    ) {
        return TimerState.builder()
                .roomId(roomId)
                .phase(phase)
                .phaseDurationSeconds(
                        phase == TimerPhase.FOCUS
                                ? focusMinutes * 60
                                : breakMinutes * 60
                )
                .remainingSeconds(remainingSeconds)
                .defaultFocusMinutes(focusMinutes)
                .defaultBreakMinutes(breakMinutes)
                .totalSessions(Integer.MAX_VALUE)
                .currentSession(1)
                .running(true)
                .phaseStartTime(phaseStartTime)
                .build();
    }

    public void switchToBreak() {
        phase = TimerPhase.BREAK;
        phaseDurationSeconds = defaultBreakMinutes * 60;
        remainingSeconds = phaseDurationSeconds;
        phaseStartTime = System.currentTimeMillis();
    }

    public void switchToFocus() {
        phase = TimerPhase.FOCUS;
        phaseDurationSeconds = defaultFocusMinutes * 60;
        remainingSeconds = phaseDurationSeconds;
        phaseStartTime = System.currentTimeMillis();
    }

    public void switchPhase(TimerPhase next) {
        phase = next;
        phaseDurationSeconds =
                next == TimerPhase.FOCUS
                        ? defaultFocusMinutes * 60
                        : defaultBreakMinutes * 60;
        remainingSeconds = phaseDurationSeconds;
        phaseStartTime = System.currentTimeMillis();
    }

    public void advanceSession() {
        currentSession++;
    }

    public void pause() {
        running = false;
    }

    public void resume() {
        running = true;
        phaseStartTime = System.currentTimeMillis();
    }

    public void finish() {
        running = false;
    }

    public void switchToFocus(int focusMinutes) {
        phase = TimerPhase.FOCUS;
        phaseDurationSeconds = focusMinutes * 60;
        remainingSeconds = phaseDurationSeconds;
        phaseStartTime = System.currentTimeMillis();
    }
}
