package com.junwoo.hamkke.domain.stat.dto;

import lombok.Builder;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 13.
 */
@Builder
public record TodayStudyTimeResponse(
        int todayMinutes
) {
}
