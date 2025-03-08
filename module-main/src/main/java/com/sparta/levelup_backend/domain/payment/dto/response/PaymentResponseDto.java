package com.sparta.levelup_backend.domain.payment.dto.response;

import com.sparta.levelup_backend.domain.payment.entity.PaymentEntity;
import com.sparta.levelup_backend.domain.review.dto.response.UserResponseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PaymentResponseDto {

    private final Long amount;

    private final String orderName;

    private final String orderId;

    private final String customerEmail;

    private final String customerName;

    private final String customerKey;

    @Setter
    private String successUrl;

    @Setter
    private String failUrl;

    public PaymentResponseDto(PaymentEntity payment, UserResponseDto user) {
        this.amount = payment.getAmount();
        this.orderName = payment.getOrder().getOrderName();
        this.orderId = payment.getOrderId();
        this.customerEmail = user.getEmail();
        this.customerName = user.getNickName();
        this.customerKey = payment.getUserKey();
    }
}
