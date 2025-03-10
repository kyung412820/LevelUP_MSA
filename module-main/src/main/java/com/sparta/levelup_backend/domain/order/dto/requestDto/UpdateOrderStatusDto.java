package com.sparta.levelup_backend.domain.order.dto.requestDto;


import com.sparta.levelup_backend.utill.OrderStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateOrderStatusDto {
	private final Long orderId;
	private final OrderStatus orderStatus;
}
