package com.sparta.levelup_backend.domain.order.dto.responseDto;

import com.sparta.levelup_backend.domain.order.entity.OrderEntity;
import com.sparta.levelup_backend.domain.review.dto.response.UserEntityResponseDto;
import com.sparta.levelup_backend.utill.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderResponseDto {

    private final Long orderId;

    private final Long productId;

    private final String Student;

    private final String productName;

    private final OrderStatus status;

    private final Long price;

    public OrderResponseDto(OrderEntity order, UserEntityResponseDto user) {
        this.orderId = order.getId();
        this.productId = order.getProduct().getId();
        this.Student = user.getNickName();
        this.productName = order.getProduct().getProductName();
        this.status = order.getStatus();
        this.price = order.getTotalPrice();
    }
}
