package com.sparta.levelup_backend.domain.order.controller;

import static com.sparta.levelup_backend.common.ApiResMessage.*;
import static com.sparta.levelup_backend.common.ApiResponse.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.levelup_backend.common.ApiResponse;
import com.sparta.levelup_backend.domain.order.dto.requestDto.OrderCreateRequestDto;
import com.sparta.levelup_backend.domain.order.dto.requestDto.UserAuthenticationRequestDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.OrderResponseDto;
import com.sparta.levelup_backend.domain.order.service.OrderServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ApiResponse<OrderResponseDto> createOrder(
            HttpServletRequest request,
            @RequestBody OrderCreateRequestDto dto
    ) {

        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        OrderResponseDto orderResponseDto = orderService.createOrder(userId, dto);
        return success(OK, ORDER_CREATE, orderResponseDto);
    }

    // 주문 조회
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> findOrder(
            HttpServletRequest request,
            @PathVariable Long orderId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        OrderResponseDto orderById = orderService.findOrder(userId, orderId);
        return success(OK, ORDER_FIND, orderById);
    }

    // 주문 결제 완료
    @PatchMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> updateOrder(
            HttpServletRequest request,
            @PathVariable Long orderId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        OrderResponseDto order = orderService.updateOrder(userId, orderId);
        return success(OK, ORDER_UPDATE, order);
    }

    // 결제 완료
    @PatchMapping("/student/{orderId}")
    public ApiResponse<OrderResponseDto> completeOrder(
            HttpServletRequest request,
            @PathVariable Long orderId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        OrderResponseDto order = orderService.completeOrder(userId, orderId);
        return success(OK, ORDER_COMPLETE, order);
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> deleteOrderByPending(
            HttpServletRequest request,
            @PathVariable Long orderId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        orderService.deleteOrderByPending(userId, orderId);
        return success(OK, ORDER_CANCLED);
    }

    // 결제 취소 (거래중 일때)
    @DeleteMapping("/tutor/{orderId}")
    public ApiResponse<Void> deleteOrderByTrading(
            HttpServletRequest request,
            @PathVariable Long orderId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        orderService.deleteOrderByTrading(userId, orderId);
        return success(OK, ORDER_CANCLED);
    }
}
