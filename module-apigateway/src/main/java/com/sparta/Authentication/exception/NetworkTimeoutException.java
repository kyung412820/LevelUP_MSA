package com.sparta.Authentication.exception;

import com.sparta.Authentication.exception.common.BusinessException;
import com.sparta.Authentication.exception.common.ErrorCode;

public class NetworkTimeoutException extends BusinessException {
	public NetworkTimeoutException(String details) {
		super(ErrorCode.NETWORK_TIMEOUT,details);
	}
}
