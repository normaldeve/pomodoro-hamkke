package com.junwoo.hamkke.domain.notification.listener;

import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeStartedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.StartBreakTimeEvent;
import com.junwoo.hamkke.domain.dial.dto.event.TotalSessionFinishedEvent;
import com.junwoo.hamkke.domain.notification.dto.NotificationType;
import com.junwoo.hamkke.domain.notification.dto.PushNotificationRequest;
import com.junwoo.hamkke.domain.notification.service.NotificationService;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

/**
 * 타이머 관련 이벤트에 대한 푸시 알림 리스너
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimerPushNotificationListener {

    private final NotificationService notificationService;
    private final StudyRoomRepository studyRoomRepository;

    /**
     * 집중 시간 시작 시 푸시 알림
     */
    @Async(value = "domainEventExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onFocusTimeStarted(FocusTimeStartedEvent event) {
        log.info("[TimerPushNotificationListener] 집중 시간 시작 푸시 알림 - roomId: {}, session: {}",
                event.roomId(), event.currentSessionId());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElse(null);

        if (room == null) {
            log.warn("[TimerPushNotificationListener] 방을 찾을 수 없습니다 - roomId: {}", event.roomId());
            return;
        }

        Map<String, String> variables = Map.of(
                "roomTitle", room.getTitle(),
                "session", String.valueOf(event.currentSessionId()),
                "focusMinutes", String.valueOf(room.getFocusMinutes())
        );

        Map<String, String> data = Map.of(
                "type", NotificationType.FOCUS_STARTED.name(),
                "roomId", event.roomId().toString(),
                "sessionId", String.valueOf(event.currentSessionId()),
                "focusMinutes", String.valueOf(room.getFocusMinutes())
        );

        PushNotificationRequest request = NotificationType.FOCUS_STARTED.createRequest(variables, data);
        notificationService.sendToRoomMembers(event.roomId(), request);
    }

    /**
     * 휴식 시간 시작 시 푸시 알림
     */
    @Async(value = "domainEventExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onBreakTimeStarted(StartBreakTimeEvent event) {
        log.info("[TimerPushNotificationListener] 휴식 시간 시작 푸시 알림 - roomId: {}", event.roomId());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElse(null);

        if (room == null) {
            log.warn("[TimerPushNotificationListener] 방을 찾을 수 없습니다 - roomId: {}", event.roomId());
            return;
        }

        Map<String, String> variables = Map.of(
                "roomTitle", room.getTitle(),
                "breakMinutes", String.valueOf(room.getBreakMinutes())
        );

        Map<String, String> data = Map.of(
                "type", NotificationType.BREAK_STARTED.name(),
                "roomId", event.roomId().toString(),
                "breakMinutes", String.valueOf(room.getBreakMinutes())
        );

        PushNotificationRequest request = NotificationType.BREAK_STARTED.createRequest(variables, data);
        notificationService.sendToRoomMembers(event.roomId(), request);
    }

    /**
     * 전체 세션 종료 시 푸시 알림
     */
    @Async(value = "domainEventExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTotalSessionFinished(TotalSessionFinishedEvent event) {
        log.info("[TimerPushNotificationListener] 전체 세션 종료 푸시 알림 - roomId: {}", event.roomId());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElse(null);

        if (room == null) {
            log.warn("[TimerPushNotificationListener] 방을 찾을 수 없습니다 - roomId: {}", event.roomId());
            return;
        }

        Map<String, String> variables = Map.of(
                "roomTitle", room.getTitle()
        );

        Map<String, String> data = Map.of(
                "type", NotificationType.TOTAL_SESSION_FINISHED.name(),
                "roomId", event.roomId().toString(),
                "totalSessions", String.valueOf(room.getTotalSessions())
        );

        PushNotificationRequest request = NotificationType.TOTAL_SESSION_FINISHED.createRequest(variables, data);
        notificationService.sendToRoomMembers(event.roomId(), request);
    }
}