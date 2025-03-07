package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.ErrorCode;

public class PasswordConfirmNotMatchedException extends BusinessException {

    public PasswordConfirmNotMatchedException() {
        super(ErrorCode.INVALID_PASSWORD_CONFIRM);
    }
}
