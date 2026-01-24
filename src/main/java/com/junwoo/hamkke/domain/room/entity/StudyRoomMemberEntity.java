package com.junwoo.hamkke.domain.room.entity;

import com.junwoo.hamkke.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Getter
@Builder
@Entity
@Table(name = "study_room_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudyRoomMemberEntity extends BaseEntity {

    private Long studyRoomId;

    private Long userId;

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
