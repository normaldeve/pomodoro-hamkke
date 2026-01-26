package com.junwoo.hamkke.domain.dial.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.dial.dto.DialDragMessage;
import com.junwoo.hamkke.domain.dial.dto.TimerStartRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class DialController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/study-room/{roomId}/timer/dial-drag")
    public void handleDialDrag(
            @DestinationVariable Long roomId,
            @Payload DialDragMessage message
    ) {
        try {
            messagingTemplate.convertAndSend(
                    "/topic/study-room/" + roomId + "/timer",
                    message
            );
        } catch (Exception e) {
            log.error("다이얼 드래그 메시지 처리 실패: roomId={}, error={}", roomId, e.getMessage(), e);
        }
    }
}
