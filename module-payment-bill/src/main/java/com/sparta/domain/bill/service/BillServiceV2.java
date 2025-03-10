package com.sparta.domain.bill.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.domain.bill.dto.requestDto.BillCancelRequestDto;
import com.sparta.domain.bill.dto.responseDto.BillEntityResponseDto;
import com.sparta.domain.bill.dto.responseDto.BillResponseDto;
import com.sparta.domain.payment.dto.response.BooleanStatusDto;

public interface BillServiceV2 {
    Page<BillResponseDto> findBillsByTutor(Long userId, Pageable pageable);

    Page<BillResponseDto> findBillsByStudent(Long userId, Pageable pageable);

    BillResponseDto findBillByTutor(Long userId, Long billId);

    BillResponseDto findBillByStudent(Long userId, Long billId);

    void deleteBillByTutor(Long userId, Long billId);

    void deleteBillByStudent(Long userId, Long billId);

	BillEntityResponseDto findBillById(Long billId);

    BooleanStatusDto cancelBIll(BillCancelRequestDto billCancelRequestDto);
}
