package com.sparta.exception.common;


public class ForbiddenException extends BusinessException {
  public ForbiddenException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ForbiddenException(ErrorCode errorCode, String detail){
    super(errorCode, detail);
  }

}
