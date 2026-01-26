package com.junwoo.hamkke.domain.room_member.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.dial.service.TimerStateService;
import com.junwoo.hamkke.domain.room_member.dto.EnterStudyRoomRequest;
import com.junwoo.hamkke.domain.room_member.dto.ParticipantMemberInfo;
import com.junwoo.hamkke.domain.room_member.dto.StudyRoomMemberResponse;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomFocusStatEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomFocusStatRepository;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoomMemberService {

    private final UserRepository userRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final TimerStateService timerStateService;
    private final MemberFocusRuntimeService runtimeService;
    private final StudyRoomMemberRepository studyRoomMemberRepository;
    private final StudyRoomFocusStatRepository focusStatRepository;

    @Transactional(readOnly = true)
    public List<StudyRoomMemberResponse> getStudyRoomMembers(Long roomId) {

        List<StudyRoomMemberEntity> members = studyRoomMemberRepository.findByStudyRoomIdOrderByRoleAscCreatedAtAsc(roomId);

        List<Long> userIds = members.stream().map(StudyRoomMemberEntity::getUserId).toList();

        Map<Long, UserEntity> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return members.stream()
                .map(member -> {
                            UserEntity user = userMap.get(member.getUserId());
                            return StudyRoomMemberResponse.from(member, user);
                        }
                ).toList();
    }

    public Optional<ParticipantMemberInfo> enterRoom(Long roomId, Long userId, EnterStudyRoomRequest request) {

        // 이미 입장한 멤버인지 확인
        if (studyRoomMemberRepository.existsByStudyRoomIdAndUserId(roomId, userId)) {
            return Optional.empty();
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_USER));

        // 비관적 락 적용
        StudyRoomEntity room = studyRoomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        if (room.isSecret()) {
            if (request == null || request.password() == null || !request.password().equals(room.getPassword())) {
                throw new StudyRoomException(ErrorCode.SECRET_ROOM_PASSWORD_INVALID);
            }
        }

        if (room.getCurrentParticipants() >= room.getMaxParticipants()) {
            throw new StudyRoomException(ErrorCode.ROOM_CAPACITY_EXCEEDED);
        }

        StudyRoomMemberEntity member = StudyRoomMemberEntity.registerMember(roomId, userId);

        studyRoomMemberRepository.save(member);

        room.addCurrentParticipant();

        return Optional.of(ParticipantMemberInfo.from(user));
    }

    public void leaveRoom(Long roomId, Long userId) {

        StudyRoomEntity room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        room.removeCurrentParticipant();

        runtimeService.onMemberLeave(roomId, userId, room.getCurrentSession());

        studyRoomMemberRepository.deleteByStudyRoomIdAndUserId(roomId, userId);
    }
}