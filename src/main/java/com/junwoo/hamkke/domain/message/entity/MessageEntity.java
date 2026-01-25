package com.junwoo.hamkke.domain.message.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 25.
 */
@Getter
@Builder
@Entity
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MessageEntity extends BaseEntity {

    private Long roomId;

    private Long senderId;

    private String content;

    public static MessageEntity createMessage(Long roomId, Long senderId, String content) {
        return MessageEntity.builder()
                .roomId(roomId)
                .senderId(senderId)
                .content(content)
                .build();

    }
}
