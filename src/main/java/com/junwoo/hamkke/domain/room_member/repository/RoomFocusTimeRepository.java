package com.junwoo.hamkke.domain.room_member.repository;

import com.junwoo.hamkke.domain.room_member.entity.RoomFocusTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
public interface RoomFocusTimeRepository extends JpaRepository<RoomFocusTimeEntity, Long> {

    Optional<RoomFocusTimeEntity> findByUserIdAndStudyRoomIdAndFocusDate(Long userId, Long studyRoomId, LocalDate focusDate);
}
