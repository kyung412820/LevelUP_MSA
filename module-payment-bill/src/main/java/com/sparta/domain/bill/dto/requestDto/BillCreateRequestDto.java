package com.sparta.domain.bill.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BillCreateRequestDto {
	private Long userId;
	private Long orderId;
}
