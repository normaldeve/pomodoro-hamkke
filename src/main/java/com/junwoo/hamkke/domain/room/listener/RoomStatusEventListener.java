package com.junwoo.hamkke.domain.room.listener;

import com.junwoo.hamkke.common.discord.DiscordNotifier;
import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.event.TimerPhaseChangeEvent;
import com.junwoo.hamkke.domain.room.entity.RoomStatus;
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
 * @date 26. 1. 26.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoomStatusEventListener {

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
    public void handle(TimerPhaseChangeEvent event) {
        log.info("[RoomStatusEventListener] handle() : TimerPhaseChangeEvent 이벤트를 처리합니다 - roomId: {}, phase: {}", event.roomId(), event.phase());
        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        RoomStatus newStatus = switch (event.phase()) {
            case FOCUS -> RoomStatus.FOCUS;
            case BREAK -> RoomStatus.BREAK;
            case FINISHED -> RoomStatus.FINISHED;
            case IDLE -> RoomStatus.WAITING;
        };

        room.changeStatus(newStatus);

        log.info("[RoomStatusEventListener] handle() : 방 상태를 변경 완료했습니다 - roomId: {}, status: {}", room.getId(), newStatus);

        messagingTemplate.convertAndSend(WebSocketDestination.roomStatus(room.getId()), newStatus);
    }

    @Recover
    public void recover(
            Exception e,
            TimerPhaseChangeEvent event
    ) {
        discordNotifier.sendError(
                "RoomStatusEventListener 재시도 실패",
                """
                roomId: %s
                phase: %s
                exception: %s
                """.formatted(
                        event.roomId(),
                        event.phase(),
                        e.getClass().getSimpleName()
                )
        );
        log.error("[RoomStatusEventListener] 재시도 실패 - roomId={}, phase={}", event.roomId(), event.phase(), e);
    }
}
