package com.junwoo.hamkke.domain.room_member.service;

import com.junwoo.hamkke.domain.room_member.dto.MemberFocusRuntime;
import com.junwoo.hamkke.domain.room_member.entity.FocusStatType;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomFocusStatEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomFocusStatRepository;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
public class MemberFocusRuntimeService {

    private final Map<Long, Map<Long, MemberFocusRuntime>> runtimes = new ConcurrentHashMap<>();
    private final StudyRoomFocusStatRepository focusStatRepository;
    private final StudyRoomMemberRepository memberRepository;

    public void onEnterRoom(Long roomId, Long userId, boolean isFocus) {
        runtimes
                .computeIfAbsent(roomId, r -> new ConcurrentHashMap<>())
                .putIfAbsent(userId, new MemberFocusRuntime());

        if (isFocus) {
            runtimes.get(roomId).get(userId).startFocus();
        }
    }

    @Transactional(readOnly = true)
    public void startFocus(Long roomId) {

        List<StudyRoomMemberEntity> members = memberRepository.findByStudyRoomId(roomId);

        Map<Long, MemberFocusRuntime> roomMap = runtimes.computeIfAbsent(roomId, r -> new ConcurrentHashMap<>());

        for (StudyRoomMemberEntity member : members) {
            roomMap
                    .computeIfAbsent(member.getUserId(), id -> new MemberFocusRuntime())
                    .startFocus();
        }
    }

    @Transactional
    public void saveSessionFocusTime(Long roomId, int sessionNumber) {
        List<StudyRoomMemberEntity> members = memberRepository.findByStudyRoomId(roomId);

        Map<Long, MemberFocusRuntime> roomMap = runtimes.getOrDefault(roomId, Map.of());

        for (StudyRoomMemberEntity member : members) {
            MemberFocusRuntime runtime = roomMap.get(member.getUserId());
            if (runtime != null && runtime.isFocusing()) {
                runtime.stopFocus();

                StudyRoomFocusStatEntity stat = StudyRoomFocusStatEntity.builder()
                        .roomId(roomId)
                        .userId(member.getUserId())
                        .sessionNumber(sessionNumber)
                        .focusSeconds(runtime.finishAndGetTotalSeconds())
                        .type(FocusStatType.SESSION_COMPLETE)
                        .build();

                focusStatRepository.save(stat);

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
        MemberFocusRuntime runtime = runtimes.getOrDefault(roomId, Map.of()).remove(userId);

        if (runtime != null) {
            int currentFocusSeconds = runtime.finishAndGetTotalSeconds();

            if (currentFocusSeconds > 0) {
                StudyRoomFocusStatEntity stat = StudyRoomFocusStatEntity.builder()
                        .roomId(roomId)
                        .userId(userId)
                        .sessionNumber(currentSession)
                        .focusSeconds(currentFocusSeconds)
                        .type(FocusStatType.EARLY_EXIT)
                        .build();

                focusStatRepository.save(stat);
            }

            runtimes.getOrDefault(roomId, Map.of()).remove(userId);
        }
    }
}
