package com.junwoo.hamkke.domain.room_member.repository;

import com.junwoo.hamkke.domain.room_member.entity.StudyRoomFocusStatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 26.
 */
public interface StudyRoomFocusStatRepository extends JpaRepository<StudyRoomFocusStatEntity, Long> {

    Optional<StudyRoomFocusStatEntity> findByRoomIdAndUserIdAndSessionNumber(Long studyRoomId, Long userId, int sessionNumber);
}
