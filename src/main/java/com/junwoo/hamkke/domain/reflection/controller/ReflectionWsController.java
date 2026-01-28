package com.junwoo.hamkke.domain.reflection.controller;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import com.junwoo.hamkke.domain.reflection.dto.CreateReflectionRequest;
import com.junwoo.hamkke.domain.reflection.dto.CreateReflectionResponse;
import com.junwoo.hamkke.domain.reflection.service.ReflectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

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

        CreateReflectionResponse response = reflectionService.createReflection(roomId, request);

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
