package com.sparta.levelup_backend.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sparta.levelup_backend.domain.order.dto.requestDto.BillCancelRequestDto;
import com.sparta.levelup_backend.domain.order.dto.requestDto.BillCreateRequestDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.BillEntityResponseDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.BooleanStatusDto;

@FeignClient(name = "module-payment-bill")
public interface BillServiceClient {

	@PostMapping("/v2/bill/intra/createBill")
	BooleanStatusDto createBill(@RequestBody BillCreateRequestDto billCreateRequestDto);

	@GetMapping("/v2/bill/intra/findBillById/{billId}")
	BillEntityResponseDto findBillById(@PathVariable("billId") Long billId);

	@PutMapping("/v2/bill/intra/cancelBill")
	BooleanStatusDto cancelBill(@RequestBody BillCancelRequestDto billCancelRequestDto);
}
