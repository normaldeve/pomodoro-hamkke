package com.junwoo.hamkke.domain.plan.service;

import com.junwoo.hamkke.common.exception.ErrorCode;
import com.junwoo.hamkke.domain.plan.dto.CreatePlanRequest;
import com.junwoo.hamkke.domain.plan.dto.PlanResponse;
import com.junwoo.hamkke.domain.plan.dto.UpdatePlanRequest;
import com.junwoo.hamkke.domain.plan.entity.PlanEntity;
import com.junwoo.hamkke.domain.plan.exception.PlanException;
import com.junwoo.hamkke.domain.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Plan Service
 * @author junnukim1007gmail.com
 * @date 26. 2. 8.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanValidation planValidation;

    /**
     * 계획 생성
     */
    @Transactional
    public PlanResponse createPlan(Long userId, CreatePlanRequest request) {
        planValidation.validateTimeRange(request.startTime(), request.endTime());
        planValidation.validateOverlap(userId, request.planDate(), request.startTime(), request.endTime());

        PlanEntity plan = PlanEntity.builder()
                .userId(userId)
                .title(request.title())
                .planDate(request.planDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .color(request.color())
                .build();

        PlanEntity savedPlan = planRepository.save(plan);
        return PlanResponse.from(savedPlan);
    }

    /**
     * 계획 수정
     */
    @Transactional
    public PlanResponse updatePlan(Long userId, Long planId, UpdatePlanRequest request) {
        planValidation.validateTimeRange(request.startTime(), request.endTime());
        planValidation.validateOverlapForUpdate(userId, planId, request.planDate(), request.startTime(), request.endTime());

        PlanEntity plan = planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new PlanException(ErrorCode.PLAN_NOT_FOUND));

        plan.updatePlan(
                request.title(),
                request.planDate(),
                request.startTime(),
                request.endTime(),
                request.color()
        );

        return PlanResponse.from(plan);
    }

    /**
     * 계획 완료 상태 토글
     */
    @Transactional
    public PlanResponse togglePlanCompleted(Long userId, Long planId) {
        PlanEntity plan = planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new PlanException(ErrorCode.PLAN_NOT_FOUND));
        plan.complete();
        return PlanResponse.from(plan);
    }

    /**
     * 계획 미완료 상태 토글
     */
    @Transactional
    public PlanResponse togglePlanUnCompleted(Long userId, Long planId) {
        PlanEntity plan = planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new PlanException(ErrorCode.PLAN_NOT_FOUND));
        plan.unComplete();
        return PlanResponse.from(plan);
    }

    /**
     * 계획 삭제
     */
    @Transactional
    public void deletePlan(Long userId, Long planId) {
        PlanEntity plan = planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new PlanException(ErrorCode.PLAN_NOT_FOUND));
        planRepository.delete(plan);
    }

    /**
     * 특정 날짜의 계획 조회
     */
    public List<PlanResponse> getPlansByDate(Long userId, LocalDate date) {
        return planRepository.findByUserIdAndPlanDate(userId, date)
                .stream()
                .map(PlanResponse::from)
                .toList();
    }

    /**
     * 기간별 계획 조회
     */
    public List<PlanResponse> getPlansByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new PlanException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return planRepository.findByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(PlanResponse::from)
                .toList();
    }
}