package com.junwoo.hamkke.domain.reflection.controller;

import com.junwoo.hamkke.domain.reflection.dto.CreateReflectionRequest;
import com.junwoo.hamkke.domain.reflection.dto.ReflectionResponse;
import com.junwoo.hamkke.domain.reflection.service.ReflectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ReflectionWsController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ReflectionService reflectionService;

    @MessageMapping("/study-room/{roomId}/reflection")
    public void createReflection(
            @DestinationVariable Long roomId,
            @Payload CreateReflectionRequest request
    ) {

        ReflectionResponse response = reflectionService.createReflection(roomId, request);

        try {
            messagingTemplate.convertAndSend(
                    "/topic/study-room/" + roomId + "/reflection",
                    response
            );
        } catch (Exception e) {
            log.error("[WS] Reflection 전송 실패 error: {}", e.getMessage(), e);
        }

        log.info("[WS] 회고 전송 - roomId={}, userId={}", roomId,request.userId());
    }
}
