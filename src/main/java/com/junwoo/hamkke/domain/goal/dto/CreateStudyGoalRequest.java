package com.junwoo.hamkke.domain.goal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Builder
public record CreateStudyGoalRequest(
        @NotBlank(message = "목표를 입력해주세요")
        String content
        ) {
}
