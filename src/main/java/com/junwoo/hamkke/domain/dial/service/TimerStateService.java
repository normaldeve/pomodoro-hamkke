package com.junwoo.hamkke.domain.dial.service;

import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import com.junwoo.hamkke.domain.dial.dto.event.*;
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

    // 타이머 시작
    public void start(Long roomId, TimerStartRequest request) {
        stop(roomId);

        log.info("[TimerStateService] start() : 타이머를 시작합니다 - roomId: {}", roomId);

        TimerState state = TimerState.createFocus(roomId, request);
        timerState.put(roomId, state);
        startTick(roomId);
        broadcast(state);

        log.info("[TimerStateService] start() :타이머 시작 관련 이벤트를 호출합니다 - roomId: {}", roomId);
        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.FOCUS));
        eventPublisher.publishEvent(new FocusTimeStartedEvent(state.getRoomId(), state.getCurrentSession()));
        eventPublisher.publishEvent(new FocusTimeChangedEvent(roomId, state.getDefaultFocusMinutes()));
    }

    // 집중, 휴식 종료
    private void onPhaseFinished(TimerState state) {
        log.info("[TimerStateService] onPhaseFinished() : 페이즈 완료 처리 - roomId: {}, phase: {}", state.getRoomId(), state.getPhase());
        if (state.getPhase() == TimerPhase.FOCUS) {
            onFocusFinished(state);
        } else if (state.getPhase() == TimerPhase.BREAK) {
            onBreakFinished(state);
        }
    }

    // 집중 시간 종료
    private void onFocusFinished(TimerState state) {
        log.info("[TimerStateService] onFocusFinished() : 집중 종료 - roomId: {}", state.getRoomId());
        eventPublisher.publishEvent(new FocusTimeFinishedEvent(state.getRoomId(), state.getDefaultFocusMinutes(), state.getCurrentSession()));
        eventPublisher.publishEvent(new ReflectionCreateEvent(state.getRoomId(), state.getCurrentSession()));
        if (state.getCurrentSession() >= state.getTotalSessions()) {
            finishTimer(state);
            return;
        }

        switchToBreak(state);
    }

    private void switchToBreak(TimerState state) {
        log.info("[TimerStateService] switchToBreak() : 휴식 전환 - roomId: {}", state.getRoomId());
        state.setPhase(TimerPhase.BREAK);
        state.setPhaseDurationSeconds(state.getDefaultBreakMinutes() * 60);
        state.setRemainingSeconds(state.getDefaultBreakMinutes() * 60);
        state.setPhaseStartTime(System.currentTimeMillis());
        broadcast(state);

        log.info("[TimerStateService] switchToBreak() : 타이머 상태 변경 이벤트를 생성합니다 - roomId: {}, phase: {}", state.getRoomId(), TimerPhase.BREAK);
        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.BREAK));
    }

    private void onBreakFinished(TimerState state) {
        log.info("[TimerStateService] onBreakFinished() : 휴식 종료 - roomId: {}", state.getRoomId());

        startNextFocus(state);
    }

    public void startNextFocus(TimerState state) {
        log.info("[TimerStateService] startNextFocus() : 다음 세션 시작 - roomId: {}, session: {} -> {}", state.getRoomId(), state.getCurrentSession(), state.getCurrentSession() + 1);

        state.setCurrentSession(state.getCurrentSession() + 1);
        eventPublisher.publishEvent(new FocusTimeStartedEvent(state.getRoomId(), state.getCurrentSession()));

        // 변경된 집중 시간이 있다면 해당 시간으로 변경
        int focusMinutes = state.getNextFocusMinutes() != null ? state.getNextFocusMinutes() : state.getDefaultFocusMinutes();

        state.setNextFocusMinutes(null);
        state.setPhase(TimerPhase.FOCUS);
        state.setPhaseDurationSeconds(focusMinutes * 60);
        state.setRemainingSeconds(focusMinutes * 60);
        state.setPhaseStartTime(System.currentTimeMillis());
        broadcast(state);

        log.info("[TimerStateService] startNextFocus() : 다음 세션 이벤트를 생성합니다 - roomId: {}, session: {} -> {}", state.getRoomId(), state.getCurrentSession(), state.getCurrentSession() + 1);
        eventPublisher.publishEvent(new RoomSessionAdvancedEvent(state.getRoomId()));
        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.FOCUS));
    }

    // 전체 세션 이후 타이머 종료
    private void finishTimer(TimerState state) {
        log.info("[TimerStateService] finishTimer() : 타이머 완료 roomId: {}, 총 세션: {}", state.getRoomId(), state.getTotalSessions());

        state.setRunning(false);
        stop(state.getRoomId());
        broadcast(state);

        log.info("TimerStateService] finishTimer() : 타이머 상태 변경 이벤트를 생성합니다 - roomId: {}, phase: {}", state.getRoomId(), TimerPhase.FINISHED);
        eventPublisher.publishEvent(new TimerPhaseChangeEvent(state.getRoomId(), TimerPhase.FINISHED));
    }

    // 다음 집중 시간 업데이트
    public void updateNextFocusTime(Long roomId, int focusMinutes) {
        log.info("[TimerStateService] updateNextFocusTime() : 다음 집중 시간 설정을 변경합니다 - roomId: {}", roomId);
        TimerState state = timerState.get(roomId);
        if (state == null || state.getPhase() != TimerPhase.BREAK) {
            return;
        }

        state.setNextFocusMinutes(focusMinutes);
        broadcast(state);

        eventPublisher.publishEvent(new FocusTimeChangedEvent(roomId, focusMinutes));
    }

    // 타이머 정지
    public void pause(Long roomId) {
        log.info("[TimerStateService] pause() : 타이머를 일시정지 합니다 - roomId: {}", roomId);
        TimerState state = timerState.get(roomId);
        if (state == null || !state.isRunning()) {
            return;
        }

        state.setRunning(false);
        stop(roomId);
        broadcast(state);
    }

    // 타이머 재개
    public void resume(Long roomId) {
        log.info("[TimerStateService] resume() : 타이머를 재개합니다 - roomId: {}", roomId);
        TimerState state = timerState.get(roomId);
        if (state == null || state.isRunning()) {
            return;
        }

        state.setRunning(true);
        state.setPhaseStartTime(System.currentTimeMillis());
        startTick(roomId);
        broadcast(state);
    }

    // 타이머 스케줄러 삭제
    public void stop(Long roomId) {
        ScheduledFuture<?> task = tasks.remove(roomId);
        if (task != null) {
            task.cancel(false);
        }
    }

    // 타이머 스케줄러 시작
    private void startTick(Long roomId) {
        log.info("[TimerStateService] startTick() : 스케줄러 시작 - roomId: {}", roomId);
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

    /**
     * 방 삭제 시 타이머 완전 정리
     * - 스케줄러 종료
     * - 타이머 상태 제거
     */
    public void cleanupTimer(Long roomId) {
        log.info("[TimerStateService] cleanupTimer() : 방 삭제로 인한 타이머 정리 시작 - roomId: {}", roomId);

        // 스케줄러 종료
        ScheduledFuture<?> task = tasks.remove(roomId);
        if (task != null) {
            boolean cancelled = task.cancel(false);
            log.info("[TimerStateService] cleanupTimer() : 스케줄러 취소 - roomId: {}, cancelled: {}", roomId, cancelled);
        }

        // 타이머 상태 제거
        TimerState removedState = timerState.remove(roomId);
        if (removedState != null) {
            log.info("[TimerStateService] cleanupTimer() : 타이머 상태 제거 완료 - roomId: {}, phase: {}, running: {}",
                    roomId, removedState.getPhase(), removedState.isRunning());
        } else {
            log.info("[TimerStateService] cleanupTimer() : 타이머 상태 없음 (이미 종료되었거나 시작 안함) - roomId: {}", roomId);
        }

        log.info("[TimerStateService] cleanupTimer() : 타이머 정리 완료 - roomId: {}", roomId);
    }

    private void broadcast(TimerState state) {
        try {
            log.info("[TimerStateService] broadcast() : 웹소켓을 통해 데이터를 전달합니다 - roomId: {}, state: {}", state.getRoomId(), state);
            messagingTemplate.convertAndSend(WebSocketDestination.timer(state.getRoomId()), state);
        } catch (Exception e) {
            log.error("[WS] 타이머 상태 전송 실패");
        }
    }
}