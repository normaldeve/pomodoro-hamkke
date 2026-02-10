package com.junwoo.hamkke.domain.room_member.listener;

import com.junwoo.hamkke.common.discord.DiscordNotifier;
import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeFinishedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.FocusTimeStartedEvent;
import com.junwoo.hamkke.domain.dial.dto.event.TimerPhaseChangeEvent;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.room_member.entity.RoomFocusTimeEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.RoomFocusTimeRepository;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
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
import java.time.LocalDate;
import java.util.List;

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
    private final DiscordNotifier discordNotifier;

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

        for (StudyRoomMemberEntity member : members) {
            log.info("[FocusTimeTrackingListener] onFocusPhaseEnd() : 참여자 집중 시간 설정 - member {}", member.getId());

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
            member.markParticipating(event.currentSessionId());
        }
    }

    @Recover
    public void recover(
            Exception e,
            TimerPhaseChangeEvent event
    ) {
        discordNotifier.sendError(
                "RoomStatusEventListener 재시도 실패",
                """
                roomId: %s
                phase: %s
                exception: %s
                """.formatted(
                        event.roomId(),
                        event.phase(),
                        e.getClass().getSimpleName()
                )
        );
        log.error("[RoomStatusEventListener] 재시도 실패 - roomId={}, phase={}", event.roomId(), event.phase(), e);
    }
}
