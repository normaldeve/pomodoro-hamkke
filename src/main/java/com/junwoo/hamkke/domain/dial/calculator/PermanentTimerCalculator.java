package com.junwoo.hamkke.domain.dial.calculator;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;

public class PermanentTimerCalculator {

    private PermanentTimerCalculator() {}

    public static Result calculate(int focusMinutes, int breakMinutes) {

        int focusSeconds = focusMinutes * 60;
        int breakSeconds = breakMinutes * 60;
        int cycleSeconds = focusSeconds + breakSeconds;

        long nowMillis = System.currentTimeMillis();
        long nowSeconds = nowMillis / 1000;

        // 정각 기준: 현재 시간에서 분 + 초
        int secondsInHour =
                (int) ((nowSeconds / 60 % 60) * 60 + (nowSeconds % 60));

        int elapsedInCycle = secondsInHour % cycleSeconds;

        TimerPhase phase;
        int remainingSeconds;
        long phaseStartTime;

        if (elapsedInCycle < focusSeconds) {
            // FOCUS 구간
            phase = TimerPhase.FOCUS;
            remainingSeconds = focusSeconds - elapsedInCycle;
            phaseStartTime = nowMillis - (elapsedInCycle * 1000L);
        } else {
            // BREAK 구간
            phase = TimerPhase.BREAK;
            int elapsedInBreak = elapsedInCycle - focusSeconds;
            remainingSeconds = breakSeconds - elapsedInBreak;
            phaseStartTime = nowMillis - (elapsedInBreak * 1000L);
        }

        return new Result(phase, remainingSeconds, phaseStartTime);
    }

    // 결과 DTO (내부 전용)
    public record Result(
            TimerPhase phase,
            int remainingSeconds,
            long phaseStartTime
    ) {}
}