package com.junwoo.hamkke.domain.room.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.room.dto.EnterStudyRoomRequest;
import com.junwoo.hamkke.domain.room.dto.StudyRoomMemberResponse;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    /**
     * [TODO] 방 인원 Lock 적용 테스트 코드 확인할 것!!
     */
    public void enterRoom(Long roomId, Long userId, EnterStudyRoomRequest request) {

        // 이미 입장한 멤버인지 확인
        if (studyRoomMemberRepository.existsByStudyRoomIdAndUserId(roomId, userId)) {
            return;
        }

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
    }
}
