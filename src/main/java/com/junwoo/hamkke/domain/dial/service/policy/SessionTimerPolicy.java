package com.junwoo.hamkke.domain.dial.service.policy;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.dto.TimerState;
import com.junwoo.hamkke.domain.dial.dto.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 10.
 */
@Component
@RequiredArgsConstructor
public class SessionTimerPolicy implements TimerPolicy{

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public TimerState initialize(UUID roomId, TimerStartRequest request) {
        TimerState state = TimerState.createFocus(roomId, request);

        eventPublisher.publishEvent(new TimerStartEvent(roomId, state.getDefaultFocusMinutes()));
        eventPublisher.publishEvent(new FocusTimeStartedEvent(roomId, state.getCurrentSession()));

        return state;
    }

    @Override
    public void onTick(TimerState state) {
        state.setRemainingSeconds(state.getRemainingSeconds() - 1);
    }

    @Override
    public void onPhaseFinished(TimerState state) {
        if (state.getPhase() == TimerPhase.FOCUS) {
            onPhaseFinished(state);
        } else {
            onBreakFinished(state);
        }
    }

    @Override
    public boolean supportsPermanent() {
        return false;
    }


    private void onFocusFinished(TimerState state) {
        eventPublisher.publishEvent(new FocusTimeFinishedEvent(state.getRoomId(), state.getDefaultFocusMinutes(), state.getCurrentSession()));

        eventPublisher.publishEvent(new ReflectionCreateEvent(state.getRoomId(), state.getCurrentSession()));

        if (state.getCurrentSession() >= state.getTotalSessions()) {
            state.finish();
            eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.FINISHED));
            return;
        }

        switchToBreak(state);
    }

    private void switchToBreak(TimerState state) {
        state.switchToBreak();
        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.BREAK));
    }

    private void onBreakFinished(TimerState state) {
        state.advanceSession();

        int focusMinutes = resolveNextFocusMinutes(state);

        state.switchToFocus(focusMinutes);

        eventPublisher.publishEvent(new FocusTimeStartedEvent(state.getRoomId(), state.getCurrentSession()));

        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.FOCUS));
    }

    private int resolveNextFocusMinutes(TimerState state) {
        if (state.getNextFocusMinutes() != null) {
            return state.getNextFocusMinutes();
        }
        return state.getDefaultFocusMinutes();
    }
}
