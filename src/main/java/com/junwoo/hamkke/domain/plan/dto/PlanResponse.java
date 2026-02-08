package com.junwoo.hamkke.domain.plan.dto;

import com.junwoo.hamkke.domain.plan.entity.EventColor;
import com.junwoo.hamkke.domain.plan.entity.PlanEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 학습 계획 응답
 * @author junnukim1007gmail.com
 * @date 26. 2. 8.
 */
public record PlanResponse(
        Long id,
        String title,
        LocalDate planDate,
        LocalTime startTime,
        LocalTime endTime,
        EventColor color,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PlanResponse from(PlanEntity plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getPlanDate(),
                plan.getStartTime(),
                plan.getEndTime(),
                plan.getColor(),
                plan.isCompleted(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}