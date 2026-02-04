package com.junwoo.hamkke.domain.room_member.dto;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 4.
 */
public record TransferHostRequests(
        @NotNull(message = "위임할 사용자 ID는 필수입니다")
        Long targetUserId
) {
}
