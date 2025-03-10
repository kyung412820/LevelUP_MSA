//package com.sparta.domain.community.dto.response;
//
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@Builder
//public class PaymentResponseDto {
//	private Long paymentId;
//	private String orderId;
//	private String payType;
//	private Long amount;
//	private String orderName;
//	private OrderResponseDto order;
//	private UserEntityResponseDto customer;
//	private String userKey;
//	private String paymentKey;
//	private String completedAt;
//	private boolean isPaid;
//	private boolean isCanceled;
//	private String customerEmail;
//	private String customerName;
//
//	public PaymentResponseDto(Long paymentId, String orderId, String payType, Long amount, String orderName,
//		OrderResponseDto order, UserEntityResponseDto customer, String userKey, String paymentKey,
//		String completedAt, boolean isPaid, boolean isCanceled, String customerEmail, String customerName) {
//		this.paymentId = paymentId;
//		this.orderId = orderId;
//		this.payType = payType;
//		this.amount = amount;
//		this.orderName = orderName;
//		this.order = order;
//		this.customer = customer;
//		this.userKey = userKey;
//		this.paymentKey = paymentKey;
//		this.completedAt = completedAt;
//		this.isPaid = isPaid;
//		this.isCanceled = isCanceled;
//		this.customerEmail = customerEmail;
//		this.customerName = customerName;
//	}
//}
