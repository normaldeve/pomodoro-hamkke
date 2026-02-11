package com.junwoo.hamkke.domain.plan.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.plan.exception.PlanException;
import com.junwoo.hamkke.domain.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 11.
 */
@Component
@RequiredArgsConstructor
public class PlanValidation {

    private final PlanRepository planRepository;

    /**
     * 시간 범위 유효성 검증
     */
    public void validateTimeRange(LocalTime start, LocalTime end) {
        if (end.isBefore(start) || end.equals(start)) {
            throw new PlanException(ErrorCode.INVALID_PLAN_TIME_RANGE);
        }
    }

    /**
     * 일정 겹침 검증 (생성용)
     */
    public void validateOverlap(Long userId, LocalDate planDate, LocalTime start, LocalTime end) {

        boolean exists = planRepository
                .existsByUserIdAndPlanDateAndStartTimeLessThanAndEndTimeGreaterThan(
                        userId,
                        planDate,
                        end,
                        start
                );

        if (exists) {
            throw new PlanException(ErrorCode.PLAN_TIME_CONFLICT);
        }
    }

    /**
     * 일정 겹침 검증 (수정용)
     */
    public void validateOverlapForUpdate(Long userId, Long planId, LocalDate planDate, LocalTime start, LocalTime end) {

        boolean exists = planRepository
                .existsByUserIdAndPlanDateAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                        userId,
                        planDate,
                        planId,
                        end,
                        start
                );

        if (exists) {
            throw new PlanException(ErrorCode.PLAN_TIME_CONFLICT);
        }
    }
}
