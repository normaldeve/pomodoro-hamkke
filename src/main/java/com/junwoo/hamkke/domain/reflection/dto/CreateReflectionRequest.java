package com.junwoo.hamkke.domain.reflection.dto;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
public record CreateReflectionRequest(
        Long sessionId,
        String content,
        Integer focusScore,
        String imageUrl,
        Boolean isPrivate
        ) {
}
