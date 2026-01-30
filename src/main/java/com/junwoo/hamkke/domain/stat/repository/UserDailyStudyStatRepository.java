package com.junwoo.hamkke.domain.stat.repository;

import com.junwoo.hamkke.domain.stat.entity.UserDailyStudyStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
public interface UserDailyStudyStatRepository extends JpaRepository<UserDailyStudyStat, Long> {

    Optional<UserDailyStudyStat> findByUserIdAndStudyDate(Long userId, LocalDate studyDate);

    List<UserDailyStudyStat> findAllByUserIdAndStudyDateBetween(Long userId, LocalDate start, LocalDate end);
}
