package com.junwoo.hamkke.domain.reflection.listener;

import com.junwoo.hamkke.common.websocket.WebSocketDestination;
import com.junwoo.hamkke.domain.dial.dto.event.ReflectionCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReflectionEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onReflectionPrompt(ReflectionCreateEvent event) {

        messagingTemplate.convertAndSend(WebSocketDestination.reflection(event.roomId()), event);

        log.info("[ReflectionPromptEvent] onReflectionPrompt() WS 전송 - roomId={}, sessionId={}", event.roomId(), event.sessionId());
    }
}
