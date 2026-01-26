package com.junwoo.hamkke.domain.dial.service;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeChangedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.RoomSessionAdvancedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.RoomTimerStartedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.TimerPhaseChangeEvent;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.dto.TimerState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimerStateService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ApplicationEventPublisher eventPublisher;
    private final Map<Long, TimerState> timerState = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public void start(Long roomId, TimerStartRequest request) {
        stop(roomId);

        TimerState state = TimerState.createFocus(roomId, request);
        timerState.put(roomId, state);
        startTick(roomId);
        broadcast(state);

        eventPublisher.publishEvent(new RoomTimerStartedEvent(roomId, request.focusMinutes()));
    }

    public void pause(Long roomId) {
        TimerState state = timerState.get(roomId);
        if (state == null || !state.isRunning()) {
            return;
        }

        state.setRunning(false);
        stop(roomId);
        broadcast(state);
    }

    public void resume(Long roomId) {
        TimerState state = timerState.get(roomId);
        if (state == null || state.isRunning()) {
            return;
        }

        state.setRunning(true);
        state.setPhaseStartTime(System.currentTimeMillis());
        startTick(roomId);
        broadcast(state);
    }

    public void stop(Long roomId) {
        ScheduledFuture<?> task = tasks.remove(roomId);
        if (task != null) {
            task.cancel(false);
        }
    }

    public void updateNextFocusTime(Long roomId, int focusMinutes) {
        TimerState state = timerState.get(roomId);
        if (state == null || state.getPhase() != TimerPhase.BREAK) {
            return;
        }

        state.setNextFocusMinutes(focusMinutes);
        broadcast(state);

        eventPublisher.publishEvent(new FocusTimeChangedEvent(roomId, focusMinutes));
    }

    private void startTick(Long roomId) {
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            TimerState state = timerState.get(roomId);
            if (state == null || !state.isRunning()) {
                return;
            }

            state.setRemainingSeconds(state.getRemainingSeconds() - 1);

            if (state.getRemainingSeconds() <= 0) {
                onPhaseFinished(state);
            }

            broadcast(state);
        }, 1, 1, TimeUnit.SECONDS);

        tasks.put(roomId, task);
    }

    private void onPhaseFinished(TimerState state) {
        if (state.getPhase() == TimerPhase.FOCUS) {
            onFocusFinished(state);
        } else if (state.getPhase() == TimerPhase.BREAK) {
            onBreakFinished(state);
        }
    }

    private void switchToBreak(TimerState state) {
        state.setPhase(TimerPhase.BREAK);
        state.setPhaseDurationSeconds(state.getDefaultBreakMinutes() * 60);
        state.setRemainingSeconds(state.getDefaultBreakMinutes() * 60);
        state.setPhaseStartTime(System.currentTimeMillis());

        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.BREAK));
    }

    private void onFocusFinished(TimerState state) {
        if (state.getCurrentSession() >= state.getTotalSessions()) {
            finishTimer(state);
            return;
        }

        switchToBreak(state);
    }

    private void onBreakFinished(TimerState state) {
        if (state.getCurrentSession() >= state.getTotalSessions()) {
            finishTimer(state);
            return;
        }

        startNextFocus(state);
    }

    public void startNextFocus(TimerState state) {
        state.setCurrentSession(state.getCurrentSession() + 1);

        int focusMinutes = state.getNextFocusMinutes() != null
                ? state.getNextFocusMinutes() : state.getDefaultFocusMinutes();

        state.setNextFocusMinutes(null);
        state.setPhase(TimerPhase.FOCUS);
        state.setPhaseDurationSeconds(focusMinutes * 60);
        state.setRemainingSeconds(focusMinutes * 60);
        state.setPhaseStartTime(System.currentTimeMillis());

        eventPublisher.publishEvent(new RoomSessionAdvancedEvent(state.getRoomId(), state.getCurrentSession()));
    }

    private void finishTimer(TimerState state) {
        state.setRunning(false);
        stop(state.getRoomId());
        broadcast(state);

        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.FINISHED));
    }

    private void broadcast(TimerState state) {
        try {
            messagingTemplate.convertAndSend("/topic/study-room/" + state.getRoomId() + "/timer", state);
        } catch (Exception e) {
            log.error("[WS] 타이머 상태 전송 실패");
        }
    }
}