package com.junwoo.hamkke.domain.goal.repository;

import com.junwoo.hamkke.domain.goal.entity.StudyGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public interface StudyGoalRepository extends JpaRepository<StudyGoalEntity, Long> {

    List<StudyGoalEntity> findByStudyRoomIdAndUserId(UUID studyRoomId, Long userId);

}
