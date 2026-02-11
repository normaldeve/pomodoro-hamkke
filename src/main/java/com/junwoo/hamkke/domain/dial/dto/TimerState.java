package com.junwoo.hamkke.domain.dial.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Getter
public class TimerState {

    private final UUID roomId;

    private TimerPhase phase;
    private int phaseDurationSeconds;

    private int defaultFocusMinutes;
    private int defaultBreakMinutes;

    private int totalSessions;
    private int currentSession;

    private Integer nextFocusMinutes;

    private long accumulatedSeconds;

    private boolean phaseCompleted;

    private boolean running;

    private long phaseStartTime;

    public static TimerState createFocus(UUID roomId, TimerStartRequest request) {
        return new TimerState(
                roomId,
                TimerPhase.FOCUS,
                request.focusMinutes() * 60,
                request.focusMinutes(),
                request.breakMinutes(),
                request.totalSessions(),
                1,
                null,
                true,
                System.currentTimeMillis()
        );
    }

    public static TimerState createPermanent(
            UUID roomId,
            int focusMinutes,
            int breakMinutes,
            TimerPhase phase,
            long phaseStartTime
    ) {
        return new TimerState(
                roomId,
                phase,
                phase == TimerPhase.FOCUS ? focusMinutes * 60 : breakMinutes * 60,
                focusMinutes,
                breakMinutes,
                Integer.MAX_VALUE,
                1,
                null,
                true,
                phaseStartTime
        );
    }

    private TimerState(
            UUID roomId,
            TimerPhase phase,
            int phaseDurationSeconds,
            int defaultFocusMinutes,
            int defaultBreakMinutes,
            int totalSessions,
            int currentSession,
            Integer nextFocusMinutes,
            boolean running,
            long phaseStartTime
    ) {
        this.roomId = roomId;
        this.phase = phase;
        this.phaseDurationSeconds = phaseDurationSeconds;
        this.defaultFocusMinutes = defaultFocusMinutes;
        this.defaultBreakMinutes = defaultBreakMinutes;
        this.totalSessions = totalSessions;
        this.currentSession = currentSession;
        this.nextFocusMinutes = nextFocusMinutes;
        this.running = running;
        this.phaseStartTime = phaseStartTime;
    }

    public int calculateRemainingSeconds() {
        long elapsed;

        if (running) {
            elapsed = accumulatedSeconds +
                    (System.currentTimeMillis() - phaseStartTime) / 1000;
        } else {
            elapsed = accumulatedSeconds;
        }
        return (int) Math.max(0, phaseDurationSeconds - elapsed);
    }

    public boolean isPhaseFinished() {
        return calculateRemainingSeconds() <= 0;
    }

    public void pause() {
        if (!running) return;
        long elapsed = (System.currentTimeMillis() - phaseStartTime) / 1000;
        accumulatedSeconds += elapsed;
        running = false;
    }

    public void resume() {
        if (running) return;
        phaseStartTime = System.currentTimeMillis();
        running = true;
    }

    public void startPhase(int durationSeconds) {
        this.phaseDurationSeconds = durationSeconds;
        this.phaseStartTime = System.currentTimeMillis();
        this.accumulatedSeconds = 0;
        this.running = true;
        this.phaseCompleted = false;
    }

    public void finish() {

        if (!running) return;

        // 마지막 경과 시간 반영
        long elapsed = (System.currentTimeMillis() - phaseStartTime) / 1000;

        accumulatedSeconds += elapsed;

        running = false;
        phase = TimerPhase.FINISHED;
    }

    public int moveToNextFocusSession() {
        currentSession++;
        int focusMinutes = nextFocusMinutes != null ? nextFocusMinutes : defaultFocusMinutes;
        nextFocusMinutes = null;
        phase = TimerPhase.FOCUS;
        startPhase(focusMinutes * 60);
        return currentSession;
    }

    public void moveToBreakPhase() {

        phase = TimerPhase.BREAK;

        int breakSeconds = defaultBreakMinutes * 60;

        startPhase(breakSeconds);
    }

    public boolean updateNextFocusMinutes(int focusMinutes) {

        if (phase != TimerPhase.BREAK) {
            return false;
        }

        this.nextFocusMinutes = focusMinutes;
        return true;
    }

    public void markPhaseCompleted() {
        this.phaseCompleted = true;
    }
}