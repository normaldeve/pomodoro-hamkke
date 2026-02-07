package com.junwoo.hamkke.domain.room.entity;

import com.junwoo.hamkke.common.entity.UpdatableBaseEntity;
import com.junwoo.hamkke.domain.room.dto.CreateStudyRoomRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hashtags", columnDefinition = "JSON")
    private Set<String> hashtags;

    private int focusMinutes;
    private int breakMinutes;
    private int currentSession;
    private int totalSessions;

    @Min(value = 0, message = "현재 참여 인원은 0 이상이어야 합니다.")
    private int currentParticipants;
    private int maxParticipants;

    private boolean secret;
    private String password;

    @Enumerated(EnumType.STRING)
    private TimerType timerType;

    private Long hostId;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    // 상시 운영 방 여부
    private boolean permanent;

    public static StudyRoomEntity createRoom(Long hostId, CreateStudyRoomRequest request) {
        return StudyRoomEntity.builder()
                .title(request.title())
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
                .timerType(request.timerType())
                .permanent(false)
                .build();
    }

    public static StudyRoomEntity createPermanentRoom(
            String title,
            int focusMinutes,
            int breakMinutes,
            TimerType timerType,
            Set<String> hashtags
    ) {
        return StudyRoomEntity.builder()
                .title(title)
                .hashtags(hashtags)
                .focusMinutes(focusMinutes)
                .breakMinutes(breakMinutes)
                .currentSession(1)
                .totalSessions(Integer.MAX_VALUE) // 무한 반복
                .currentParticipants(0)
                .maxParticipants(100) // 충분히 큰 값
                .secret(false)
                .password(null)
                .hostId(0L) // 시스템 방
                .status(RoomStatus.WAITING)
                .timerType(timerType)
                .permanent(true)
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

    public void finishSession() {
        this.currentSession++;
    }

    public void transferHost(Long newHostId) {
        this.hostId = newHostId;
    }
}
