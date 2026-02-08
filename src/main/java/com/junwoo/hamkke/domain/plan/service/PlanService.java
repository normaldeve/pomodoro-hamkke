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

    /**
     * 계획 생성
     */
    @Transactional
    public PlanResponse createPlan(Long userId, CreatePlanRequest request) {
        validateTimeRange(request.startTime(), request.endTime());

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
        validateTimeRange(request.startTime(), request.endTime());

        PlanEntity plan = getPlanByIdAndUserId(planId, userId);

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
        PlanEntity plan = getPlanByIdAndUserId(planId, userId);
        plan.complete();
        return PlanResponse.from(plan);
    }

    /**
     * 계획 삭제
     */
    @Transactional
    public void deletePlan(Long userId, Long planId) {
        PlanEntity plan = getPlanByIdAndUserId(planId, userId);
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

    /**
     * 미완료 계획 개수 조회
     */
    public long getIncompleteCount(Long userId) {
        return planRepository.countIncompleteByUserId(userId, LocalDate.now());
    }

    /**
     * 계획 조회 (권한 검증 포함)
     */
    private PlanEntity getPlanByIdAndUserId(Long planId, Long userId) {
        return planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new PlanException(ErrorCode.PLAN_NOT_FOUND));
    }

    /**
     * 시간 범위 유효성 검증
     */
    private void validateTimeRange(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new PlanException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}