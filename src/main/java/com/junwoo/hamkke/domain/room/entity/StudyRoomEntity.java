package com.junwoo.hamkke.domain.room.entity;

import com.junwoo.hamkke.common.entity.UpdatableBaseEntity;
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
@Table(name = "study_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudyRoomEntity extends UpdatableBaseEntity {

    private String title;

    private int focusMinutes;
    private int breakMinutes;
    private int totalSessions;

    private int maxParticipants;

    private boolean secret;
    private String password;

    private Long hostUserId;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;
}
