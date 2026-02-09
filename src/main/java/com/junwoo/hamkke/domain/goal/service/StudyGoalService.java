package com.junwoo.hamkke.domain.goal.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.goal.dto.CreateStudyGoalRequest;
import com.junwoo.hamkke.domain.goal.dto.StudyGoalResponse;
import com.junwoo.hamkke.domain.goal.entity.StudyGoalEntity;
import com.junwoo.hamkke.domain.goal.exception.StudyGoalException;
import com.junwoo.hamkke.domain.goal.repository.StudyGoalRepository;
import com.junwoo.hamkke.domain.room.entity.StudyRoomEntity;
import com.junwoo.hamkke.domain.room.exception.StudyRoomException;
import com.junwoo.hamkke.domain.room.repository.StudyRoomRepository;
import com.junwoo.hamkke.domain.user.entity.UserEntity;
import com.junwoo.hamkke.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudyGoalService {

    private final UserRepository userRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyGoalRepository studyGoalRepository;

    public StudyGoalResponse createGoal(UUID roomId, Long userId, CreateStudyGoalRequest request) {

        StudyRoomEntity studyRoom = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_ROOM));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new StudyRoomException(ErrorCode.CANNOT_FOUND_USER));

        StudyGoalEntity studyGoal = StudyGoalEntity.createGoal(roomId, userId, request);

        return StudyGoalResponse.from(studyGoalRepository.save(studyGoal));
    }

    @Transactional(readOnly = true)
    public List<StudyGoalResponse> getMyGoals(UUID roomId, Long userId) {
        return studyGoalRepository.findByStudyRoomIdAndUserId(roomId, userId)
                .stream()
                .map(StudyGoalResponse::from)
                .toList();
    }

    public void toggleGoal(Long goalId, Long userId) {

        StudyGoalEntity studyGoal = studyGoalRepository.findById(goalId)
                .orElseThrow(() -> new StudyGoalException(ErrorCode.CANNOT_FOUND_GOAL));

        studyGoal.toggleCompleted();

        log.info("[StudyGoalService] 목표 달성 변경 성공 - goalId: {}, userId: {}", goalId, userId);
    }

    public void deleteGoal(Long goalId) {

        studyGoalRepository.deleteById(goalId);

        log.info("[StudyGoalService] 목표 제거 성공 - goalId: {}", goalId);
    }
}
