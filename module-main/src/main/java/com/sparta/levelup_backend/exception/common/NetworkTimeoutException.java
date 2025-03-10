package com.sparta.levelup_backend.exception.common;

public class NetworkTimeoutException extends BusinessException {
	public NetworkTimeoutException(String details) {
		super(ErrorCode.NETWORK_TIMEOUT,details);
	}
}
