package com.junwoo.hamkke.domain.stat.listener;

import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeFinishedEvent;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.stat.entity.UserDailyStudyStat;
import com.junwoo.hamkke.domain.stat.repository.UserDailyStudyStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDailyStatListener {

    private final StudyRoomMemberRepository memberRepository;
    private final UserDailyStudyStatRepository dailyStatRepository;

    @EventListener
    @Transactional
    public void onFocusPhaseEnd(FocusTimeFinishedEvent event) {

        LocalDate today = LocalDate.now();

        List<StudyRoomMemberEntity> members = memberRepository.findAllByStudyRoomId(event.roomId());

        for (StudyRoomMemberEntity member : members) {

            if (!Objects.equals(member.getCurrentSessionId(), event.currentSessionId())) {
                continue;
            }

            UserDailyStudyStat stat =
                    dailyStatRepository.findByUserIdAndStudyDate(member.getUserId(), today)
                            .orElseGet(() -> UserDailyStudyStat.create(member.getUserId(), today));

            stat.addMinutes(event.focusTime());
            dailyStatRepository.save(stat);
        }

        log.info(
                "[UserDailyStatListener] 일별 집계 완료 - roomId: {}, minutes: {}",
                event.roomId(), event.focusTime()
        );
    }
}
