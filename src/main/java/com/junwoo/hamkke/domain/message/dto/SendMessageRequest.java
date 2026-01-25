package com.junwoo.hamkke.domain.message.dto;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
public record SendMessageRequest(
        Long senderId,
        String content
) {
}
