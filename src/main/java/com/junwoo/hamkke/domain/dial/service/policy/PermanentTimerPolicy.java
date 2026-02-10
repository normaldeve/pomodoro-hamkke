package com.junwoo.hamkke.domain.dial.service.policy;

import com.junwoo.hamkke.domain.dial.calculator.PermanentTimerCalculator;
import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import com.junwoo.hamkke.domain.dial.dto.TimerState;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.dto.event.TimerPhaseChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermanentTimerPolicy implements TimerPolicy {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public TimerState initialize(UUID roomId, TimerStartRequest request) {
        PermanentTimerCalculator.Result calc = PermanentTimerCalculator.calculate(request.focusMinutes(), request.breakMinutes());

        TimerState state = TimerState.createPermanent(
                roomId,
                request.focusMinutes(),
                request.breakMinutes(),
                calc.phase(),
                calc.remainingSeconds(),
                calc.phaseStartTime()
        );

        eventPublisher.publishEvent(new TimerPhaseChangeEvent(roomId, calc.phase()));

        return state;
    }

    @Override
    public void onTick(TimerState state) {
        state.setRemainingSeconds(state.getRemainingSeconds() - 1);
    }

    @Override
    public void onPhaseFinished(TimerState state) {
        TimerPhase next =
                state.getPhase() == TimerPhase.FOCUS
                        ? TimerPhase.BREAK
                        : TimerPhase.FOCUS;

        state.switchPhase(next);

        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), next));
    }

    @Override
    public boolean supportsPermanent() {
        return true;
    }
}
