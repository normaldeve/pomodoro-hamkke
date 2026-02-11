package com.junwoo.hamkke.domain.stat.dto;

import lombok.Builder;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 11.
 */
@Builder
public record StudyTimeSummaryResponse(
        int todayMinutes,
        int thisWeekMinutes,
        int thisMonthMinutes,
        int consecutiveDays // 연속 공부 일
) {
}
