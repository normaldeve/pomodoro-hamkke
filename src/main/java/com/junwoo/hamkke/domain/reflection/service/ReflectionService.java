package com.junwoo.hamkke.domain.reflection.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.reflection.dto.CreateReflectionRequest;
import com.junwoo.hamkke.domain.reflection.dto.ReflectionQueryResponse;
import com.junwoo.hamkke.domain.reflection.dto.ReflectionResponse;
import com.junwoo.hamkke.domain.reflection.entity.ReflectionEntity;
import com.junwoo.hamkke.domain.reflection.repository.ReflectionRepository;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.user.dto.UserInfo;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.exception.UserException;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReflectionService {

    private final UserRepository userRepository;
    private final ReflectionRepository reflectionRepository;
    private final StudyRoomRepository studyRoomRepository;

    public ReflectionResponse createReflection(Long roomId, Long userId, CreateReflectionRequest request) {

        ReflectionEntity reflection = ReflectionEntity.createReflection(roomId, userId, request.sessionId(), request.imageUrl(), request.content(), request.focusScore());

        ReflectionEntity saved = reflectionRepository.save(reflection);

        UserEntity user = userRepository.findById((userId))
                .orElseThrow(() -> new UserException(ErrorCode.CANNOT_FOUND_USER));

        return ReflectionResponse.builder()
                .reflectionId(saved.getId())
                .sessionId(saved.getSessionId())
                .userId(saved.getUserId())
                .content(saved.getContent())
                .nickname(user.getNickname())
                .userProfileUrl(user.getProfileUrl())
                .focusScore(saved.getFocusScore())
                .imageUrl(saved.getImageUrl())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReflectionResponse> getRoomReflections(Long roomId) {

        return reflectionRepository.findRoomReflections(roomId);
    }

    /**
     * 회고 조회 (특정 날/특정 월/전체)
     * - date가 있으면 특정 날
     * - year, month가 있으면 특정 월
     * - 둘 다 없으면 전체
     */
    @Transactional(readOnly = true)
    public List<ReflectionQueryResponse> getReflections(Long userId, LocalDate date, Integer year, Integer month) {
        List<ReflectionEntity> reflections;

        // 특정 날짜 조회
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            reflections = reflectionRepository.findByUserIdAndDate(userId, startOfDay, endOfDay);
        }
        // 특정 월 조회
        else if (year != null && month != null) {
            reflections = reflectionRepository.findByUserIdAndYearMonth(userId, year, month);
        }
        // 전체 조회
        else {
            reflections = reflectionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return toQueryResponses(reflections);
    }

    /**
     * [TODO] private 메서드 처리에 대한 고민이 필요함
     * Entity List를 QueryResponse List로 변환
     */
    private List<ReflectionQueryResponse> toQueryResponses(List<ReflectionEntity> reflections) {
        if (reflections.isEmpty()) {
            return List.of();
        }

        // userId와 roomId 수집
        List<Long> userIds = reflections.stream()
                .map(ReflectionEntity::getUserId)
                .distinct()
                .toList();

        List<Long> roomIds = reflections.stream()
                .map(ReflectionEntity::getStudyRoomId)
                .distinct()
                .toList();

        // 한 번에 조회
        Map<Long, UserEntity> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u));

        Map<Long, StudyRoomEntity> roomMap = studyRoomRepository.findAllById(roomIds).stream()
                .collect(Collectors.toMap(StudyRoomEntity::getId, r -> r));

        // Response 생성
        return reflections.stream()
                .map(r -> {
                    UserEntity user = userMap.get(r.getUserId());
                    StudyRoomEntity room = roomMap.get(r.getStudyRoomId());

                    return ReflectionQueryResponse.builder()
                            .reflectionId(r.getId())
                            .sessionId(r.getSessionId())
                            .content(r.getContent())
                            .focusScore(r.getFocusScore())
                            .imageUrl(r.getImageUrl())
                            .createdAt(r.getCreatedAt())
                            .user(UserInfo.builder()
                                    .userId(user.getId())
                                    .nickname(user.getNickname())
                                    .profileUrl(user.getProfileUrl())
                                    .build())
                            .room(ReflectionQueryResponse.RoomInfo.builder()
                                    .roomId(room.getId())
                                    .roomName(room.getTitle())
                                    .build())
                            .build();
                })
                .toList();
    }
}
