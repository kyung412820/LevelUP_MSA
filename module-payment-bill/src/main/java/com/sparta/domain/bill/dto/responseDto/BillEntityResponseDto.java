package com.sparta.domain.bill.dto.responseDto;

import java.time.LocalDateTime;

import com.sparta.domain.bill.entity.BillEntity;
import com.sparta.enums.BillStatus;

import lombok.Getter;

@Getter
public class BillEntityResponseDto {

    private Long id;
    private Long tutorId;
    private Long studentId;
    private Long orderId;
    private String billHistory;
    private Long price;
    private BillStatus status;
    private Boolean tutorIsDeleted;
    private Boolean studentIsDeleted;
    private Boolean isDeleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public BillEntityResponseDto(BillEntity billEntity) {
        this.id = billEntity.getId();
        this.tutorId = billEntity.getTutorId();
        this.studentId = billEntity.getStudentId();
        this.orderId = billEntity.getOrderId();
        this.billHistory = billEntity.getBillHistory();
        this.price = billEntity.getPrice();
        this.status = billEntity.getStatus();
        this.tutorIsDeleted = billEntity.getTutorIsDeleted();
        this.studentIsDeleted = billEntity.getStudentIsDeleted();
        this.isDeleted = billEntity.getIsDeleted();
        this.createdAt = billEntity.getCreatedAt();
        this.updatedAt = billEntity.getUpdatedAt();
    }

}
