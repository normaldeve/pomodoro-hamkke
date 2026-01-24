package com.junwoo.hamkke.domain.image;

import com.junwoo.hamkke.common.exception.DomainException;
import com.junwoo.hamkke.common.exception.ErrorCode;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 21.
 */
public class ImageUploaderException extends DomainException {

    public ImageUploaderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
