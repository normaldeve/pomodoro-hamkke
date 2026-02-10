package com.junwoo.hamkke.domain.room.listener;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.event.StartBreakTimeEvent;
import com.junwoo.hamkke.domain.room.dto.RoomInfoMessage;
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
 * @date 26. 2. 10.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartBreakTimeListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final StudyRoomRepository studyRoomRepository;

    @EventListener
    @Transactional
    public void handle(StartBreakTimeEvent event) {
        log.info("[StartBreakTimeListener] 쉬는 시간이 시작되는 이벤트를 수신하여, 방 상태를 변경합니다 - roomId: {}", event.roomId());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        room.changeBreakStatus();

        RoomInfoMessage roomInfo = new RoomInfoMessage(room.getStatus(), room.getCurrentSession(), room.getTotalSessions());
        messagingTemplate.convertAndSend(WebSocketDestination.roomStatus(room.getId()), roomInfo);
    }
}