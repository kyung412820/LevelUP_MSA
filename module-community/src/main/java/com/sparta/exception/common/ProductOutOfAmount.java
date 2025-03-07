package com.sparta.exception.common;

import com.sparta.levelup_backend.exception.common.BusinessException;
import com.sparta.levelup_backend.exception.common.ErrorCode;

public class ProductOutOfAmount extends BusinessException {
    public ProductOutOfAmount() {
        super(ErrorCode.DUPLICATE_OUT_OF_AMOUNT);
    }
}
