package com.sparta.Authentication.exception.common;

public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
