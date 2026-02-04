package com.junwoo.hamkke.common.websocket.event;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
public record AutoLeaveEvent(
        Long userId,
        Long roomId,
        String reason
) {
    public AutoLeaveEvent(Long userId, Long roomId) {
        this(userId, roomId, "WebSocket 재연결 타임아웃");
    }
}