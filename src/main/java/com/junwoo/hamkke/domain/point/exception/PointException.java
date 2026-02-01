package com.junwoo.hamkke.domain.point.exception;

import com.junwoo.hamkke.common.exception.DomainException;
import com.junwoo.hamkke.common.exception.ErrorCode;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 31.
 */
public class PointException extends DomainException {

    public PointException(ErrorCode errorCode) {
        super(errorCode);
    }
}
