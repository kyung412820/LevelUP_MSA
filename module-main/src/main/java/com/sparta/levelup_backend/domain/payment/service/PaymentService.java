package com.sparta.levelup_backend.domain.payment.service;

import com.sparta.levelup_backend.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.levelup_backend.domain.payment.dto.request.UserAuthenticationRequestDto;
import com.sparta.levelup_backend.domain.payment.dto.response.CancelResponseDto;
import com.sparta.levelup_backend.domain.payment.dto.response.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createPayment(UserAuthenticationRequestDto auth, Long orderId);

    CancelResponseDto requestCancel(UserAuthenticationRequestDto auth, CancelPaymentRequestDto dto);
}
