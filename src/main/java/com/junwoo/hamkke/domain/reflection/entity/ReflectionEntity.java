package com.junwoo.hamkke.domain.reflection.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Getter
@Entity
@Builder
@Table(
        name = "session_reflection",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"study_room_id", "user_id", "session_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReflectionEntity extends BaseEntity {

    @Column(nullable = false)
    private Long studyRoomId;

    @Column(nullable = false)
    private Long userId;

    private String imageUrl;

    @Column(nullable = false)
    private Long sessionId;

    @Column(length = 1000)
    private String content;

    private Integer focusScore;

    public static ReflectionEntity createReflection(Long studyRoomId, Long userId, Long sessionId, String imageUrl, String content, Integer focusScore) {
        return ReflectionEntity.builder()
                .studyRoomId(studyRoomId)
                .userId(userId)
                .sessionId(sessionId)
                .imageUrl(imageUrl)
                .content(content)
                .focusScore(focusScore)
                .build();
    }
}
