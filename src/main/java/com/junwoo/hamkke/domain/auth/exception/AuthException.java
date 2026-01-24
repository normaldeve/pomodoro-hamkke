package com.junwoo.hamkke.domain.auth.exception;

import com.junwoo.hamkke.common.exception.DomainException;
import com.junwoo.hamkke.common.exception.ErrorCode;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public class AuthException extends DomainException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
