package com.junwoo.hamkke.domain.room.entity;

import com.junwoo.hamkke.common.entity.UpdatableBaseEntity;
import com.junwoo.hamkke.domain.room.dto.CreateStudyRoomRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Getter
@Builder
@Entity
@Table(name = "study_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudyRoomEntity extends UpdatableBaseEntity {

    private String title;
    private String description;
    private Set<String> hashtags;

    private int focusMinutes;
    private int breakMinutes;
    private int currentSession;
    private int totalSessions;

    private int currentParticipants;
    private int maxParticipants;

    private boolean secret;
    private String password;

    private Long hostId;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    public static StudyRoomEntity createRoom(Long hostId, CreateStudyRoomRequest request) {
        return StudyRoomEntity.builder()
                .title(request.title())
                .description(request.description())
                .hashtags(request.hashtags())
                .focusMinutes(0)
                .breakMinutes(request.breakMinutes())
                .currentSession(1)
                .totalSessions(request.totalSessions())
                .maxParticipants(request.maxParticipants())
                .secret(request.secret())
                .password(request.password())
                .hostId(hostId)
                .status(RoomStatus.WAITING)
                .build();
    }

    public void addCurrentParticipant() {
        this.currentParticipants++;
    }

    public void removeCurrentParticipant() {
        this.currentParticipants--;
    }

    public void changeStatus(RoomStatus status) {
        this.status = status;
    }

    public void changeFocusMinutes(int focusMinutes) {
        this.focusMinutes = focusMinutes;
    }

    public void handleTimerStartEvent(int focusMinutes) {
        this.focusMinutes = focusMinutes;
        this.status = RoomStatus.FOCUS;
    }

    public void finishSession() {
        this.currentSession++;
        this.status = RoomStatus.FOCUS;
    }
}
