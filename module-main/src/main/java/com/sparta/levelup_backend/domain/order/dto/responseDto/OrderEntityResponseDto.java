package com.sparta.levelup_backend.domain.order.dto.responseDto;

import java.time.LocalDateTime;

import com.sparta.levelup_backend.domain.order.entity.OrderEntity;
import com.sparta.levelup_backend.utill.OrderStatus;

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
	public OrderEntityResponseDto(OrderEntity order, ProductEntityResponseDto product) {
		this.id = order.getId();
		this.status = order.getStatus();
		this.totalPrice = order.getTotalPrice();
		this.userId = order.getUserId();
		this.product = product;
		this.orderName = order.getOrderName();
		this.paymentId = order.getPaymentId();
		this.isDeleted = order.getIsDeleted();
		this.createdAt = order.getCreatedAt();
		this.updatedAt = order.getUpdatedAt();
	}
}