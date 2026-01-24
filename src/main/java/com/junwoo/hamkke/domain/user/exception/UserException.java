package com.junwoo.hamkke.domain.user.exception;

import com.junwoo.hamkke.common.exception.DomainException;
import com.junwoo.hamkke.common.exception.ErrorCode;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 12.
 */
public class UserException extends DomainException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
