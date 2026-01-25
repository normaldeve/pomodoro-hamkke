package com.junwoo.hamkke.domain.message.controller;

import com.junwoo.hamkke.domain.auth.dto.AuthDTO;
import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.message.dto.MessageResponse;
import com.junwoo.hamkke.domain.message.dto.SendMessageRequest;
import com.junwoo.hamkke.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/study-room/{roomId}/messages")
    public void sendChannelMessage(
            @DestinationVariable Long roomId,
            @Payload SendMessageRequest request
    ) {

        Long senderId = request.senderId();

        MessageResponse response = messageService.sendMessage(roomId, request, senderId);

        try {
            messagingTemplate.convertAndSend("/topic/study-room/" + roomId + "/messages", response);
        } catch (Exception e) {
            log.error("[WS] Message 전송 실패 error: {}", e.getMessage(), e);
        }

        log.info("[WS] 채널 메시지 전송 - channelId = {}, senderId = {}", roomId, senderId);
    }
}