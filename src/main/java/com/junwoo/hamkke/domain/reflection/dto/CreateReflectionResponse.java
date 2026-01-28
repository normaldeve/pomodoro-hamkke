package com.junwoo.hamkke.domain.reflection.dto;

import com.junwoo.hamkke.domain.reflection.entity.ReflectionEntity;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Builder
public record CreateReflectionResponse(
        Long reflectionId,
        Long roomId,
        Long sessionId,
        Long userId,
        String content,
        Integer focusScore,
        String imageUrl,
        LocalDateTime createdAt
) {

    public static CreateReflectionResponse from(ReflectionEntity entity) {
        return CreateReflectionResponse.builder()
                .reflectionId(entity.getId())
                .roomId(entity.getStudyRoomId())
                .sessionId(entity.getSessionId())
                .userId(entity.getUserId())
                .content(entity.getContent())
                .focusScore(entity.getFocusScore())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
