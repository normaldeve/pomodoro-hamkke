package com.junwoo.hamkke.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public record LoginRequest(

        @NotBlank(message = "아이디 입력은 필수입니다")
        String username,

        @NotBlank(message = "비밀번호 입력은 필수입니다")
        String password
) {
}
