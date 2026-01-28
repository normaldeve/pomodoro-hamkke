package com.junwoo.hamkke.domain.room_member.listener;

import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeFinishedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeStartedEvent;
import com.junwoo.hamkke.domain.room_member.entity.DailyFocusTimeEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.DailyFocusTimeRepository;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class FocusTimeTrackingListener {

    private final StudyRoomMemberRepository memberRepository;
    private final DailyFocusTimeRepository dailyFocusTimeRepository;

    @EventListener
    @Transactional
    public void onFocusPhaseEnd(FocusTimeFinishedEvent event) {

        log.info("[FocusTimeTracking] FOCUS 정상 종료 - roomId: {}", event.roomId());

        LocalDate today = LocalDate.now();

        List< StudyRoomMemberEntity> members = memberRepository.findAllByStudyRoomId(event.roomId());

        if (members.isEmpty()) {
            log.warn("[FocusTimeTrackingListener] onFocusPhaseEnd() : 방에 멤버가 없습니다 - roomId: {}", event.roomId());
            return;
        }

        for (StudyRoomMemberEntity member : members) {
            log.info("[FocusTimeTrackingListener] onFocusPhaseEnd() : 참여자 집중 시간 설정 - member {}", member.getId());
            // 현재 세션 중간에 참여했을 경우는 저장하지 않음
            if (!Objects.equals(member.getCurrentSessionId(), event.currentSessionId())) {
                continue;
            }
            DailyFocusTimeEntity dailyFocusTime = dailyFocusTimeRepository.findByUserIdAndFocusDate(member.getUserId(), today)
                    .orElseGet(() -> DailyFocusTimeEntity.builder()
                            .userId(member.getUserId())
                            .focusDate(today)
                            .totalFocusMinutes(0)
                            .build());
            dailyFocusTime.addMinutes(event.focusTime());
            dailyFocusTimeRepository.save(dailyFocusTime);
        }

        log.info("[FocusTimeTrackingListener] onFocusPhaseEnd() : 방에 있는 사용자 집중 시간 누적 완료 - roomId: {}, focusTime: {}", event.roomId(), event.focusTime());

    }

    @EventListener
    @Transactional
    public void onFocusPhaseStart(FocusTimeStartedEvent event) {

        log.info("[FocusTimeTracking] onFocusPhaseStart() : 집중 시간이 시작, 참여자 현재 세션 참여 상태로 변경 - currentSessionId: {}", event.currentSessionId());

        List<StudyRoomMemberEntity> members = memberRepository.findAllByStudyRoomId(event.roomId());

        for (StudyRoomMemberEntity member : members) {
            member.markParticipating(event.currentSessionId());
        }
    }
}
