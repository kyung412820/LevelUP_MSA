package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.ErrorCode;

public class AlreadyDeletedUserException extends BusinessException {

    public AlreadyDeletedUserException() {
        super(ErrorCode.ALREADY_DELETED_USER);
    }
}
