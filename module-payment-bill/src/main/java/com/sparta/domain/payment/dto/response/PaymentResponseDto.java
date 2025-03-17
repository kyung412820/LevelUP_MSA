package com.sparta.domain.payment.dto.response;

import com.sparta.domain.bill.dto.responseDto.OrderEntityResponseDto;
import com.sparta.domain.bill.dto.responseDto.UserEntityResponseDto;
import com.sparta.domain.payment.entity.PaymentEntity;

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

    public PaymentResponseDto(PaymentEntity payment, OrderEntityResponseDto order, UserEntityResponseDto user) {
        this.amount = payment.getAmount();
        this.orderName = order.getOrderName();
        this.orderId = payment.getOrderId();
        this.customerEmail = user.getEmail();
        this.customerName = user.getNickName();
        this.customerKey = payment.getUserKey();
    }
}
