package com.junwoo.hamkke.domain.reflection.dto;

import com.junwoo.hamkke.domain.user.dto.UserInfo;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 8.
 */
@Builder
public record ReflectionQueryResponse(
        Long reflectionId,
        Long sessionId,
        String content,
        Integer focusScore,
        String imageUrl,
        Boolean isPrivate,
        LocalDateTime createdAt,
        UserInfo user,
        RoomInfo room
) {
    @Builder
    public record RoomInfo(UUID roomId, String roomName) { }
}
