package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.ErrorCode;

public class CurrentPasswordNotMatchedException extends BusinessException {

    public CurrentPasswordNotMatchedException() {
        super(ErrorCode.INVALID_CURRENT_PASSWORD);
    }
}
