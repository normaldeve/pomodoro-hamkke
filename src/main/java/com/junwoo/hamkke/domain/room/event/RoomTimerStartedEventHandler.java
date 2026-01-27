package com.junwoo.hamkke.domain.room.event;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.dial.dto.event.RoomTimerStartedEvent;
import com.junwoo.hamkke.domain.room.dto.RoomStateResponse;
import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
public class RoomTimerStartedEventHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final StudyRoomRepository studyRoomRepository;

    @EventListener
    @Transactional
    public void handle(RoomTimerStartedEvent event) {

        log.info("[RoomTimerStartedEvent] 타이머 시작 관련 이벤트를 수신합니다 - roomId: {}", event.roomId());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        log.info("[RoomTimerStartedEvent] 집중 상태로 방 상태를 변경하고 집중 시간을 설정합니다 - roomId: {}", event.roomId());
        room.handleTimerStartEvent(event.focusMinutes());

        messagingTemplate.convertAndSend(
                "/topic/study-room/" + room.getId() + "/room-state",
                new RoomStateResponse(RoomStatus.FOCUS, event.focusMinutes())
        );
    }
}