package com.junwoo.hamkke.domain.room_member.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private Long studyRoomId;

    private Long userId;

    private int focusSeconds;

    @Enumerated(EnumType.STRING)
    private RoomMemberRole role;

    public static StudyRoomMemberEntity registerHost(Long studyRoomId, Long userId) {
        return StudyRoomMemberEntity.builder()
                .studyRoomId(studyRoomId)
                .userId(userId)
                .role(RoomMemberRole.HOST)
                .build();
    }

    public static StudyRoomMemberEntity registerMember(Long studyRoomId, Long userId) {
        return StudyRoomMemberEntity.builder()
                .studyRoomId(studyRoomId)
                .userId(userId)
                .role(RoomMemberRole.MEMBER)
                .build();
    }
}
