package com.junwoo.hamkke.domain.dial.calculator;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import org.springframework.stereotype.Component;

@Component
public class PermanentTimerCalculator {

    public PermanentTimerCalculation calculatePermanentTimerState(int focusMinutes, int breakMinutes) {

        int cycleMinutes = focusMinutes + breakMinutes;
        int cycleDurationSeconds = cycleMinutes * 60;

        // 현재 시간에서 분/초 추출
        long now = System.currentTimeMillis();
        int currentMinute = (int) ((now / 1000 / 60) % 60);
        int currentSecond = (int) ((now / 1000) % 60);

        // 정각 기준으로 몇 초 경과했는지
        int elapsedSecondsInHour = currentMinute * 60 + currentSecond;

        // 현재 사이클 내에서 몇 초 경과했는지
        int elapsedInCycle = elapsedSecondsInHour % cycleDurationSeconds;

        TimerPhase phase;
        int remainingSeconds;
        long phaseStartTime;

        if (elapsedInCycle < focusMinutes * 60) {
            // 집중 시간 중
            phase = TimerPhase.FOCUS;
            remainingSeconds = (focusMinutes * 60) - elapsedInCycle;
            phaseStartTime = now - (elapsedInCycle * 1000L);
        } else {
            // 휴식 시간 중
            phase = TimerPhase.BREAK;
            int elapsedInBreak = elapsedInCycle - (focusMinutes * 60);
            remainingSeconds = (breakMinutes * 60) - elapsedInBreak;
            phaseStartTime = now - (elapsedInBreak * 1000L);
        }

        return new PermanentTimerCalculation(phase, remainingSeconds, phaseStartTime);
    }
}