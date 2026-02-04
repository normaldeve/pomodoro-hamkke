package com.junwoo.hamkke.domain.room_member.listener;

import com.junwoo.hamkke.common.websocket.event.AutoLeaveEvent;
import com.junwoo.hamkke.domain.room_member.service.StudyRoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 웹 소켓 연결 해제 이후 자동 퇴장 이벤트 처리
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoLeaveEventListener {

    private final StudyRoomMemberService studyRoomMemberService;

    @Async
    @EventListener
    @Transactional
    public void onAutoLeave(AutoLeaveEvent event) {
        log.info("[AutoLeaveEventListener] 자동 퇴장 처리 - userId: {}, roomId: {}, reason: {}",
                event.userId(), event.roomId(), event.reason());

        studyRoomMemberService.leaveRoom(event.roomId(), event.userId());

        log.info("[AutoLeaveEventListener] 자동 퇴장 완료 - userId: {}, roomId: {}", event.userId(), event.roomId());
    }
}
