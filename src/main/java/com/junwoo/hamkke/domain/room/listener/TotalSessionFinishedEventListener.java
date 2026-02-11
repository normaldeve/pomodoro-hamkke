package com.junwoo.hamkke.domain.room.listener;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.event.TotalSessionFinishedEvent;
import com.junwoo.hamkke.domain.room.dto.RoomInfoMessage;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 전체 세션이 종료되었을 때 발생하는 이벤트 처리
 * @author junnukim1007gmail.com
 * @date 26. 2. 10.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TotalSessionFinishedEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final StudyRoomRepository studyRoomRepository;

    @Async(value = "domainEventExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(TotalSessionFinishedEvent event) {
        log.info("[TotalSessionFinishedEventListener] 전체 세션이 종료되어, 방 상태를 FINISHED로 변경합니다 - roomId: {}", event.roomId());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        room.totalSessionFinish();

        RoomInfoMessage roomInfo = new RoomInfoMessage(room.getStatus(), room.getCurrentSession(), room.getTotalSessions());
        messagingTemplate.convertAndSend(WebSocketDestination.roomStatus(room.getId()), roomInfo);
    }
}
