package com.junwoo.hamkke.domain.dial.service;

import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import com.junwoo.hamkke.domain.dial.dto.TimerTickMessage;
import com.junwoo.hamkke.domain.dial.dto.event.*;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import com.junwoo.hamkke.domain.dial.dto.TimerState;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * [TODO] 상시 운영 방 책임이 추가되면서 코드 베이스 개선 필요
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
// TimerStateService.java 수정
@Slf4j
@Service
@RequiredArgsConstructor
public class TimerStateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyRoomRepository studyRoomRepository;

    private final Map<UUID, TimerState> timerState = new ConcurrentHashMap<>();
    private final Map<UUID, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    // 타이머 시작
    public void start(UUID roomId, TimerStartRequest request) {
        stop(roomId);

        log.info("[TimerStateService] start() : 타이머를 시작합니다 - roomId: {}", roomId);

        TimerState state = TimerState.createFocus(roomId, request);
        timerState.put(roomId, state);
        startTick(roomId);

        log.info("[TimerStateService] start() :타이머 시작 관련 이벤트를 호출합니다 - roomId: {}", roomId);
        eventPublisher.publishEvent(new TimerStartEvent(roomId, state.getDefaultFocusMinutes()));
        eventPublisher.publishEvent(new FocusTimeStartedEvent(roomId, 1));
    }

    // 새로 추가: 상시 운영 방 타이머 시작 (정각 기준)
    public void startPermanent(UUID roomId, int focusMinutes, int breakMinutes) {
        stop(roomId);

        log.info("[TimerStateService] startPermanent() : 상시 운영 방 타이머 시작 - roomId: {}, focus: {}분, break: {}분",
                roomId, focusMinutes, breakMinutes);

        // 정각 기준 타이머 계산
        PermanentTimerCalculation calc = calculatePermanentTimerState(focusMinutes, breakMinutes);

        TimerState state = TimerState.createPermanent(
                roomId,
                focusMinutes,
                breakMinutes,
                calc.phase(),
                calc.remainingSeconds(),
                calc.phaseStartTime()
        );

        timerState.put(roomId, state);
        startTick(roomId);

        log.info("[TimerStateService] startPermanent() : 상시 운영 방 타이머 시작 완료 - roomId: {}, phase: {}, remaining: {}초",
                roomId, calc.phase(), calc.remainingSeconds());

        if (calc.phase() == TimerPhase.FOCUS) {
            eventPublisher.publishEvent(new TimerStartEvent(roomId, state.getDefaultFocusMinutes()));
            eventPublisher.publishEvent(new FocusTimeStartedEvent(roomId, 1));
        } else {
            eventPublisher.publishEvent(new StartBreakTimeEvent(roomId));
        }

        eventPublisher.publishEvent(new FocusTimeChangedEvent(roomId, focusMinutes));
    }

    // 새로 추가: 정각 기준 타이머 상태 계산
    private PermanentTimerCalculation calculatePermanentTimerState(int focusMinutes, int breakMinutes) {
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

    // 내부 레코드
    private record PermanentTimerCalculation(
            TimerPhase phase,
            int remainingSeconds,
            long phaseStartTime
    ) {}

    // 집중, 휴식 종료
    private void onPhaseFinished(TimerState state) {
        log.info("[TimerStateService] onPhaseFinished() : 페이즈 완료 처리 - roomId: {}, phase: {}",
                state.getRoomId(), state.getPhase());

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

        // 상시 운영 방이 아니고 총 세션 완료 시 종료
        if (!isPermanentRoom(state.getRoomId()) &&
                state.getCurrentSession() >= state.getTotalSessions()) {
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

        log.info("[TimerStateService] switchToBreak() : 타이머 상태 변경 이벤트를 생성합니다 - roomId: {}, phase: {}", state.getRoomId(), TimerPhase.BREAK);
        eventPublisher.publishEvent(new StartBreakTimeEvent(state.getRoomId()));
    }

    private void onBreakFinished(TimerState state) {
        startNextFocus(state);

        log.info("[TimerStateService] onBreakFinished() : 휴식이 종료되며 세션이 종료되었습니다 - roomId: {}", state.getRoomId());
        eventPublisher.publishEvent(new SessionFinishedEvent(state.getRoomId()));
    }

    public void startNextFocus(TimerState state) {
        log.info("[TimerStateService] startNextFocus() : 다음 세션 시작 - roomId: {}, session: {} -> {}",
                state.getRoomId(), state.getCurrentSession(), state.getCurrentSession() + 1);

        // 상시 운영 방이 아닐 때만 세션 증가
        if (!isPermanentRoom(state.getRoomId())) {
            state.setCurrentSession(state.getCurrentSession() + 1);
        }

        log.info("[TimerStateService] startNextFocus() : 다음 세션 이벤트를 생성합니다 - roomId: {}, session: {}",
                state.getRoomId(), state.getCurrentSession());
        eventPublisher.publishEvent(new FocusTimeStartedEvent(state.getRoomId(), state.getCurrentSession()));

        // 변경된 집중 시간이 있다면 해당 시간으로 변경 (일반 방만)
        int focusMinutes = state.getDefaultFocusMinutes();
        if (!isPermanentRoom(state.getRoomId()) && state.getNextFocusMinutes() != null) {
            focusMinutes = state.getNextFocusMinutes();
        }

        state.setNextFocusMinutes(null);
        state.setPhase(TimerPhase.FOCUS);
        state.setPhaseDurationSeconds(focusMinutes * 60);
        state.setRemainingSeconds(focusMinutes * 60);
        state.setPhaseStartTime(System.currentTimeMillis());
    }

    // 전체 세션 이후 타이머 종료
    private void finishTimer(TimerState state) {
        log.info("[TimerStateService] finishTimer() : 타이머 완료 roomId: {}, 총 세션: {}", state.getRoomId(), state.getTotalSessions());

        state.setRunning(false);
        stop(state.getRoomId());

        log.info("TimerStateService] finishTimer() : 타이머 상태 변경 이벤트를 생성합니다 - roomId: {}, phase: {}", state.getRoomId(), TimerPhase.FINISHED);
        eventPublisher.publishEvent(new TotalSessionFinishedEvent(state.getRoomId()));
    }

    // 다음 집중 시간 업데이트 (상시 운영 방은 불가)
    public void updateNextFocusTime(UUID roomId, int focusMinutes) {
        log.info("[TimerStateService] updateNextFocusTime() : 다음 집중 시간 설정을 변경합니다 - roomId: {}", roomId);

        // 상시 운영 방은 집중 시간 변경 불가
        if (isPermanentRoom(roomId)) {
            log.warn("[TimerStateService] updateNextFocusTime() : 상시 운영 방은 집중 시간 변경 불가 - roomId: {}", roomId);
            return;
        }

        TimerState state = timerState.get(roomId);
        if (state == null || state.getPhase() != TimerPhase.BREAK) {
            return;
        }

        state.setNextFocusMinutes(focusMinutes);

        eventPublisher.publishEvent(new FocusTimeChangedEvent(roomId, focusMinutes));
    }

    // 타이머 정지
    public void pause(UUID roomId) {
        log.info("[TimerStateService] pause() : 타이머를 일시정지 합니다 - roomId: {}", roomId);

        TimerState state = timerState.get(roomId);
        if (state == null || !state.isRunning()) {
            return;
        }

        state.setRunning(false);
        stop(roomId);
    }

    // 타이머 재개
    public void resume(UUID roomId) {
        log.info("[TimerStateService] resume() : 타이머를 재개합니다 - roomId: {}", roomId);

        TimerState state = timerState.get(roomId);
        if (state == null || state.isRunning()) {
            return;
        }

        state.setRunning(true);
        state.setPhaseStartTime(System.currentTimeMillis());
        startTick(roomId);
    }

    // 타이머 스케줄러 삭제
    public void stop(UUID roomId) {
        ScheduledFuture<?> task = tasks.remove(roomId);
        if (task != null) {
            task.cancel(false);
        }
    }

    // 타이머 스케줄러 시작
    private void startTick(UUID roomId) {
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

            TimerTickMessage tick = TimerTickMessage.from(state);
            broadcastTick(tick);
        }, 1, 1, TimeUnit.SECONDS);

        tasks.put(roomId, task);
    }

    /**
     * 방 삭제 시 타이머 완전 정리
     * - 스케줄러 종료
     * - 타이머 상태 제거
     */
    public void cleanupTimer(UUID roomId) {
        log.info("[TimerStateService] cleanupTimer() : 방 삭제로 인한 타이머 정리 시작 - roomId: {}", roomId);

        // 스케줄러 종료
        ScheduledFuture<?> task = tasks.remove(roomId);
        if (task != null) {
            boolean cancelled = task.cancel(false);
            log.info("[TimerStateService] cleanupTimer() : 스케줄러 취소 - roomId: {}, cancelled: {}",
                    roomId, cancelled);
        }

        // 타이머 상태 제거
        TimerState removedState = timerState.remove(roomId);
        if (removedState != null) {
            log.info("[TimerStateService] cleanupTimer() : 타이머 상태 제거 완료 - roomId: {}, phase: {}, running: {}",
                    roomId, removedState.getPhase(), removedState.isRunning());
        } else {
            log.info("[TimerStateService] cleanupTimer() : 타이머 상태 없음 (이미 종료되었거나 시작 안함) - roomId: {}",
                    roomId);
        }

        log.info("[TimerStateService] cleanupTimer() : 타이머 정리 완료 - roomId: {}", roomId);
    }

    // 상시 운영 방 여부 확인
    private boolean isPermanentRoom(UUID roomId) {
        return studyRoomRepository.findById(roomId)
                .map(StudyRoomEntity::isPermanent)
                .orElse(false);
    }

    private void broadcastTick(TimerTickMessage tick) {
        messagingTemplate.convertAndSend(WebSocketDestination.tick(tick.roomId()), tick);
    }
}