package com.junwoo.hamkke.domain.room.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.room.dto.CreateStudyRoomRequest;
import com.junwoo.hamkke.domain.room.dto.StudyRoomResponse;
import com.junwoo.hamkke.domain.room.entity.RoomStatus;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room_member.entity.StudyRoomMemberEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room_member.repository.StudyRoomMemberRepository;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
// StudyRoomService.java 수정
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoomService {

    private final static int PAGE_SIZE = 4;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMemberRepository studyRoomMemberRepository;

    public StudyRoomResponse createRoom(Long hostId, CreateStudyRoomRequest request) {

        StudyRoomEntity room = StudyRoomEntity.createRoom(hostId, request);

        StudyRoomEntity savedRoom = studyRoomRepository.save(room);

        StudyRoomMemberEntity member = StudyRoomMemberEntity.registerHost(savedRoom.getId(), hostId);

        studyRoomMemberRepository.save(member);

        savedRoom.addCurrentParticipant();

        return StudyRoomResponse.from(savedRoom);
    }

    @Transactional(readOnly = true)
    public Page<StudyRoomResponse> getStudyRooms(int page) {

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by("createdAt").descending()
        );

        // 일반 방만 조회 (상시 운영 방 제외)
        return studyRoomRepository
                .findByStatusNotAndPermanentFalse(RoomStatus.FINISHED, pageable)
                .map(StudyRoomResponse::from);
    }

    @Transactional(readOnly = true)
    public List<StudyRoomResponse> getPermanentRooms() {

        // 상시 운영 방만 조회
        return studyRoomRepository.findByPermanentTrue().stream()
                .map(StudyRoomResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudyRoomResponse getStudyRoom(Long roomId) {
        return StudyRoomResponse.from(
                studyRoomRepository.findById(roomId)
                        .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM))
        );
    }
}
