package com.sparta.client;

import com.sparta.config.CustomErrorDecoder;
import com.sparta.config.FeignConfig;
import com.sparta.domain.bill.dto.responseDto.OrderEntityResponseDto;
import com.sparta.domain.payment.dto.request.UpdateOrderStatusDto;
import com.sparta.domain.payment.dto.request.UpdateProductAmountDto;
import com.sparta.domain.payment.dto.response.BooleanStatusDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "module-main", configuration = {CustomErrorDecoder.class, FeignConfig.class})
public interface EntityServiceClient {

	@GetMapping("/v2/orders/intra/findOrderById/{orderId}")
	OrderEntityResponseDto findOrderById(@PathVariable("orderId") Long orderId);

	@PutMapping("/v2/orders/intra/updateOrderStatus")
	BooleanStatusDto updateOrderStatus(@RequestBody UpdateOrderStatusDto updateOrderStatusDto);

	@PutMapping("/v1/products/intra/updateProductAmount")
	BooleanStatusDto updateProductAmount(@RequestBody UpdateProductAmountDto updateProductAmountDto);
}
