package com.junwoo.hamkke.domain.point.dto;

import lombok.Builder;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 31.
 */
@Builder
public record PointSummaryResponse(
        int totalPoints,
        int todayEarned,
        int thisWeekendEarned,
        int thisMonthEarned
) {

}
