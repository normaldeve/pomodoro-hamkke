package com.junwoo.hamkke.domain.reflection.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private UUID studyRoomId;

    @Column(nullable = false)
    private Long userId;

    private String imageUrl;

    @Column(nullable = false)
    private Long sessionId;

    @Column(length = 1000)
    private String content;

    private Integer focusScore;

    @Column(nullable = false)
    private boolean isPrivate;

    public static ReflectionEntity createReflection(UUID studyRoomId, Long userId, Long sessionId, String imageUrl, String content, Integer focusScore, boolean isPrivate) {
        return ReflectionEntity.builder()
                .studyRoomId(studyRoomId)
                .userId(userId)
                .sessionId(sessionId)
                .imageUrl(imageUrl)
                .content(content)
                .focusScore(focusScore)
                .isPrivate(isPrivate)
                .build();
    }
}
