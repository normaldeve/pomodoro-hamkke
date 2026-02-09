package com.junwoo.hamkke.support;

import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.entity.TimerType;

/**
 * 테스트를 위한 스터디 룸 엔티티 생성 클래스
 * @author junnukim1007gmail.com
 * @date 26. 2. 9.
 */
public class StudyRoomFixture {

    // 사용자 ID, 방 제목만으로 방 생성
    public static StudyRoomEntity create(String title, Long hostId) {
        return StudyRoomEntity.builder()
                .title(title)
                .focusMinutes(0)
                .breakMinutes(10)
                .currentSession(1)
                .totalSessions(10)
                .maxParticipants(3)
                .secret(false)
                .hostId(hostId)
                .status(RoomStatus.WAITING)
                .timerType(TimerType.POMODORO)
                .permanent(false)
                .build();
    }
}
