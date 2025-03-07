package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.ErrorCode;

public class PaymentException extends BusinessException {
  public PaymentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public PaymentException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }
}


