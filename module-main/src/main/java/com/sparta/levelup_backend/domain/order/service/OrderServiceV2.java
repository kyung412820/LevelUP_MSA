package com.sparta.levelup_backend.domain.order.service;

import java.util.List;

import com.sparta.levelup_backend.domain.order.dto.requestDto.OrderCreateRequestDto;
import com.sparta.levelup_backend.domain.order.dto.requestDto.UpdateOrderStatusDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.BooleanStatusDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.OrderEntityResponseDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.OrderResponseDto;

public interface OrderServiceV2 {
    OrderResponseDto createOrder(Long userId, OrderCreateRequestDto dto);

    OrderResponseDto findOrder(Long userId, Long orderId);

    OrderResponseDto updateOrder(Long userId, Long orderId);

    OrderResponseDto completeOrder(Long userId, Long orderId);

    void deleteOrderByPending(Long userId, Long orderId);

    void deleteOrderByTrading(Long userId, Long orderId);

    OrderEntityResponseDto findOrderById(Long orderId);

    List<OrderEntityResponseDto> findAllOrders(List<Long> orderIds);

    BooleanStatusDto updateOrderStatus(UpdateOrderStatusDto updateOrderStatusDto);
}
