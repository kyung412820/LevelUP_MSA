package com.sparta.levelup_backend.domain.order.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BillCancelRequestDto {
	private Long billId;
}
