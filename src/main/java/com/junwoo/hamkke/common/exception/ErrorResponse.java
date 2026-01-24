package com.junwoo.hamkke.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final int code;
    private final LocalDateTime timestamp;
    private final String message;
    private final Map<String, Object> details;

    public ErrorResponse(DomainException exception) {
        this(exception.getErrorCode().getCode() ,LocalDateTime.now(), exception.getMessage(), exception.getDetails());
    }

    public ErrorResponse(Exception e, int code) {
        this(code, LocalDateTime.now(), e.getMessage(), new HashMap<>());
    }
}
