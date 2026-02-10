package com.junwoo.hamkke.domain.dial.service;

import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import com.junwoo.hamkke.domain.dial.dto.TimerState;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.service.policy.TimerPolicy;
import com.junwoo.hamkke.domain.dial.service.scheduler.TimerScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TimerStateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final TimerScheduler scheduler;
    private final List<TimerPolicy> policies;

    private final Map<UUID, TimerState> states = new ConcurrentHashMap<>();

    public void start(UUID roomId, TimerStartRequest request, boolean permanent) {
        TimerPolicy policy = policies.stream()
                .filter(p -> p.supportsPermanent() == permanent)
                .findFirst()
                .orElseThrow();

        TimerState state = policy.initialize(roomId, request);
        states.put(roomId, state);

        scheduler.start(
                roomId,
                state,
                policy,
                () -> messagingTemplate.convertAndSend(
                        WebSocketDestination.timer(roomId),
                        state
                )
        );
    }

    public void pause(UUID roomId) {
        TimerState state = states.get(roomId);
        if (state == null) return;

        state.pause();
        scheduler.stop(roomId);
    }

    public void resume(UUID roomId) {
        TimerState state = states.get(roomId);
        if (state == null) return;

        state.resume();
    }

    public void updateNextFocusTime(UUID roomId, int focusMinutes) {
        TimerState state = states.get(roomId);
        if (state == null) return;

        // 일반 세션 + BREAK 중일 때만 허용
        if (state.getPhase() != TimerPhase.BREAK) return;

        state.setNextFocusMinutes(focusMinutes);
    }

    public void cleanup(UUID roomId) {
        scheduler.stop(roomId);
        states.remove(roomId);
    }
}