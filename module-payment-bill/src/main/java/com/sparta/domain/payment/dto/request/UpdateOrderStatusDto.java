package com.sparta.domain.payment.dto.request;

import com.sparta.utill.OrderStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateOrderStatusDto {
	private final Long orderId;
	private final OrderStatus orderStatus;
}
