package com.sparta.levelup_backend.domain.order.dto.responseDto;

import java.time.LocalDateTime;

import com.sparta.levelup_backend.utill.BillStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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

}
