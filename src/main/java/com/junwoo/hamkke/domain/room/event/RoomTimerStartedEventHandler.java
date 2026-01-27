package com.junwoo.hamkke.domain.room.event;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.dial.dto.event.RoomTimerStartedEvent;
import com.junwoo.hamkke.domain.room.dto.RoomStateResponse;
import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.room_member.service.MemberFocusRuntimeService;
import lombok.RequiredArgsConstructor;
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
@Component
@RequiredArgsConstructor
public class RoomTimerStartedEventHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final StudyRoomRepository studyRoomRepository;
    private final MemberFocusRuntimeService runtimeService;

    @Async("domainEventExecutor")
    @EventListener
    @Transactional
    public void handle(RoomTimerStartedEvent event) {

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        room.handleTimerStartEvent(event.focusMinutes());

        runtimeService.startFocus(event.roomId());

        messagingTemplate.convertAndSend(
                "/topic/study-room/" + room.getId() + "/room-state",
                new RoomStateResponse(RoomStatus.FOCUS, event.focusMinutes())
        );
    }
}