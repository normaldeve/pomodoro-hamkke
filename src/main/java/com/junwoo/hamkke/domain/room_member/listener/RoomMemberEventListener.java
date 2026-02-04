package com.junwoo.hamkke.domain.room_member.listener;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.common.websocket.WebSocketConnectionTracker;
import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.service.TimerStateService;
import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.room_member.dto.HostTransferredResponse;
import com.junwoo.hamkke.domain.room_member.dto.event.HostTransferredEvent;
import com.junwoo.hamkke.domain.room_member.dto.event.MemberLeftRoomEvent;
import com.junwoo.hamkke.domain.room_member.dto.event.RoomEmptiedEvent;
import com.junwoo.hamkke.domain.room_member.service.StudyRoomMemberService;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoomMemberEventListener {

    private final WebSocketConnectionTracker connectionTracker;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMemberService studyRoomMemberService;
    private final UserRepository userRepository;
    private final TimerStateService timerStateService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @Transactional
    public void onMemberLeft(MemberLeftRoomEvent event) {
        log.info("[RoomMemberEventListener] onMemberLeft() - roomId: {}, userId: {}, wasHost: {}, remainingMembers: {}",
                event.roomId(), event.userId(), event.wasHost(), event.remainingMembers());

        // 멤버가 퇴장 시에 WebSocket 추적을 해제 합니다.
        connectionTracker.onUserLeftRoom(event.userId());

        if (event.remainingMembers() == 0) {
            // 방에 사용자가 남아 있지 않는 경우 -> 방 삭제
            eventPublisher.publishEvent(new RoomEmptiedEvent(event.roomId(), event.userId()));
        } else if (event.wasHost()) {
            // 방장이 나갔지만 다른 멤버가 있는 경우 -> 방장 권한 위임
            studyRoomMemberService.transferHostToOldestMember(event.roomId());
        }
    }

    @EventListener
    @Transactional
    public void onRoomEmptied(RoomEmptiedEvent event) {
        log.info("[RoomMemberEventListener] onRoomEmptied() - roomId: {}, lastMemberId: {}",
                event.roomId(), event.lastMemberId());

        StudyRoomEntity room = studyRoomRepository.findById(event.roomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        RoomStatus previousStatus = room.getStatus();
        room.changeStatus(RoomStatus.FINISHED);

        // 타이머 정리
        timerStateService.cleanupTimer(event.roomId());

        log.info("[RoomMemberEventListener] 마지막 멤버가 나가서 방을 삭제합니다 - roomId: {}, 이전 상태: {}",
                event.roomId(), previousStatus);

        try {
            messagingTemplate.convertAndSend(WebSocketDestination.roomStatus(event.roomId()), RoomStatus.FINISHED);
        } catch (Exception e) {
            log.error("[WS] 방 삭제 상태 전송 실패 - roomId: {}", event.roomId(), e);
        }
    }

    @EventListener
    @Transactional(readOnly = true)
    public void onHostTransferred(HostTransferredEvent event) {
        log.info("[RoomMemberEventListener] onHostTransferred() - roomId: {}, from: {}, to: {}, auto: {}",
                event.roomId(), event.previousHostId(), event.newHostId(), event.isAutoTransfer());

        UserEntity previousHost = userRepository.findById(event.previousHostId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_USER));

        UserEntity newHost = userRepository.findById(event.newHostId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_USER));

        HostTransferredResponse response = HostTransferredResponse.of(
                previousHost.getId(),
                previousHost.getNickname(),
                previousHost.getProfileUrl(),
                newHost.getId(),
                newHost.getNickname(),
                newHost.getProfileUrl(),
                event.isAutoTransfer()
        );

        try {
            messagingTemplate.convertAndSend(WebSocketDestination.member(event.roomId()), response);

            log.info("[RoomMemberEventListener] 방장 변경 알림 전송 완료 - roomId: {}, {} -> {}",
                    event.roomId(), previousHost.getNickname(), newHost.getNickname());
        } catch (Exception e) {
            log.error("[WS] 방장 변경 알림 전송 실패 - roomId: {}, newHostId: {}",
                    event.roomId(), event.newHostId(), e);
        }
    }
}
