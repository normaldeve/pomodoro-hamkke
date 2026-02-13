package com.junwoo.hamkke.domain.room_member.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Getter
@Builder
@Entity
@Table(
        name = "study_room_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"study_room_id", "user_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudyRoomMemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private UUID studyRoomId;

    private Long userId;

    private int currentSessionId;

    private Integer focusJoinElapsedSeconds;

    @Enumerated(EnumType.STRING)
    private RoomMemberRole role;

    public static StudyRoomMemberEntity registerHost(UUID studyRoomId, Long userId) {
        return StudyRoomMemberEntity.builder()
                .studyRoomId(studyRoomId)
                .userId(userId)
                .currentSessionId(0)
                .focusJoinElapsedSeconds(null)
                .role(RoomMemberRole.HOST)
                .build();
    }

    public static StudyRoomMemberEntity registerMember(UUID studyRoomId, Long userId) {
        return StudyRoomMemberEntity.builder()
                .studyRoomId(studyRoomId)
                .userId(userId)
                .currentSessionId(0)
                .focusJoinElapsedSeconds(null)
                .role(RoomMemberRole.MEMBER)
                .build();
    }

    public void markParticipating(int currentSessionId, int focusJoinElapsedSeconds) {
        this.currentSessionId = currentSessionId;
        this.focusJoinElapsedSeconds = Math.max(0, focusJoinElapsedSeconds);
    }

    public void clearParticipation() {
        this.currentSessionId = 0;
        this.focusJoinElapsedSeconds = null;
    }

    public void promoteToHost() {
        this.role = RoomMemberRole.HOST;
    }

    public void demoteToMember() {
        this.role = RoomMemberRole.MEMBER;
    }

    public boolean isHost() {
        return this.role == RoomMemberRole.HOST;
    }
}
