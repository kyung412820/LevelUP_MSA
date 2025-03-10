package com.sparta.exception.common;

public class NetworkTimeoutException extends BusinessException {
	public NetworkTimeoutException(String details) {
		super(ErrorCode.NETWORK_TIMEOUT,details);
	}
}
