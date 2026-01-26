package com.junwoo.hamkke.domain.room_member.service;

import com.junwoo.hamkke.domain.room_member.dto.MemberFocusRuntime;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
@Service
@RequiredArgsConstructor
public class MemberFocusRuntimeService {

    private final Map<Long, Map<Long, MemberFocusRuntime>> runtimes = new ConcurrentHashMap<>();
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

    public int onMemberLeave(Long roomId, Long userId) {
        MemberFocusRuntime runtime = runtimes.getOrDefault(roomId, Map.of()).remove(userId);

        return runtime == null ? 0 : runtime.finishAndGetTotalSeconds();
    }
}
