package com.sparta.levelup_backend.domain.bill.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.levelup_backend.domain.bill.entity.BillEntity;
import com.sparta.levelup_backend.domain.review.dto.response.UserResponseDto;
import com.sparta.levelup_backend.utill.BillStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class BillResponseDto {

    private final Long billId;

    private final String tutorName;

    private final String tutorNumber;

    private final String studentName;

    private final String studentNumber;

    private final String billHistory;

    private final Long price;

    private final BillStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime paymentDate;

    public BillResponseDto(BillEntity bill, UserResponseDto tutor, UserResponseDto student) {
        this.billId = bill.getId();
        this.tutorName = tutor.getNickName();
        this.tutorNumber = tutor.getPhoneNumber();
        this.studentName = student.getNickName();
        this.studentNumber = student.getPhoneNumber();
        this.billHistory = bill.getBillHistory();
        this.price = bill.getPrice();
        this.status = bill.getStatus();
        this.paymentDate = bill.getCreatedAt();
    }

}
