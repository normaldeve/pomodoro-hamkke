package com.junwoo.hamkke.domain.reflection.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Builder
public record ReflectionResponse(
        Long reflectionId,
        Long sessionId,
        Long userId,
        String content,
        String username,
        String userProfileUrl,
        Integer focusScore,
        String imageUrl,
        LocalDateTime createdAt
) {

}
