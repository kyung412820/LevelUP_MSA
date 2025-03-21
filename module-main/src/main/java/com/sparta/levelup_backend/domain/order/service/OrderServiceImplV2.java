package com.sparta.levelup_backend.domain.order.service;

import static com.sparta.levelup_backend.exception.common.ErrorCode.*;
import static com.sparta.levelup_backend.utill.OrderStatus.CANCELED;
import static com.sparta.levelup_backend.utill.OrderStatus.COMPLETED;
import static com.sparta.levelup_backend.utill.OrderStatus.PENDING;
import static com.sparta.levelup_backend.utill.OrderStatus.TRADING;

import com.sparta.levelup_backend.client.BillServiceClient;

import com.sparta.levelup_backend.domain.order.dto.requestDto.BillCancelRequestDto;
import com.sparta.levelup_backend.domain.order.dto.requestDto.BillCreateRequestDto;
import com.sparta.levelup_backend.domain.order.dto.requestDto.OrderCreateRequestDto;
import com.sparta.levelup_backend.domain.order.dto.requestDto.UpdateOrderStatusDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.BillEntityResponseDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.BooleanStatusDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.OrderEntityResponseDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.OrderResponseDto;
import com.sparta.levelup_backend.domain.order.dto.responseDto.ProductEntityResponseDto;
import com.sparta.levelup_backend.domain.order.entity.OrderEntity;
import com.sparta.levelup_backend.domain.order.repository.OrderRepository;
import com.sparta.levelup_backend.domain.product.entity.ProductEntity;
import com.sparta.levelup_backend.domain.product.service.ProductServiceImpl;
import com.sparta.levelup_backend.client.UserServiceClient;
import com.sparta.levelup_backend.domain.review.dto.response.UserEntityResponseDto;
import com.sparta.levelup_backend.exception.common.ForbiddenException;
import com.sparta.levelup_backend.exception.common.LockException;
import com.sparta.levelup_backend.exception.common.MismatchException;
import com.sparta.levelup_backend.exception.common.NetworkTimeoutException;
import com.sparta.levelup_backend.exception.common.NotFoundException;
import com.sparta.levelup_backend.exception.common.OrderException;
import com.sparta.levelup_backend.utill.ProductStatus;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImplV2 implements OrderServiceV2 {
    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceImpl productService;
    private final BillServiceClient billServiceClient;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 주문생성
     *
     * @param dto productId
     * @return orderId, productId, productName, status, price
     */
    @Override
    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderCreateRequestDto dto) {

        RLock lock = redissonClient.getLock("stock_lock_" + dto.getProductId());

        UserEntityResponseDto user = getUser(userId);

        OrderEntity saveOrder = null;

        try {
            boolean avaiable = lock.tryLock(1, 10, TimeUnit.SECONDS);
            if (!avaiable) {
                throw new LockException(CONFLICT_LOCK_GET);
            }

            // 비관적 락
            ProductEntity product = productService.getFindByIdWithLock(dto.getProductId());

            if (product.getStatus().equals(ProductStatus.INACTIVE)) {
                throw new NotFoundException(PRODUCT_NOT_FOUND);
            }

            if (user.getId() == product.getUserId()) {
                throw new OrderException(INVALID_ORDER_CREATE);
            }

            product.decreaseAmount();

            OrderEntity order = OrderEntity.builder()
                    .userId(user.getId())
                    .status(PENDING)
                    .totalPrice(product.getPrice())
                    .product(product)
                    .orderName(product.getProductName())
                    .build();


            saveOrder = orderRepository.save(order);

            // Redis에 주문 Id 저장 (10분 TTL)
            redisTemplate.opsForValue().set("order:expire:" + order.getId(), "PENDING", Duration.ofMinutes(10));
        } catch (InterruptedException e) {
            throw new LockException(CONFLICT_LOCK_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return new OrderResponseDto(saveOrder,user);
    }

    /**
     * 주문 조회
     *
     * @param orderId 조회 주문 id
     * @return orderId, productId, productName, status, price
     */
    @Override
    public OrderResponseDto findOrder(Long userId, Long orderId) {

        OrderEntity order = orderRepository.findByIdOrElseThrow(orderId);
        UserEntityResponseDto user = getUser(order.getUserId());

        // 구매자와 판매자만 조회 가능
        if (!order.getUserId().equals(userId) && !order.getProduct().getUserId().equals(userId)) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        return new OrderResponseDto(order, user);
    }

    /**
     * 주문 상태 변경
     *
     * @param orderId 변경할 주문 id
     * @return orderId, productId, productName, status, price
     */
    @Override
    @Transactional
    public OrderResponseDto updateOrder(Long userId, Long orderId) {

        OrderEntity order = orderRepository.findByIdOrElseThrow(orderId);

        // 구매자인지 확인
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        // 결제 대기 상태가 아니라면 변경 불가
        if (order.getStatus() != PENDING) {
            throw new OrderException(INVALID_ORDER_STATUS);
        }

        order.setStatus(TRADING);
        OrderEntity saveOrder = orderRepository.save(order);
        UserEntityResponseDto user = getUser(saveOrder.getUserId());

        if(!createBill(new BillCreateRequestDto(userId, orderId)).isStatus()) {
            throw new NotFoundException(BILL_NOT_FOUND);
        }
        return new OrderResponseDto(saveOrder, user);
    }

    /**
     * 거래 완료
     *
     * @param userId  사용자 id
     * @param orderId 변경할 주문 id
     * @return orderId, productId, productName, status, price
     */
    @Override
    public OrderResponseDto completeOrder(Long userId, Long orderId) {

        OrderEntity order = orderRepository.findByIdOrElseThrow(orderId);
        UserEntityResponseDto user = getUser(order.getUserId());

        // 구매자인지 확인
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        // 거래중 상태가 아니라면 변경 불가
        if (order.getStatus() != TRADING) {
            throw new OrderException(INVALID_ORDER_STATUS);
        }

        order.setStatus(COMPLETED);
        orderRepository.save(order);
        log.info("order {} 거래 완료.", order.getId());
        return new OrderResponseDto(order,user);
    }

    /**
     * 주문 취소(결제 요청 상태일때)
     *
     * @param userId  구매자 id Or 판매자 id
     * @param orderId 주문 id
     * @return null
     */
    @Transactional
    @Override
    public void deleteOrderByPending(Long userId, Long orderId) {

        OrderEntity order = orderRepository.findByIdOrElseThrow(orderId);

        RLock lock = redissonClient.getLock("stock_lock_" + order.getProduct().getId());

        // 판매자 구매자 둘 다 취소 가능
        if (!order.getUserId().equals(userId) && !order.getProduct().getUserId().equals(userId)) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        // 거래요청이 아닐 때 예외 발생
        if (order.getStatus() != PENDING) {
            throw new OrderException(INVALID_ORDER_STATUS);
        }

        try {
            boolean avaiable = lock.tryLock(1, 10, TimeUnit.SECONDS);
            if (!avaiable) {
                throw new LockException(CONFLICT_LOCK_GET);
            }
            ProductEntity product = productService.getFindByIdWithLock(order.getProduct().getId());
            product.increaseAmount();
            order.setStatus(CANCELED);
            order.orderDelete();
            orderRepository.save(order);
        } catch (InterruptedException e) {
            throw new LockException(CONFLICT_LOCK_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    /**
     * 결제 취소(거래중 일 때 판매자만 가능)
     *
     * @param userId  판매자 id
     * @param orderId 주문 id
     * @return null
     */
    @Transactional
    @Override
    public void deleteOrderByTrading(Long userId, Long orderId) {

        OrderEntity order = orderRepository.findByIdOrElseThrow(orderId);

        RLock lock = redissonClient.getLock("stock_lock_" + order.getProduct().getId());

        // 판매자인지 확인
        if (!order.getProduct().getUserId().equals(userId)) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        // 거래중이 아닐 때 예외 발생
        if (order.getStatus() != TRADING) {
            throw new OrderException(INVALID_ORDER_STATUS);
        }

        try {
            boolean avaiable = lock.tryLock(1, 10, TimeUnit.SECONDS);
            if (!avaiable) {
                throw new LockException(CONFLICT_LOCK_GET);
            }
            ProductEntity product = productService.getFindByIdWithLock(order.getProduct().getId());
            product.increaseAmount();
            order.setStatus(CANCELED);
            order.orderDelete();

            BillEntityResponseDto bill = findBillById(orderId);
            if(!cancelBill(new BillCancelRequestDto(bill.getId())).isStatus()) {
               throw new MismatchException(MISMATCH_BILL_STATUS);
            }

            orderRepository.save(order);

        } catch (InterruptedException e) {
            throw new LockException(CONFLICT_LOCK_ERROR);
        }
        finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    @Override
    public OrderEntityResponseDto findOrderById(Long orderId) {
        OrderEntity order = orderRepository.findByIdOrElseThrow(orderId);
        return new OrderEntityResponseDto(order, new ProductEntityResponseDto(order.getProduct()));
    }

    @Override
    public List<OrderEntityResponseDto> findAllOrders(List<Long> orderIds) {
        List<OrderEntity> allOrders = orderRepository.findAllById(orderIds);
        List<Long> userIdList = allOrders.stream().map(OrderEntity::getUserId).toList();
        Map<Long, UserEntityResponseDto> users = findAllUsers(userIdList).stream().collect(Collectors.
            toMap(user -> user.getId(), user -> user));

        List<OrderEntityResponseDto> orderResponseDtos = new ArrayList<>();

        for (OrderEntity order : allOrders) {
            orderResponseDtos.add(new OrderEntityResponseDto(order, new ProductEntityResponseDto(order.getProduct())));
        }

        return orderResponseDtos;
    }

    @Override
    @Transactional
    public BooleanStatusDto updateOrderStatus(UpdateOrderStatusDto updateOrderStatusDto) {
        try {
            OrderEntity order = orderRepository.findByIdOrElseThrow(updateOrderStatusDto.getOrderId());
            order.setStatus(updateOrderStatusDto.getOrderStatus());
            return new BooleanStatusDto(true);

        }catch(Exception e){
            return new BooleanStatusDto(false);
        }
    }

    public UserEntityResponseDto getUser(Long userId) {
        try{
            return userServiceClient.findUserById(userId);
        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
    }

    public List<UserEntityResponseDto> findAllUsers(List<Long> userIds) {
        try{
            return userServiceClient.findAllUsers(userIds);
        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
    }

    public BooleanStatusDto createBill(BillCreateRequestDto billCreateRequestDto) {
        try{
            return billServiceClient.createBill(billCreateRequestDto);
        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
    }

    public BillEntityResponseDto findBillById(Long billId) {
        try{
            return billServiceClient.findBillById(billId);
        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
    }

    public BooleanStatusDto cancelBill(BillCancelRequestDto billCancelRequestDto) {
        try{
            return billServiceClient.cancelBill(billCancelRequestDto);
        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
    }

}
