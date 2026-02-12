package com.junwoo.hamkke.domain.dial.service;

import com.junwoo.hamkke.domain.dial.calculator.PermanentTimerCalculation;
import com.junwoo.hamkke.domain.dial.calculator.PermanentTimerCalculator;
import com.junwoo.hamkke.domain.dial.dto.*;
import com.junwoo.hamkke.domain.dial.dto.event.*;
import com.junwoo.hamkke.domain.dial.schedule.TimerScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
@RequiredArgsConstructor
public class TimerStateService {

    private final ApplicationEventPublisher eventPublisher;
    private final PermanentTimerCalculator permanentTimerCalculator;
    private final TimerTickBroadcaster tickBroadcaster;
    private final TimerScheduler timerScheduler;

    private final Map<UUID, TimerState> timerState = new ConcurrentHashMap<>();

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

    // 상시 운영 방 타이머 시작 (정각 기준)
    public void startPermanent(UUID roomId, int focusMinutes, int breakMinutes) {
        stop(roomId);

        log.info("[TimerStateService] startPermanent() : 상시 운영 방 타이머 시작 - roomId: {}, focus: {}분, break: {}분",
                roomId, focusMinutes, breakMinutes);

        // 정각 기준 타이머 계산
        PermanentTimerCalculation calc = permanentTimerCalculator.calculatePermanentTimerState(focusMinutes, breakMinutes);

        TimerState state = TimerState.createPermanent(
                roomId,
                focusMinutes,
                breakMinutes,
                calc.phase(),
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
    }

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

        // 총 세션 완료 시 종료
        if (state.getCurrentSession() >= state.getTotalSessions()) {
            finishTimer(state);
            return;
        }

        switchToBreak(state);
    }

    private void switchToBreak(TimerState state) {
        log.info("[TimerStateService] switchToBreak() : 휴식 전환 - roomId: {}", state.getRoomId());

        state.moveToBreakPhase();

        log.info("[TimerStateService] switchToBreak() : 타이머 상태 변경 이벤트를 생성합니다 - roomId: {}, phase: {}", state.getRoomId(), TimerPhase.BREAK);
        eventPublisher.publishEvent(new StartBreakTimeEvent(state.getRoomId()));
    }

    private void onBreakFinished(TimerState state) {
        startNextFocus(state);

        log.info("[TimerStateService] onBreakFinished() : 휴식이 종료되며 세션이 종료되었습니다 - roomId: {}", state.getRoomId());
        eventPublisher.publishEvent(new SessionFinishedEvent(state.getRoomId()));
    }

    public void startNextFocus(TimerState state) {
        log.info("[TimerStateService] startNextFocus() : 다음 세션 시작 - roomId: {}, session: {}", state.getRoomId(), state.moveToNextFocusSession());

        int session = state.moveToNextFocusSession();

        log.info("[TimerStateService] startNextFocus() : 다음 세션 이벤트를 생성합니다 - roomId: {}, session: {}", state.getRoomId(), state.getCurrentSession());
        eventPublisher.publishEvent(new FocusTimeStartedEvent(state.getRoomId(), session));
    }

    // 전체 세션 이후 타이머 종료
    private void finishTimer(TimerState state) {
        log.info("[TimerStateService] finishTimer() : 타이머 완료 roomId: {}, 총 세션: {}", state.getRoomId(), state.getTotalSessions());

        state.finish();
        stop(state.getRoomId());

        log.info("TimerStateService] finishTimer() : 타이머 상태 변경 이벤트를 생성합니다 - roomId: {}, phase: {}", state.getRoomId(), TimerPhase.FINISHED);
        eventPublisher.publishEvent(new TotalSessionFinishedEvent(state.getRoomId()));
    }

    // 다음 집중 시간 업데이트 (상시 운영 방은 불가)
    public void updateNextFocusTime(UUID roomId, int focusMinutes) {
        log.info("[TimerStateService] updateNextFocusTime() : 다음 집중 시간 설정을 변경합니다 - roomId: {}", roomId);

        TimerState state = timerState.get(roomId);
        if(state == null) return;

        state.updateNextFocusMinutes(focusMinutes);

        eventPublisher.publishEvent(new FocusTimeChangedEvent(roomId, state.getNextFocusMinutes()));
    }

    // 타이머 정지
    public void pause(UUID roomId) {
        log.info("[TimerStateService] pause() : 타이머를 일시정지 합니다 - roomId: {}", roomId);

        TimerState state = timerState.get(roomId);
        if (state == null) {
            return;
        }
        state.pause();
        stop(roomId);
        tickBroadcaster.sendTick(TimerTickMessage.from(state, state.calculateRemainingSeconds()));
    }

    // 타이머 재개
    public void resume(UUID roomId) {
        log.info("[TimerStateService] resume() : 타이머를 재개합니다 - roomId: {}", roomId);

        TimerState state = timerState.get(roomId);

        if (state == null) return;
        state.resume();
        startTick(roomId);
        tickBroadcaster.sendTick(TimerTickMessage.from(state, state.calculateRemainingSeconds()));
    }

    // 타이머 스케줄러 삭제
    public void stop(UUID roomId) {
        timerScheduler.cancel(roomId);
    }

    // 타이머 스케줄러 시작
    private void startTick(UUID roomId) {

        timerScheduler.schedule(roomId, () -> {

            TimerState state = timerState.get(roomId);
            if (state == null || !state.isRunning()) {
                return;
            }

            if (!state.isPhaseCompleted() && state.isPhaseFinished()) {
                state.markPhaseCompleted();
                onPhaseFinished(state);
                return;
            }

            int remaining = state.calculateRemainingSeconds();
            tickBroadcaster.sendTick(TimerTickMessage.from(state, remaining));
        });
    }

    /**
     * 방 삭제 시 타이머 완전 정리
     * - 스케줄러 종료
     * - 타이머 상태 제거
     */
    public void cleanupTimer(UUID roomId) {
        log.info("[TimerStateService] cleanupTimer() : 방 삭제로 인한 타이머 정리 시작 - roomId: {}", roomId);

        // 스케줄러 종료
        stop(roomId);

        // 타이머 상태 제거
        TimerState removedState = timerState.remove(roomId);

        log.info("[TimerStateService] cleanupTimer() : 타이머 정리 완료 - roomId: {}", roomId);
    }
}