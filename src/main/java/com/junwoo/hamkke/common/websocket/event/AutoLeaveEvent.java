package com.junwoo.hamkke.common.websocket.event;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
public record AutoLeaveEvent(
        Long userId,
        UUID roomId,
        String reason
) {
    public AutoLeaveEvent(Long userId, UUID roomId) {
        this(userId, roomId, "WebSocket 재연결 타임아웃");
    }
}