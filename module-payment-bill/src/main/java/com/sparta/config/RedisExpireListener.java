package com.sparta.config;


import static com.sparta.exception.common.ErrorCode.*;
import static com.sparta.utill.OrderStatus.*;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.client.EntityServiceClient;
import com.sparta.domain.bill.dto.responseDto.OrderEntityResponseDto;
import com.sparta.domain.payment.dto.request.UpdateProductAmountDto;
import com.sparta.domain.payment.dto.response.BooleanStatusDto;
import com.sparta.exception.common.LockException;
import com.sparta.exception.common.MismatchException;
import com.sparta.exception.common.NetworkTimeoutException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisExpireListener implements MessageListener {

    private final EntityServiceClient entityServiceClient;
    private final RedissonClient redissonClient;

    @Transactional
    public void handleOrderExpiration(Long orderId) {
            OrderEntityResponseDto order = getOrder(orderId);
        RLock lock = redissonClient.getLock("stock_lock_" + order.getProduct().getId());

        if (order.getStatus() == PENDING) {

            try {
                boolean avaiable = lock.tryLock(1, 10, TimeUnit.SECONDS);
                if (!avaiable) {
                    throw new LockException(CONFLICT_LOCK_GET);
                }
                if(!updateProductAmount(new UpdateProductAmountDto(order.getProduct().getId())).isStatus()) {
                    throw new MismatchException(MISMATCH_PRODUCT_AMOUNT);
                }
                log.info("상품: {} 수량 복구 완료", order.getProduct().getProductName());
            } catch (InterruptedException e) {
                throw new LockException(CONFLICT_LOCK_ERROR);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }

            log.info("Order {}가 만료로 인해 삭제되었습니다.", orderId);
        }
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
            String expiredKey = message.toString();

            if (expiredKey.startsWith("order:expire:")) {
                Long orderId = Long.parseLong(expiredKey.replace("order:expire:", ""));
                handleOrderExpiration(orderId);
            }
        }

        public OrderEntityResponseDto getOrder(Long orderId) {
        try{

        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
        return entityServiceClient.findOrderById(orderId);
        }

    public BooleanStatusDto updateProductAmount(UpdateProductAmountDto updateProductAmountDto) {
        try{
            return entityServiceClient.updateProductAmount(updateProductAmountDto);
        }catch(FeignException e){
            throw new NetworkTimeoutException(e.contentUTF8());
        }
    }
    }
