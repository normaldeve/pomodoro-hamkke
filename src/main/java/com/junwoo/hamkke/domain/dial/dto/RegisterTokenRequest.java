package com.junwoo.hamkke.domain.dial.dto;

import com.junwoo.hamkke.domain.notification.entity.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 12.
 */
public record RegisterTokenRequest(
        @NotBlank(message = "FCM 토큰은 필수입니다")
        String fcmToken,

        @NotNull(message = "디바이스 타입은 필수입니다")
        DeviceType deviceType
) {
}
