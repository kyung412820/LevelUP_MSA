package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.ErrorCode;

public class PageOutOfBoundsException extends BusinessException {
	public PageOutOfBoundsException(ErrorCode errorCode) {
		super(errorCode);
	}
}
