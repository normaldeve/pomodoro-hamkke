package com.junwoo.hamkke.domain.room_member.controller;

import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.room_member.dto.EnterStudyRoomRequest;
import com.junwoo.hamkke.domain.room_member.dto.ParticipantMemberInfo;
import com.junwoo.hamkke.domain.room_member.service.StudyRoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class StudyRoomMemberWsController {

    private final SimpMessagingTemplate messagingTemplate;
    private final StudyRoomMemberService studyRoomMemberService;

    @MessageMapping("/study-room/{roomId}/members/enter")
    public void enterMember(
            @DestinationVariable Long roomId,
            @Payload EnterStudyRoomRequest request
    ) {

        Optional<ParticipantMemberInfo> response = studyRoomMemberService.enterRoom(roomId, request.userId(), request);

        try {
            messagingTemplate.convertAndSend(WebSocketDestination.member(roomId), response);
        } catch (Exception e) {
            log.error("[WS] 사용자 방 입장 전송 실패 roomId={}", roomId, e);
        }
    }

    @MessageMapping("/study-room/{roomId}/members/exit")
    public void exitMember(
            @DestinationVariable Long roomId,
            @Payload Long userId
    ) {

        studyRoomMemberService.leaveRoom(roomId, userId);

        try {
            messagingTemplate.convertAndSend(WebSocketDestination.member(roomId), userId);
        } catch (Exception e) {
            log.error("[WS] 사용자 방 떠나기 실패 userId = {}", userId, e);
        }
    }
}
