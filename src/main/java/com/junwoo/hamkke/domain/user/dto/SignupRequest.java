package com.junwoo.hamkke.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public record SignupRequest(
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
        String password
        ) {

}
