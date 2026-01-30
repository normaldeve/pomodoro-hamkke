package com.junwoo.hamkke.domain.room.dto;

import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.entity.TimerType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Builder
public record StudyRoomResponse(
        Long roomId,
        String title,
        Set<String> hashtags,
        int focusMinutes,
        int breakMinutes,
        int currentSession,
        int totalSessions,
        int currentParticipants,
        int maxParticipants,
        boolean secret,
        Long hostId,
        RoomStatus status,
        TimerType timerType,
        LocalDateTime createdAt
) {

    public static StudyRoomResponse from(StudyRoomEntity room) {
        return StudyRoomResponse.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .hashtags(room.getHashtags())
                .currentSession(room.getCurrentSession())
                .focusMinutes(room.getFocusMinutes())
                .breakMinutes(room.getBreakMinutes())
                .totalSessions(room.getTotalSessions())
                .currentParticipants(room.getCurrentParticipants())
                .maxParticipants(room.getMaxParticipants())
                .secret(room.isSecret())
                .hostId(room.getHostId())
                .status(room.getStatus())
                .timerType(room.getTimerType())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
