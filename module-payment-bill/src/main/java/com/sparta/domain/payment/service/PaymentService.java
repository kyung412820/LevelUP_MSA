package com.sparta.domain.payment.service;

import com.sparta.domain.bill.dto.requestDto.UserAuthenticationRequestDto;
import com.sparta.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.domain.payment.dto.response.CancelResponseDto;
import com.sparta.domain.payment.dto.response.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createPayment(UserAuthenticationRequestDto auth, Long orderId);

    CancelResponseDto requestCancel(UserAuthenticationRequestDto auth, CancelPaymentRequestDto dto);
}
