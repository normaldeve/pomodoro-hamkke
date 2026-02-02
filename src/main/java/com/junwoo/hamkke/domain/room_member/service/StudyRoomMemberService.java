package com.junwoo.hamkke.domain.room_member.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.room_member.dto.EnterStudyRoomRequest;
import com.junwoo.hamkke.domain.room_member.dto.ParticipantMemberInfo;
import com.junwoo.hamkke.domain.room_member.dto.StudyRoomMemberResponse;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoomMemberService {

    private final UserRepository userRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMemberRepository studyRoomMemberRepository;

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

    public ParticipantMemberInfo enterRoom(Long roomId, Long userId, EnterStudyRoomRequest request) {

        // 이미 입장한 멤버인지 확인
        if (studyRoomMemberRepository.existsByStudyRoomIdAndUserId(roomId, userId)) {
            throw new StudyRoomException(ErrorCode.ALREADY_IN_ROOM);
        }

        // 다른 방에 들어가 있는 사용자인지 확인
        if (studyRoomMemberRepository.existsByUserId(userId)) {
            log.error("[StudyRoomMemberService] 이미 다른 스터디에 들어가 있는 사용자입니다!!!");
            throw new StudyRoomException(ErrorCode.ALREADY_IN_ANOTHER_ROOM);
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_USER));

        StudyRoomEntity room = studyRoomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        if (room.isSecret()) {
            if (request == null || request.password() == null ||
                    !request.password().equals(room.getPassword())) {
                throw new StudyRoomException(ErrorCode.SECRET_ROOM_PASSWORD_INVALID);
            }
        }

        if (room.getCurrentParticipants() >= room.getMaxParticipants()) {
            throw new StudyRoomException(ErrorCode.ROOM_CAPACITY_EXCEEDED);
        }

        StudyRoomMemberEntity member =
                StudyRoomMemberEntity.registerMember(roomId, userId);

        studyRoomMemberRepository.save(member);
        room.addCurrentParticipant();

        return ParticipantMemberInfo.from(user);
    }

    // [TODO] 방에 남은 사용자가 없다면 방이 삭제되어야 한다.
    // 방에 사용자가 남아 있다면 방장 권한을 넘겨주어야 한다.
    public void leaveRoom(Long roomId, Long userId) {

        StudyRoomEntity room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        room.removeCurrentParticipant();

        studyRoomMemberRepository.deleteByStudyRoomIdAndUserId(roomId, userId);
    }
}