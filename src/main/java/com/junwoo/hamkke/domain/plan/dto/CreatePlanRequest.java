package com.junwoo.hamkke.domain.plan.dto;

import com.junwoo.hamkke.domain.plan.entity.EventColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 학습 계획 생성 요청
 * @author junnukim1007gmail.com
 * @date 26. 2. 8.
 */
public record CreatePlanRequest(
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다")
        String title,

        @NotNull(message = "날짜는 필수입니다")
        LocalDate planDate,

        @NotNull(message = "시작 시간은 필수입니다")
        LocalTime startTime,

        @NotNull(message = "종료 시간은 필수입니다")
        LocalTime endTime,

        @NotNull(message = "색상은 필수입니다")
        EventColor color
) {
}
