package com.junwoo.hamkke.domain.room.listener;

import com.junwoo.hamkke.common.discord.DiscordNotifier;
import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.event.TimerStartEvent;
import com.junwoo.hamkke.domain.room.dto.RoomInfoMessage;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 10.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimerStartEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final StudyRoomRepository studyRoomRepository;
    private final DiscordNotifier discordNotifier;

    @Async(value = "domainEventExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            value = {
                    OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    SocketTimeoutException.class,
                    ConnectException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void handle(TimerStartEvent event) {

        log.info("[TimerStartEventListener] 타이머 시작 이벤트를 처리합니다: roomId = {}, focusTime = {}", event.roomId(), event.focusTime());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        log.info("[TimerStartEventListener] 방 변경 이전 - roomId: {}, status: {}, focusTime: {}", event.roomId(), room.getStatus(), room.getFocusMinutes());
        room.statTimer(event.focusTime());
        log.info("[TimerStartEventListener] 방 변경 이후 - roomId: {}, status: {}, focusTime: {}", event.roomId(), room.getStatus(), room.getFocusMinutes());

        RoomInfoMessage roomInfo = new RoomInfoMessage(room.getStatus(), room.getCurrentSession(), room.getTotalSessions());

        messagingTemplate.convertAndSend(WebSocketDestination.focusTime(room.getId()), room.getFocusMinutes());
        messagingTemplate.convertAndSend(WebSocketDestination.roomStatus(room.getId()), roomInfo);
        log.info("[TimerStartEventListener] 방 정보 데이터를 전송합니다: roomId = {}, currentSession = {}, totalSessions = {}", room.getId(), room.getCurrentSession(), room.getTotalSessions());
    }

    @Recover
    public void recover(
            Exception e,
            TimerStartEvent event
    ) {
        discordNotifier.sendError(
                "RoomStatusEventListener 재시도 실패",
                """
                roomId: %s
                exception: %s
                """.formatted(
                        event.roomId(),
                        e.getClass().getSimpleName()
                )
        );
        log.error("[RoomStatusEventListener] 재시도 실패 - roomId={}", event.roomId(), e);
    }
}
