package com.sparta.levelup_backend.domain.order.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BillCreateRequestDto {
	private Long userId;
	private Long orderId;
}
