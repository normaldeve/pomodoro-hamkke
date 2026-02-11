package com.junwoo.hamkke.domain.stat.repository;

import com.junwoo.hamkke.domain.stat.entity.UserDailyStudyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 특정 기간의 총 공부 시간 계산
    @Query("""
        SELECT COALESCE(SUM(s.totalMinutes), 0)
        FROM UserDailyStudyStat s
        WHERE s.userId = :userId
        AND s.studyDate BETWEEN :startDate AND :endDate
    """)
    int sumMinutesByPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 연속 공부 일수 계산용
    @Query("""
        SELECT s.studyDate
        FROM UserDailyStudyStat s
        WHERE s.userId = :userId
        AND s.studyDate <= :today
        AND s.totalMinutes > 0
        ORDER BY s.studyDate DESC
    """)
    List<LocalDate> findRecentStudyDates(@Param("userId") Long userId, @Param("today") LocalDate today);

    // 특정 기간의 월별 총 공부 시간 조회
    @Query("""
    SELECT
        FUNCTION('YEAR', s.studyDate),
        FUNCTION('MONTH', s.studyDate),
        SUM(s.totalMinutes)
    FROM UserDailyStudyStat s
    WHERE s.userId = :userId
    AND s.studyDate BETWEEN :startDate AND :endDate
    GROUP BY FUNCTION('YEAR', s.studyDate), FUNCTION('MONTH', s.studyDate)
    ORDER BY FUNCTION('YEAR', s.studyDate), FUNCTION('MONTH', s.studyDate)
""")
    List<Object[]> getMonthlyStudyMinutes(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
