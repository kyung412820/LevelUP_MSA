package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.ErrorCode;

public class NotFoundException extends BusinessException {
    public NotFoundException(com.sparta.levelup_backend.exception.common.ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
