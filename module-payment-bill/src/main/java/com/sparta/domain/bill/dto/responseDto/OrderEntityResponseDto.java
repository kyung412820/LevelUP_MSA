package com.sparta.domain.bill.dto.responseDto;

import java.time.LocalDateTime;

import com.sparta.domain.payment.dto.response.PaymentResponseDto;
import com.sparta.utill.OrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderEntityResponseDto {
	private Long id;
	private OrderStatus status;
	private Long totalPrice;
	private Long userId;
	private ProductEntityResponseDto product;
	private String orderName;
	private Long paymentId;
	private Boolean isDeleted = false;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public OrderEntityResponseDto(Long id, OrderStatus status, Long totalPrice, Long userId, ProductEntityResponseDto product, String orderName, Long paymentId, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.status = status;
		this.totalPrice = totalPrice;
		this.userId = userId;
		this.product = product;
		this.orderName = orderName;
		this.paymentId = paymentId;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}