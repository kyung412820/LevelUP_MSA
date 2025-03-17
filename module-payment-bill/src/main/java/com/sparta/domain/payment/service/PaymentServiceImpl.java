package com.sparta.domain.payment.service;


import static com.sparta.exception.common.ErrorCode.*;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import com.sparta.client.EntityServiceClient;
import com.sparta.client.UserServiceClient;
import com.sparta.config.TossPaymentConfig;
import com.sparta.domain.bill.dto.requestDto.UserAuthenticationRequestDto;
import com.sparta.domain.bill.dto.responseDto.OrderEntityResponseDto;
import com.sparta.domain.bill.dto.responseDto.UserEntityResponseDto;
import com.sparta.domain.payment.dto.response.CancelResponseDto;
import com.sparta.domain.payment.dto.response.PaymentResponseDto;
import com.sparta.domain.payment.entity.PaymentEntity;
import com.sparta.domain.payment.repository.PaymentRepository;
import com.sparta.exception.common.BusinessException;
import com.sparta.exception.common.ForbiddenException;
import com.sparta.exception.common.NetworkTimeoutException;
import com.sparta.exception.common.OrderException;
import com.sparta.exception.common.PaymentException;
import com.sparta.utill.OrderStatus;
import com.sparta.domain.payment.dto.request.CancelPaymentRequestDto;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PaymentRepository paymentRepository;
    private final EntityServiceClient entityServiceClient;
    private final UserServiceClient userServiceClient;
    private final TossPaymentConfig tossPaymentConfig;
    private final RateLimitService rateLimitService;

    @Transactional
    @Override
    public PaymentResponseDto createPayment(UserAuthenticationRequestDto auth, Long orderId) {
        Long userId = auth.getId();
            OrderEntityResponseDto order = getOrder(orderId);
           UserEntityResponseDto user = getUser(order.getUserId());
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        logger.info("주문상태: {}", order.getStatus());
        // 결제 대기 상태에서 결제요청 불가
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderException(INVALID_ORDER_STATUS);
        }


        // 결제 정보가 있다면 새로 만들지말고 결제 정보를 조회해서 사용
        PaymentEntity existingPayment = paymentRepository.findByPaymentOrderId(order.getId());
        if (existingPayment != null) {
            OrderEntityResponseDto existingPaymentOrder = getOrder(existingPayment.getPaymentOrderId());
            UserEntityResponseDto existingUser = getUser(existingPaymentOrder.getUserId());
            PaymentResponseDto response = new PaymentResponseDto(existingPayment,existingPaymentOrder,existingUser);
            response.setSuccessUrl(tossPaymentConfig.getSuccessUrl());
            response.setFailUrl(tossPaymentConfig.getFailUrl());
            logger.info("결제정보Id: {}", existingPayment.getPaymentId());
            return response;
        }

        PaymentEntity payment = PaymentEntity.builder()
                .orderId(UUID.randomUUID().toString())
                .paymentOrderId(order.getId())
                .customerUserId(user.getId())
                .orderName(order.getProduct().getProductName())
                .amount(order.getTotalPrice())
                .customerName(user.getNickName())
                .customerEmail(user.getEmail())
                .userKey(user.getCustomerKey())
                .ispaid(false)
                .iscanceled(false)
                .build();

        PaymentResponseDto response = new PaymentResponseDto(payment, order ,user);
        response.setSuccessUrl(tossPaymentConfig.getSuccessUrl());
        response.setFailUrl(tossPaymentConfig.getFailUrl());
        paymentRepository.save(payment);
        logger.info("결제정보Id 생성: {}", payment.getPaymentId());

        return response;
    }

    @Override
    public CancelResponseDto requestCancel(UserAuthenticationRequestDto auth, CancelPaymentRequestDto dto) {

        PaymentEntity payment = paymentRepository.findByPaymentKey(dto.getKey())
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));
        OrderEntityResponseDto order = getOrder(payment.getPaymentOrderId());

        // 판매자 검증
        if (!order.getProduct().getUserId().equals(auth.getId())) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        // 취소 완료 되었는지 검증
        if (order.getStatus().equals(OrderStatus.CANCELED)) {
            throw new PaymentException(PAYMENT_CANCELED_OK);
        }

        // 결제 요청 상태일 때 취소 불가
        if (order.getStatus().equals(OrderStatus.PENDING)) {
            throw new PaymentException(PAYMENT_PENDING);
        }

        if (!rateLimitService.isRequest(auth.getId())) {
            throw new BusinessException(INVALID_REQUEST_MANY);
        }

        logger.info("취소 이유: {}, paymentKey: {}", dto.getReason(), dto.getKey());

        return CancelResponseDto.builder()
                .cancelReason(dto.getReason())
                .paymentKey(dto.getKey())
                .build();
    }

    public OrderEntityResponseDto getOrder(Long orderId) {
        try{
            return entityServiceClient.findOrderById(orderId);
        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
    }

    public UserEntityResponseDto getUser(Long userId) {
        try{
            return userServiceClient.findUserById(userId);
        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
    }

}
