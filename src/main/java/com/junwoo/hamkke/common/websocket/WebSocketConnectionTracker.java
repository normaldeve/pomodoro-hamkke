package com.junwoo.hamkke.common.websocket;

import com.junwoo.hamkke.common.websocket.event.AutoLeaveEvent;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

/**
 * WebSocket 연결 상태 추적 및 자동 퇴장 처리
 * - 사용자의 방 참여 상태 추적
 * - 연결 해제 시 10초 타이머 시작
 * - 재연결 시 타이머 취소
 * - 타임아웃 시 자동 퇴장 이벤트 발행
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
@Slf4j
@Component
public class WebSocketConnectionTracker {

    // userId -> roomId 매핑
    private final Map<Long, Long> userRoomMap = new ConcurrentHashMap<>();

    // userId -> 재연결 타이머 매핑
    private final Map<Long, ScheduledFuture<?>> disconnectTimers = new ConcurrentHashMap<>();

    // 이벤트 발행용
    private final ApplicationEventPublisher eventPublisher;

    // 스케줄러 (스레드 풀)
    private final ScheduledExecutorService scheduler;

    // 재연결 대기 시간 (초)
    private static final long RECONNECT_TIMEOUT_SECONDS = 10;

    // 스레드 풀 크기
    private static final int THREAD_POOL_SIZE = 10;

    /**
     * 생성자
     */
    public WebSocketConnectionTracker(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.scheduler = Executors.newScheduledThreadPool(
                THREAD_POOL_SIZE,
                new ThreadFactory() {
                    private int count = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("websocket-disconnect-timer-" + count++);
                        thread.setDaemon(true); // 데몬 스레드로 설정
                        return thread;
                    }
                }
        );

        log.info("[ConnectionTracker] 초기화 완료 - 스레드 풀 크기: {}, 타임아웃: {}초",
                THREAD_POOL_SIZE, RECONNECT_TIMEOUT_SECONDS);
    }

    /**
     * 사용자가 방에 입장했을 때 호출
     */
    public void onUserJoinedRoom(Long userId, Long roomId) {
        log.info("[ConnectionTracker] 사용자 방 입장 추적 시작 - userId: {}, roomId: {}", userId, roomId);

        userRoomMap.put(userId, roomId);

        // 기존 타이머가 있다면 취소 (재연결된 경우)
        cancelDisconnectTimer(userId);
    }

    /**
     * 사용자가 방에서 나갔을 때 호출
     */
    public void onUserLeftRoom(Long userId) {
        Long roomId = userRoomMap.get(userId);

        if (roomId != null) {
            log.info("[ConnectionTracker] 사용자 방 퇴장 - userId: {}, roomId: {}", userId, roomId);
        } else {
            log.debug("[ConnectionTracker] 사용자 방 퇴장 - userId: {} (이미 퇴장 처리됨)", userId);
        }

        userRoomMap.remove(userId);
        cancelDisconnectTimer(userId);
    }

    /**
     * WebSocket 연결이 끊어졌을 때 호출
     */
    public void onWebSocketDisconnected(Long userId) {
        Long roomId = userRoomMap.get(userId);

        if (roomId == null) {
            log.debug("[ConnectionTracker] 연결 종료 - 방에 참여 중이지 않은 사용자 - userId: {}", userId);
            return;
        }

        log.info("[ConnectionTracker] WebSocket 연결 종료 - userId: {}, roomId: {}", userId, roomId);
        log.info("[ConnectionTracker] {}초 후 재연결되지 않으면 자동 퇴장 처리", RECONNECT_TIMEOUT_SECONDS);

        // 기존 타이머 취소 (혹시 모를 중복 방지)
        cancelDisconnectTimer(userId);

        // 10초 후 자동 퇴장 타이머 시작
        ScheduledFuture<?> timer = scheduler.schedule(
                () -> handleAutoLeave(userId, roomId),
                RECONNECT_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
        );

        disconnectTimers.put(userId, timer);
    }

    /**
     * WebSocket 재연결 시 호출
     */
    public void onWebSocketReconnected(Long userId) {
        Long roomId = userRoomMap.get(userId);

        if (roomId == null) {
            log.debug("[ConnectionTracker] 재연결 - 방에 참여 중이지 않은 사용자 - userId: {}", userId);
            return;
        }

        log.info("[ConnectionTracker] WebSocket 재연결 성공 - userId: {}, roomId: {} - 자동 퇴장 타이머 취소",
                userId, roomId);

        // 자동 퇴장 타이머 취소
        cancelDisconnectTimer(userId);
    }

    /**
     * 자동 퇴장 처리
     */
    private void handleAutoLeave(Long userId, Long roomId) {
        try {
            // 타이머 제거
            disconnectTimers.remove(userId);

            // 아직 방에 있는지 확인 (수동으로 나갔을 수도 있음)
            if (!userRoomMap.containsKey(userId)) {
                log.info("[ConnectionTracker] 이미 방을 나간 사용자 - userId: {}", userId);
                return;
            }

            log.warn("[ConnectionTracker] 재연결 타임아웃 - 자동 퇴장 처리 - userId: {}, roomId: {}", userId, roomId);

            // 자동 퇴장 이벤트 발행 (이벤트 리스너에서 실제 퇴장 처리)
            eventPublisher.publishEvent(new AutoLeaveEvent(userId, roomId, "reconnect_timeout"));

            log.info("[ConnectionTracker] AutoLeaveEvent 발행 완료 - userId: {}, roomId: {}", userId, roomId);

        } catch (Exception e) {
            log.error("[ConnectionTracker] 자동 퇴장 처리 중 오류 - userId: {}, roomId: {}", userId, roomId, e);
        }
    }

    /**
     * 타이머 취소
     */
    private void cancelDisconnectTimer(Long userId) {
        ScheduledFuture<?> timer = disconnectTimers.remove(userId);

        if (timer != null && !timer.isDone()) {
            boolean cancelled = timer.cancel(false);
            log.debug("[ConnectionTracker] 자동 퇴장 타이머 취소 - userId: {}, cancelled: {}",
                    userId, cancelled);
        }
    }

    /**
     * 애플리케이션 종료 시 스케줄러 정리
     */
    @PreDestroy
    public void shutdown() {
        log.info("[ConnectionTracker] 종료 처리 시작 - 추적 중인 사용자: {}, 대기 중인 타이머: {}",
                userRoomMap.size(), disconnectTimers.size());

        // 모든 타이머 취소
        disconnectTimers.values().forEach(timer -> {
            if (!timer.isDone()) {
                timer.cancel(false);
            }
        });
        disconnectTimers.clear();

        // 스케줄러 종료
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                log.warn("[ConnectionTracker] 스케줄러 강제 종료");
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("[ConnectionTracker] 스케줄러 종료 중 인터럽트 발생", e);
        }

        userRoomMap.clear();

        log.info("[ConnectionTracker] 종료 처리 완료");
    }
}