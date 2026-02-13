package com.junwoo.hamkke.domain.room_member.listener;

import com.junwoo.hamkke.common.discord.DiscordNotifier;
import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeFinishedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeStartedEvent;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.room_member.service.FocusTimeService;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
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
    private final FocusTimeService focusTimeService;
    private final DiscordNotifier discordNotifier;

    @Async(value = "domainEventExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onFocusPhaseEnd(FocusTimeFinishedEvent event) {

        log.info("[FocusTimeTracking] FOCUS 정상 종료 - roomId: {}", event.roomId());

        List<StudyRoomMemberEntity> members = memberRepository.findAllByStudyRoomId(event.roomId());

        if (members.isEmpty()) {
            log.warn("[FocusTimeTrackingListener] onFocusPhaseEnd() : 방에 멤버가 없습니다 - roomId: {}", event.roomId());
            return;
        }

        int fullFocusSeconds = event.focusTime() * 60;

        for (StudyRoomMemberEntity member : members) {
            if (!Objects.equals(member.getCurrentSessionId(), event.currentSessionId())) {
                continue;
            }

            int joinElapsedSeconds = member.getFocusJoinElapsedSeconds() == null ? 0 : member.getFocusJoinElapsedSeconds();
            int earnedSeconds = Math.max(0, fullFocusSeconds - joinElapsedSeconds);

            focusTimeService.addFocusSeconds(member.getUserId(), event.roomId(), earnedSeconds);
            member.clearParticipation();
        }

        log.info("[FocusTimeTrackingListener] onFocusPhaseEnd() : 방에 있는 사용자 집중 시간 누적 완료 - roomId: {}, focusTime: {}",
                event.roomId(), event.focusTime());
    }

    @Async(value = "domainEventExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            value = {
                    OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    SocketTimeoutException.class,
                    ConnectException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void onFocusPhaseStart(FocusTimeStartedEvent event) {

        log.info("[FocusTimeTracking] onFocusPhaseStart() : 집중 시간이 시작, 참여자 현재 세션 참여 상태로 변경 - currentSessionId: {}",
                event.currentSessionId());

        List<StudyRoomMemberEntity> members = memberRepository.findAllByStudyRoomId(event.roomId());

        for (StudyRoomMemberEntity member : members) {
            member.markParticipating(event.currentSessionId(), 0);
        }
    }

    @Recover
    public void recover(
            Exception e,
            FocusTimeStartedEvent event
    ) {
        discordNotifier.sendError(
                "RoomStatusEventListener 재시도 실패",
                """
                roomId: %s
                exception: %s
                """.formatted(
                        event.roomId(),
                        e.getClass().getSimpleName()
                )
        );
        log.error("[RoomStatusEventListener] 재시도 실패 - roomId={}, phase={}", event.roomId(), e);
    }
}
