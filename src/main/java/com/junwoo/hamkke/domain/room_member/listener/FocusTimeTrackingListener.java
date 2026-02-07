package com.junwoo.hamkke.domain.room_member.listener;

import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeFinishedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeStartedEvent;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.room_member.entity.RoomFocusTimeEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.RoomFocusTimeRepository;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * [TODO] 아래와 같은 방식으로 사용자 필드 업데이트 시 문제가 발생하지는 않을까? -> 테스트 필요!!!!!
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
// FocusTimeTrackingListener.java 수정
@Slf4j
@Component
@RequiredArgsConstructor
public class FocusTimeTrackingListener {

    private final StudyRoomMemberRepository memberRepository;
    private final RoomFocusTimeRepository roomFocusTimeRepository;
    private final StudyRoomRepository studyRoomRepository;

    @EventListener
    @Transactional
    public void onFocusPhaseEnd(FocusTimeFinishedEvent event) {

        log.info("[FocusTimeTracking] FOCUS 정상 종료 - roomId: {}", event.roomId());

        LocalDate today = LocalDate.now();

        List<StudyRoomMemberEntity> members = memberRepository.findAllByStudyRoomId(event.roomId());

        if (members.isEmpty()) {
            log.warn("[FocusTimeTrackingListener] onFocusPhaseEnd() : 방에 멤버가 없습니다 - roomId: {}", event.roomId());
            return;
        }

        // 상시 운영 방 여부 확인
        boolean isPermanentRoom = studyRoomRepository.findById(event.roomId())
                .map(StudyRoomEntity::isPermanent)
                .orElse(false);

        for (StudyRoomMemberEntity member : members) {
            log.info("[FocusTimeTrackingListener] onFocusPhaseEnd() : 참여자 집중 시간 설정 - member {}", member.getId());

            // 상시 운영 방이 아닐 때만 세션 ID 체크
            if (!isPermanentRoom && !Objects.equals(member.getCurrentSessionId(), event.currentSessionId())) {
                continue;
            }

            RoomFocusTimeEntity roomFocusTime = roomFocusTimeRepository
                    .findByUserIdAndStudyRoomIdAndFocusDate(member.getUserId(), event.roomId(), today)
                    .orElseGet(() -> RoomFocusTimeEntity.builder()
                            .userId(member.getUserId())
                            .studyRoomId(event.roomId())
                            .focusDate(today)
                            .totalFocusMinutes(0)
                            .build());

            roomFocusTime.addMinutes(event.focusTime());
            roomFocusTimeRepository.save(roomFocusTime);
        }

        log.info("[FocusTimeTrackingListener] onFocusPhaseEnd() : 방에 있는 사용자 집중 시간 누적 완료 - roomId: {}, focusTime: {}",
                event.roomId(), event.focusTime());
    }

    @EventListener
    @Transactional
    public void onFocusPhaseStart(FocusTimeStartedEvent event) {

        log.info("[FocusTimeTracking] onFocusPhaseStart() : 집중 시간이 시작, 참여자 현재 세션 참여 상태로 변경 - currentSessionId: {}",
                event.currentSessionId());

        // 상시 운영 방 여부 확인
        boolean isPermanentRoom = studyRoomRepository.findById(event.roomId())
                .map(StudyRoomEntity::isPermanent)
                .orElse(false);

        // 상시 운영 방은 세션 ID 업데이트 불필요
        if (isPermanentRoom) {
            log.info("[FocusTimeTracking] 상시 운영 방은 세션 ID 업데이트 스킵 - roomId: {}", event.roomId());
            return;
        }

        List<StudyRoomMemberEntity> members = memberRepository.findAllByStudyRoomId(event.roomId());

        for (StudyRoomMemberEntity member : members) {
            member.markParticipating(event.currentSessionId());
        }
    }
}
