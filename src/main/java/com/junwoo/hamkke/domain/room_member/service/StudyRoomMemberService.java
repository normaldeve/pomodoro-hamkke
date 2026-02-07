package com.junwoo.hamkke.domain.room_member.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.room_member.dto.*;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room_member.dto.event.HostTransferredEvent;
import com.junwoo.hamkke.domain.room_member.dto.event.MemberLeftRoomEvent;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoomMemberService {

    private final UserRepository userRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMemberRepository studyRoomMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

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

    // StudyRoomMemberService.java 일부 수정
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

        // [TODO] 사용자 수 동시성 문제를 위해 비관적 락 적용 테스트 코드 필요
        StudyRoomEntity room = studyRoomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        // 상시 운영 방이 아닐 때만 비밀번호 확인
        if (!room.isPermanent() && room.isSecret()) {
            if (request == null || request.password() == null ||
                    !request.password().equals(room.getPassword())) {
                throw new StudyRoomException(ErrorCode.SECRET_ROOM_PASSWORD_INVALID);
            }
        }

        if (room.getCurrentParticipants() >= room.getMaxParticipants()) {
            throw new StudyRoomException(ErrorCode.ROOM_CAPACITY_EXCEEDED);
        }

        // 상시 운영 방은 모두 MEMBER로 등록 (HOST 없음)
        StudyRoomMemberEntity member = StudyRoomMemberEntity.registerMember(roomId, userId);

        studyRoomMemberRepository.save(member);
        room.addCurrentParticipant();

        return ParticipantMemberInfo.from(user);
    }

    // 방에 사용자가 남아 있다면 방장 권한을 넘겨주어야 한다.
    public void leaveRoom(Long roomId, Long userId) {

        StudyRoomEntity room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        StudyRoomMemberEntity leavingMember = studyRoomMemberRepository.findByStudyRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_USER));

        boolean isHost = leavingMember.isHost();

        studyRoomMemberRepository.deleteByStudyRoomIdAndUserId(roomId, userId);
        room.removeCurrentParticipant();

        long remainingMembers = studyRoomMemberRepository.countByStudyRoomId(roomId);

        log.info("[StudyRoomMemberService] 사용자가 방을 나갔습니다 - roomId: {}, userId: {}, isHost: {}, remainingMembers: {}",
                roomId, userId, isHost, remainingMembers);

        eventPublisher.publishEvent(new MemberLeftRoomEvent(roomId, userId, isHost, remainingMembers));
    }

    public void transferHost(Long roomId, Long currentHostId, TransferHostRequests request) {

        if (currentHostId.equals(request.targetUserId())) {
            throw new StudyRoomException(ErrorCode.CANNOT_TRANSFER_HOST_TO_SELF);
        }

        StudyRoomEntity room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        StudyRoomMemberEntity currentHost = studyRoomMemberRepository.findByStudyRoomIdAndUserId(roomId, currentHostId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_USER));

        if (!currentHost.isHost()) {
            throw new StudyRoomException(ErrorCode.ONLY_HOST_CAN_TRANSFER);
        }

        StudyRoomMemberEntity targetMember = studyRoomMemberRepository.findByStudyRoomIdAndUserId(roomId, request.targetUserId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_USER));

        currentHost.demoteToMember();
        targetMember.promoteToHost();
        room.transferHost(request.targetUserId());

        log.info("[StudyRoomMemberService] 방장 권한 수동 위임 - roomId: {}, from: {}, to: {}",
                roomId, currentHostId, request.targetUserId());

        eventPublisher.publishEvent(new HostTransferredEvent(roomId, currentHostId, request.targetUserId(), false));
    }

    // 가장 먼저 들어온 멤버에게 방장 권한을 자동 위임
    public void transferHostToOldestMember(Long roomId) {
        StudyRoomEntity room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        StudyRoomMemberEntity oldestMember = studyRoomMemberRepository.findOldestMember(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_TRANSFER_HOST_TO_SELF));

        Long previousHostId = room.getHostId();

        oldestMember.promoteToHost();
        room.transferHost(oldestMember.getUserId());

        log.info("[StudyRoomMemberService] 방장 권한 자동 위임 - roomId: {}, newHostId: {}",
                roomId, oldestMember.getUserId());

        eventPublisher.publishEvent(new HostTransferredEvent(roomId, previousHostId, oldestMember.getUserId(), true));
    }

    @Transactional(readOnly = true)
    public Optional<ParticipateRoomInfo> getParticipateRoomInfo(Long userId) {

        log.info("[StudyRoomMemberService] getCurrentRoom() : 사용자가 참여 중인 방 조회 - userId: {}", userId);

        // 사용자가 참여 중인 방 조회
        Optional<StudyRoomMemberEntity> memberOpt = studyRoomMemberRepository.findByUserId(userId);

        if (memberOpt.isEmpty()) {
            log.info("[StudyRoomMemberService] getCurrentRoom() : 참여 중인 방 없음 - userId: {}", userId);
            return Optional.empty();
        }

        StudyRoomMemberEntity member = memberOpt.get();

        // 방 정보 조회
        StudyRoomEntity room = studyRoomRepository.findById(member.getStudyRoomId())
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        ParticipateRoomInfo response = ParticipateRoomInfo.builder()
                .lastRoomId(room.getId())
                .role(member.getRole())
                .lastRoomJointAt(member.getCreatedAt())
                .build();

        log.info("[StudyRoomMemberService] getCurrentRoom() : 현재 참여 중인 방 정보 반환 - userId: {}, roomId: {}, role: {}",
                userId, room.getId(), member.getRole());

        return Optional.of(response);
    }
}