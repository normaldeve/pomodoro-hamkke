package com.junwoo.hamkke.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public record LoginRequest(

        @NotBlank(message = "이메일은 필수입니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {
}
