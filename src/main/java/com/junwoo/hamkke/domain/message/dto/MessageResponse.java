package com.junwoo.hamkke.domain.message.dto;

import com.junwoo.hamkke.domain.message.entity.MessageEntity;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Builder
public record MessageResponse(
        Long messageId,
        Long roomId,
        Long senderId,
        String senderNickname,
        String senderProfileUrl,
        String content,
        LocalDateTime createdAt
) {
    public static MessageResponse from(MessageEntity message, UserEntity sender) {
        return MessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .senderNickname(sender.getNickname())
                .senderProfileUrl(sender.getProfileUrl())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
