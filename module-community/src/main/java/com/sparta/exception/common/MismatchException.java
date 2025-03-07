package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.ErrorCode;

public class MismatchException extends BusinessException {

    public MismatchException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MismatchException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
