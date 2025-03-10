// package com.sparta.domain.community.dto.response;
//
// import com.sparta.utill.OrderStatus;
// import java.time.LocalDateTime;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.Setter;
//
// @Getter
// @Setter
// @Builder
// public class OrderResponseDto {
// 	private Long id;
// 	private OrderStatus status;
// 	private Long totalPrice;
// 	private UserEntityResponseDto user;
// 	private ProductResponseDto product;
// 	private String orderName;
// 	private PaymentResponseDto payment;
// 	private Boolean isDeleted = false;
// 	private LocalDateTime createdAt;
// 	private LocalDateTime updatedAt;
//
// 	public OrderResponseDto(Long id, OrderStatus status, Long totalPrice, UserEntityResponseDto user, ProductResponseDto product, String orderName, PaymentResponseDto payment, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
// 		this.id = id;
// 		this.status = status;
// 		this.totalPrice = totalPrice;
// 		this.user = user;
// 		this.product = product;
// 		this.orderName = orderName;
// 		this.payment = payment;
// 		this.isDeleted = isDeleted;
// 		this.createdAt = createdAt;
// 		this.updatedAt = updatedAt;
// 	}
// }