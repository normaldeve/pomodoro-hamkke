package com.junwoo.hamkke.domain.room_member.repository;

import com.junwoo.hamkke.domain.room_member.entity.DailyFocusTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 27.
 */
public interface DailyFocusTimeRepository extends JpaRepository<DailyFocusTimeEntity, Long> {

    // 특정 일자로 사용자 집중 시간 조회
    Optional<DailyFocusTimeEntity> findByUserIdAndFocusDate(Long userId, LocalDate focusDate);
}
