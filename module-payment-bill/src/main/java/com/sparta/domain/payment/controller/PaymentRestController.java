package com.sparta.domain.payment.controller;

import static com.sparta.common.ApiResMessage.*;
import static com.sparta.common.ApiResponse.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.common.ApiResponse;
import com.sparta.domain.bill.dto.requestDto.UserAuthenticationRequestDto;
import com.sparta.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.domain.payment.dto.response.CancelResponseDto;
import com.sparta.domain.payment.dto.response.PaymentResponseDto;
import com.sparta.domain.payment.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentService paymentService;

    @PostMapping("/v3/request/{orderId}")
    public ApiResponse<PaymentResponseDto> createPayment(
            HttpServletRequest request,
            @PathVariable Long orderId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        PaymentResponseDto response = paymentService.createPayment(authRequest, orderId);
        return success(OK, OK_REQUEST, response);
    }

    @PostMapping("v3/request/cancel")
    public ApiResponse<CancelResponseDto> requestCancel(
            HttpServletRequest request,
            @RequestBody CancelPaymentRequestDto dto
    ) {

        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
        CancelResponseDto response = paymentService.requestCancel(authRequest, dto);
        return success(OK, OK_REQUEST_CANCEL, response);
    }
}
