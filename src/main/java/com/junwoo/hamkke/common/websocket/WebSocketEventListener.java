package com.junwoo.hamkke.common.websocket;

import com.junwoo.hamkke.domain.auth.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.UUID;

/**
 * WebSocket 연결/해제 이벤트 처리
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketConnectionTracker connectionTracker;

    /**
     * WebSocket 연결 시
     */
    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Authentication authentication = (Authentication) headerAccessor.getUser();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            log.warn("[WebSocket] 인증 정보 없음");
            return;
        }

        Long userId = userDetails.getUser().id();

        if (userId == null) {
            log.error("[WebSocket] userId 추출 실패 - sessionId: {}", sessionId);
            return;
        }

        log.info("[WebSocket] 연결 성공 - sessionId: {}, userId: {}", sessionId, userId);

        // 재연결 처리
        connectionTracker.onWebSocketReconnected(userId);
    }

    /**
     * WebSocket 연결 해제 시
     */
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Authentication authentication = (Authentication) headerAccessor.getUser();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            log.warn("[WebSocket] 인증 정보 없음");
            return;
        }

        Long userId = userDetails.getUser().id();

        if (userId == null) {
            log.error("[WebSocket] userId 추출 실패 - sessionId: {}", sessionId);
            return;
        }

        log.info("[WebSocket] 연결 해제 - sessionId: {}, userId: {}", sessionId, userId);

        // 10초 타이머 시작
        connectionTracker.onWebSocketDisconnected(userId);
    }

    /**
     * 방 구독 시
     */
    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        Authentication authentication = (Authentication) headerAccessor.getUser();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            log.warn("[WebSocket] 인증 정보 없음");
            return;
        }

        Long userId = userDetails.getUser().id();
        UUID roomId = extractRoomIdFromDestination(destination);

        if (userId != null && roomId != null) {
            log.info("[WebSocket] 방 구독 - userId: {}, roomId: {}, destination: {}",
                    userId, roomId, destination);
            connectionTracker.onUserJoinedRoom(userId, roomId);
        }
    }

    /**
     * destination에서 roomId(UUID) 추출
     * 예: /topic/study-room/550e8400-e29b-41d4-a716-446655440000/messages
     */
    private UUID extractRoomIdFromDestination(String destination) {
        try {
            if (destination == null || !destination.contains("study-room")) {
                return null;
            }

            String[] parts = destination.split("/");

            for (int i = 0; i < parts.length - 1; i++) {
                if ("study-room".equals(parts[i])) {
                    return UUID.fromString(parts[i + 1]);
                }
            }
        } catch (IllegalArgumentException e) {
            log.error("[WebSocket] 잘못된 UUID 형식 - destination: {}", destination, e);
        } catch (Exception e) {
            log.error("[WebSocket] roomId 추출 실패 - destination: {}", destination, e);
        }
        return null;
    }
}