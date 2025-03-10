package com.sparta.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sparta.domain.bill.dto.responseDto.BillCreatedEvent;
import com.sparta.domain.bill.dto.responseDto.BillStatusMessageDto;
import com.sparta.domain.bill.entity.BillEntity;

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
                bill.getTutorId(),
                bill.getStudentId(),
                bill.getBillHistory()
        );

        redisTemplate.convertAndSend(BILL_CHANNEL, message.toJson());

        System.out.println("Redis 이벤트 발행 완료: " + message.toString());
    }
}
