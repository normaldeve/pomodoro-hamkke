package com.junwoo.hamkke.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
@Getter
@AllArgsConstructor
public class RefreshTokenEntry {
    private final String token;
    private final long expireAt; // epoch milli
}
