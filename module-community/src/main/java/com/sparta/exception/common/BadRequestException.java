package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.ErrorCode;

public class BadRequestException extends BusinessException {
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
