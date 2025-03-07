package com.sparta.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sparta.levelup_backend.domain.bill.dto.responseDto.BillCreatedEvent;
import com.sparta.levelup_backend.domain.bill.dto.responseDto.BillStatusMessageDto;
import com.sparta.levelup_backend.domain.bill.entity.BillEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BillEventListener {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String BILL_CHANNEL = "billStatusChannel";

    @TransactionalEventListener
    public void handleBillCreated(BillCreatedEvent event) {
        BillEntity bill = event.getBill();

        BillStatusMessageDto message = new BillStatusMessageDto(
                bill.getId(),
                bill.getStatus(),
                bill.getTutor().getId(),
                bill.getStudent().getId(),
                bill.getBillHistory()
        );

        redisTemplate.convertAndSend(BILL_CHANNEL, message.toJson());

        System.out.println("Redis 이벤트 발행 완료: " + message.toString());
    }
}
