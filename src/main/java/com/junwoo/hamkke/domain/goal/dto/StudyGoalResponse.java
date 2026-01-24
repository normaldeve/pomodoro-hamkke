package com.junwoo.hamkke.domain.goal.dto;

import com.junwoo.hamkke.domain.goal.entity.StudyGoalEntity;
import lombok.Builder;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Builder
public record StudyGoalResponse(
        Long id,
        String content,
        boolean isCompleted
) {

    public static StudyGoalResponse from(StudyGoalEntity entity) {
        return StudyGoalResponse.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .isCompleted(entity.isCompleted())
                .build();
    }
}
