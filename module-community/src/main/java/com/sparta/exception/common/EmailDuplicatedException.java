package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.ErrorCode;

public class EmailDuplicatedException extends BusinessException {

    public EmailDuplicatedException() {
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}
