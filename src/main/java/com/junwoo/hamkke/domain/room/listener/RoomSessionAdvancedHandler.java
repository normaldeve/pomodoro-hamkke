package com.junwoo.hamkke.domain.room.listener;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.dial.dto.event.RoomSessionAdvancedEvent;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
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
public class RoomSessionAdvancedHandler {

    private final StudyRoomRepository studyRoomRepository;

    @EventListener
    @Transactional
    public void handleTimerPhaseChanged(RoomSessionAdvancedEvent event) {
        log.info("[RoomSessionAdvancedHandler] handle() : 세션이 종료되는 이벤트를 수신 - roomId: {}", event.roomId());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        room.finishSession();
    }
}