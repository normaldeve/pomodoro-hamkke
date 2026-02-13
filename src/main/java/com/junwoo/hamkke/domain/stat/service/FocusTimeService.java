package com.junwoo.hamkke.domain.stat.service;

import com.junwoo.hamkke.domain.dial.dto.TimerPhase;
import com.junwoo.hamkke.domain.dial.dto.TimerRuntimeSnapshot;
import com.junwoo.hamkke.domain.dial.service.TimerStateService;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.stat.entity.UserDailyStudyStat;
import com.junwoo.hamkke.domain.stat.repository.UserDailyStudyStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FocusTimeService {

    private final UserDailyStudyStatRepository dailyStatRepository;
    private final TimerStateService timerStateService;

    public void markParticipationForLateJoin(StudyRoomMemberEntity member) {
        TimerRuntimeSnapshot snapshot = timerStateService.getTimerSnapshot(member.getStudyRoomId());
        if (snapshot == null || snapshot.phase() != TimerPhase.FOCUS) {
            return;
        }

        member.markParticipating(snapshot.currentSessionId(), snapshot.elapsedSecondsInCurrentPhase());
    }

    @Transactional
    public void settleFocusTimeOnLeave(StudyRoomMemberEntity member) {
        if (member.getCurrentSessionId() <= 0) {
            return;
        }

        TimerRuntimeSnapshot snapshot = timerStateService.getTimerSnapshot(member.getStudyRoomId());
        if (snapshot == null) {
            return;
        }

        if (!Objects.equals(member.getCurrentSessionId(), snapshot.currentSessionId())) {
            return;
        }

        int joinElapsedSeconds = member.getFocusJoinElapsedSeconds() == null ? 0 : member.getFocusJoinElapsedSeconds();

        int earnedSeconds = switch (snapshot.phase()) {
            case FOCUS -> snapshot.elapsedSecondsInCurrentPhase() - joinElapsedSeconds;
            // FOCUS 종료 직후 BREAK에 퇴장하면 종료 이벤트보다 먼저 삭제될 수 있어 여기서 정산합니다.
            case BREAK -> snapshot.focusDurationSeconds() - joinElapsedSeconds;
            default -> 0;
        };

        addFocusSeconds(member.getUserId(), member.getStudyRoomId(), earnedSeconds);
    }

    @Transactional
    public void addFocusSeconds(Long userId, UUID roomId, int focusSeconds) {
        if (focusSeconds <= 0) {
            return;
        }

        int focusMinutes = focusSeconds / 60;
        if (focusMinutes == 0) {
            return;
        }

        LocalDate today = LocalDate.now();

        upsertDailyStudyStat(userId, today, focusMinutes);

        log.info("[FocusTimeService] 집중 시간 정산 완료 - userId: {}, roomId: {}, seconds: {}, minutes: {}",
                userId, roomId, focusSeconds, focusMinutes);
    }

    private void upsertDailyStudyStat(Long userId, LocalDate today, int focusMinutes) {
        int updated = dailyStatRepository.incrementMinutes(userId, today, focusMinutes);
        if (updated > 0) {
            return;
        }

        try {
            UserDailyStudyStat newStat = UserDailyStudyStat.create(userId, today);
            newStat.addMinutes(focusMinutes);
            dailyStatRepository.save(newStat);
        } catch (DataIntegrityViolationException e) {
            // 동시 삽입 경합 시 기존 행에 다시 누적
            dailyStatRepository.incrementMinutes(userId, today, focusMinutes);
        }
    }
}
