package com.junwoo.hamkke.domain.room.event;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.dial.dto.event.TimerPhaseChangeEvent;
import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Async("domainEventExecutor")
    @EventListener
    @Transactional
    public void handleTimerPhaseChanged(TimerPhaseChangeEvent event) {
        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        RoomStatus newStatus = switch (event.phase()) {
            case FOCUS -> RoomStatus.FOCUS;
            case BREAK -> RoomStatus.BREAK;
            case FINISHED -> RoomStatus.FINISHED;
            case IDLE -> RoomStatus.WAITING;
        };

        room.changeStatus(newStatus);

        messagingTemplate.convertAndSend("/topic/study-room/" + room.getId() + "/status", newStatus);
    }
}
