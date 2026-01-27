package com.junwoo.hamkke.domain.room_member.service;

import com.junwoo.hamkke.domain.room_member.dto.MemberFocusRuntime;
import com.junwoo.hamkke.domain.room_member.entity.FocusStatType;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomFocusStatEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomFocusStatRepository;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [TODO] 방 참여 시 공부 기록 측정하는 부분이 이해가 안 되네
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberFocusRuntimeService {

    private final Map<Long, Map<Long, MemberFocusRuntime>> runtimes = new ConcurrentHashMap<>();
    private final StudyRoomFocusStatRepository focusStatRepository;
    private final StudyRoomMemberRepository memberRepository;

    public void onEnterRoom(Long roomId, Long userId, boolean isFocus) {
        log.info("[집중시간] 입장 - roomId: {}, userId: {}, isFocus: {}", roomId, userId, isFocus);

        runtimes
                .computeIfAbsent(roomId, r -> new ConcurrentHashMap<>())
                .putIfAbsent(userId, new MemberFocusRuntime());

        if (isFocus) {
            MemberFocusRuntime runtime = runtimes.get(roomId).get(userId);
            runtime.startFocus();
            log.info("[집중시간] 집중 시작 (중간입장) - userId: {}, startedAt: {}",
                    userId, runtime.getFocusStartedAt());
        }
    }

    @Transactional(readOnly = true)
    public void startFocus(Long roomId) {
        log.info("[집중시간] 집중 시작 - roomId: {}", roomId);

        List<StudyRoomMemberEntity> members = memberRepository.findByStudyRoomId(roomId);

        Map<Long, MemberFocusRuntime> roomMap = runtimes.computeIfAbsent(roomId, r -> new ConcurrentHashMap<>());

        log.info("[집중시간] 방 멤버 수: {}", members.size());

        for (StudyRoomMemberEntity member : members) {
            roomMap
                    .computeIfAbsent(member.getUserId(), id -> new MemberFocusRuntime())
                    .startFocus();
        }
    }

    public void saveSessionFocusTime(Long roomId, int sessionNumber) {
        List<StudyRoomMemberEntity> members = memberRepository.findByStudyRoomId(roomId);

        Map<Long, MemberFocusRuntime> roomMap = runtimes.getOrDefault(roomId, Map.of());

        for (StudyRoomMemberEntity member : members) {
            MemberFocusRuntime runtime = roomMap.get(member.getUserId());
            if (runtime != null && runtime.isFocusing()) {
                runtime.stopFocus();
                int focusedSeconds = runtime.getTotalFocusedSeconds();

                if (focusedSeconds > 0) {
                    StudyRoomFocusStatEntity stat = focusStatRepository.findByRoomIdAndUserIdAndSessionNumber(roomId, member.getUserId(), sessionNumber)
                            .orElseGet(() -> {
                                return StudyRoomFocusStatEntity.builder()
                                        .roomId(roomId)
                                        .userId(member.getUserId())
                                        .sessionNumber(sessionNumber)
                                        .focusSeconds(0)
                                        .type(FocusStatType.SESSION_COMPLETE)
                                        .build();
                            });

                    stat.addFocusSeconds(focusedSeconds);
                    stat.markAsComplete();

                    focusStatRepository.save(stat);
                }

                runtime.resetForNextSession();
            }
        }
    }

    @Transactional(readOnly = true)
    public void stopFocus(Long roomId) {
        List<StudyRoomMemberEntity> members = memberRepository.findByStudyRoomId(roomId);

        Map<Long, MemberFocusRuntime> roomMap = runtimes.getOrDefault(roomId, Map.of());

        for (StudyRoomMemberEntity member : members) {
            MemberFocusRuntime runtime = roomMap.get(member.getUserId());
            if (runtime != null) {
                runtime.stopFocus();
            }
        }
    }

    public void onMemberLeave(Long roomId, Long userId, int currentSession) {
        log.info("[집중시간] 사용자 퇴장 시작 - roomId: {}, userId: {}, session: {}", roomId, userId, currentSession);
        Map<Long, MemberFocusRuntime> roomMap = runtimes.get(roomId);

        if (roomMap == null) {
            log.warn("[집중시간] roomMap이 null입니다 - roomId: {}", roomId);
            return;
        }

        MemberFocusRuntime runtime = roomMap.remove(userId);

        if (runtime == null) {
            log.warn("[집중시간] runtime이 null입니다 - roomId: {}, userId: {}", roomId, userId);
            return;
        }

        log.info("[집중시간] runtime 상태 - focusing: {}, startedAt: {}, totalSeconds: {}",
                runtime.isFocusing(), runtime.getFocusStartedAt(), runtime.getTotalFocusedSeconds());

        int currentFocusSeconds = runtime.finishAndGetTotalSeconds();

        log.info("[집중시간] 최종 계산된 시간: {}초", currentFocusSeconds);

        if (currentFocusSeconds > 0) {
            StudyRoomFocusStatEntity stat = focusStatRepository
                    .findByRoomIdAndUserIdAndSessionNumber(roomId, userId, currentSession)
                    .orElseGet(() -> {
                        log.info("[집중시간] 새 레코드 생성 (퇴장) - userId: {}, session: {}",
                                userId, currentSession);
                        return StudyRoomFocusStatEntity.builder()
                                .roomId(roomId)
                                .userId(userId)
                                .sessionNumber(currentSession)
                                .focusSeconds(0)
                                .type(FocusStatType.EARLY_EXIT)
                                .build();
                    });

            stat.addFocusSeconds(currentFocusSeconds);

            focusStatRepository.save(stat);
            log.info("[집중시간] DB 저장 완료 - roomId: {}, userId: {}, seconds: {}",
                    roomId, userId, currentFocusSeconds);
        } else {
            log.warn("[집중시간] 집중 시간이 0초라 저장하지 않음 - roomId: {}, userId: {}",
                    roomId, userId);
        }

        if (roomMap.isEmpty()) {
            runtimes.remove(roomId);
            log.info("[집중시간] 방 메모리 정리 완료 - roomId: {}", roomId);
        }
    }
}
