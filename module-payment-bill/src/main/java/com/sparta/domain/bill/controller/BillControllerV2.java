package com.sparta.domain.bill.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.sparta.common.ApiResMessage.*;
import static com.sparta.common.ApiResponse.*;
import static org.springframework.http.HttpStatus.*;

import com.sparta.common.ApiResponse;
import com.sparta.domain.bill.dto.requestDto.BillCancelRequestDto;
import com.sparta.domain.bill.dto.requestDto.BillCreateRequestDto;
import com.sparta.domain.bill.dto.requestDto.UserAuthenticationRequestDto;
import com.sparta.domain.bill.dto.responseDto.BillEntityResponseDto;
import com.sparta.domain.bill.dto.responseDto.BillResponseDto;
import com.sparta.domain.bill.service.BillServiceImplV2;
import com.sparta.domain.payment.dto.response.BooleanStatusDto;

@RestController
@RequestMapping("/v2/bills")
@RequiredArgsConstructor
public class BillControllerV2 {

    private final BillServiceImplV2 billService;

    @Value("${toss.client.key}")
    private String tossClientKey;

    @PostMapping("/intra/createBill")
    BooleanStatusDto createBill(@RequestBody BillCreateRequestDto billCreateRequestDto){

        return billService.createBill(billCreateRequestDto.getUserId(),
            billCreateRequestDto.getOrderId());
    };

    @GetMapping("/intra/findBillById/{billId}")
    BillEntityResponseDto findBillById(@PathVariable("billId") Long billId){
        return billService.findBillById(billId);
    };

    @PutMapping("/intra/cancelBill")
    BooleanStatusDto cancelBill(@RequestBody BillCancelRequestDto billCancelRequestDto){
        return billService.cancelBIll(billCancelRequestDto);
    };

    // 결제내역 페이징 조회(tutor 전용)
    @GetMapping("/tutor")
    public ApiResponse<Page<BillResponseDto>> findBillsByTutor(
            HttpServletRequest request,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);
        Long userId = authRequest.getId();

        Page<BillResponseDto> billById = billService.findBillsByTutor(userId, pageable);
        return success(OK, BILL_FIND, billById);
    }

    // 결제내역 페이징 조회(student 전용)
    @GetMapping("/student")
    public ApiResponse<Page<BillResponseDto>> findBillsByStudent(
            HttpServletRequest request,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        Page<BillResponseDto> billById = billService.findBillsByStudent(userId, pageable);
        return success(OK, BILL_FIND, billById);
    }

    // 결제내역 단건 조회(tutor 전용)
    @GetMapping("/tutor/{billId}")
    public ApiResponse<BillResponseDto> findBillByTutor(
           HttpServletRequest request,
            @PathVariable Long billId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        BillResponseDto bill = billService.findBillByTutor(userId, billId);
        return success(OK, BILL_FIND, bill);
    }

    // 결제내역 단건 조회(student 전용)
    @GetMapping("/student/{billId}")
    public ApiResponse<BillResponseDto> findBillByStudent(
            HttpServletRequest request,
            @PathVariable Long billId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        BillResponseDto bill = billService.findBillByStudent(userId, billId);
        return success(OK, BILL_FIND, bill);
    }

    // 결제내역 삭제(tutor)
    @DeleteMapping("/tutor/{billId}")
    public ApiResponse<Void> deleteBillByTutor(
            HttpServletRequest request,
            @PathVariable Long billId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        billService.deleteBillByTutor(userId, billId);
        return success(OK, BILL_DELETE);
    }

    // 결제내역 삭제(student)
    @DeleteMapping("/student/{billId}")
    public ApiResponse<Void> deleteBillByStudent(
            HttpServletRequest request,
            @PathVariable Long billId
    ) {
        String encodedAuth = request.getHeader("UserAuthentication");
        UserAuthenticationRequestDto authRequest = UserAuthenticationRequestDto.from(encodedAuth);

        Long userId = authRequest.getId();
        billService.deleteBillByStudent(userId, billId);
        return success(OK, BILL_DELETE);
    }

    @GetMapping("/client-key")
    public String getClientKey() {
        return tossClientKey;
    }
}