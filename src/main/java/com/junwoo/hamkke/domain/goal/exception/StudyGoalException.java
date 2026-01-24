package com.junwoo.hamkke.domain.goal.exception;

import com.junwoo.hamkke.common.exception.DomainException;
import com.junwoo.hamkke.common.exception.ErrorCode;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public class StudyGoalException extends DomainException {

    public StudyGoalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
