package com.junwoo.hamkke.domain.room.exception;

import com.junwoo.hamkke.common.exception.DomainException;
import com.junwoo.hamkke.common.exception.ErrorCode;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 24.
 */
public class StudyRoomException extends DomainException {

    public StudyRoomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
