package com.junwoo.hamkke.domain.plan.exception;

import com.junwoo.hamkke.common.exception.DomainException;
import com.junwoo.hamkke.common.exception.ErrorCode;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 2. 8.
 */
public class PlanException extends DomainException {

    public PlanException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PlanException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
