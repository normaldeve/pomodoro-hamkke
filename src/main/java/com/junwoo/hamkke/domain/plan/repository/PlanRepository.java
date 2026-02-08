package com.junwoo.hamkke.domain.plan.repository;

import com.junwoo.hamkke.domain.plan.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 8.
 */
public interface PlanRepository extends JpaRepository<PlanEntity, Long> {

    /**
     * 사용자의 특정 날짜 계획 조회
     */
    List<PlanEntity> findByUserIdAndPlanDate(Long userId, LocalDate planDate);

    /**
     * 사용자의 기간별 계획 조회
     */
    @Query("SELECT p FROM PlanEntity p WHERE p.userId = :userId " +
            "AND p.planDate BETWEEN :startDate AND :endDate " +
            "ORDER BY p.planDate, p.startTime")
    List<PlanEntity> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 사용자의 특정 계획 조회
     */
    Optional<PlanEntity> findByIdAndUserId(Long id, Long userId);

    /**
     * 사용자의 미완료 계획 개수 조회
     */
    @Query("SELECT COUNT(p) FROM PlanEntity p WHERE p.userId = :userId " +
            "AND p.completed = false AND p.planDate >= :today")
    long countIncompleteByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);
}
