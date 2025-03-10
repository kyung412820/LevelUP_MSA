package com.sparta.domain.bill.service;



import static com.sparta.enums.BillStatus.*;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sparta.client.EntityServiceClient;
import com.sparta.client.UserServiceClient;
import com.sparta.domain.bill.dto.requestDto.BillCancelRequestDto;
import com.sparta.domain.bill.dto.responseDto.BillEntityResponseDto;
import com.sparta.domain.bill.dto.responseDto.BillResponseDto;
import com.sparta.domain.bill.dto.responseDto.OrderEntityResponseDto;
import com.sparta.domain.bill.dto.responseDto.UserResponseDto;
import com.sparta.domain.bill.entity.BillEntity;
import com.sparta.domain.bill.repository.BillRepository;
import com.sparta.domain.payment.dto.response.BooleanStatusDto;
import com.sparta.exception.common.DuplicateException;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.ForbiddenException;
import com.sparta.exception.common.NetworkTimeoutException;
import com.sparta.exception.common.NotFoundException;

@Service
@RequiredArgsConstructor
public class BillServiceImplV2 implements BillServiceV2 {

	private final BillRepository billRepository;
	private final EntityServiceClient entityServiceClient;
	private final UserServiceClient userServiceClient;
	private final BillEventPubService billEventPubService;

	// 거래 내역 생성 (거래중으로 바뀌었을때 생성 됌)
	@Transactional
	public BooleanStatusDto createBill(Long userId, Long orderId) {
		try {
			UserResponseDto user = getUser(userId); // tutor 정보

			OrderEntityResponseDto order = getOrder(orderId);

			BillEntity bill = BillEntity.builder()
				.tutorId(order.getProduct().getUserId())
				.studentId(user.getId())
				.orderId(order.getId())
				.billHistory(order.getProduct().getProductName())
				.price(order.getTotalPrice())
				.status(PAYREQUEST)
				.tutorIsDeleted(false)
				.studentIsDeleted(false)
				.build();

			billEventPubService.createPaidEvent(bill);
		}catch(Exception e) {
			return new BooleanStatusDto(false);
		}
		return new BooleanStatusDto(true);
	}

	/**
	 * 결제내역 페이징 조회 (tutor 전용)
	 * @param userId 사용자
	 * @param pageable 삭제되지않은 내역 페이징
	 * @return billId, tutorName, tutorNumber, studentName, studentNumber, billHistory, price, status
	 */
	@Override
	public Page<BillResponseDto> findBillsByTutor(Long userId, Pageable pageable) {

		Page<BillEntity> tutorBills = billRepository.findTutorBills(userId, pageable);

		return findAllBillsByUsers(pageable, tutorBills);
	}

	/**
	 * 결제내역 페이징 조회 (student 전용)
	 * @param userId 사용자
	 * @param pageable 삭제되지않은 내역 페이징
	 * @return billId, tutorName, tutorNumber, studentName, studentNumber, billHistory, price, status, paymentDate
	 */
	@Override
	public Page<BillResponseDto> findBillsByStudent(Long userId, Pageable pageable) {

		Page<BillEntity> studentBills = billRepository.findStudentBills(userId, pageable);

		return findAllBillsByUsers(pageable, studentBills);
	}

	/**
	 * 결제내역 단건 조회(tutor 전용)
	 * @param userId 사용자
	 * @param billId 결제내역Id
	 * @return billId, tutorName, tutorNumber, studentName, studentNumber, billHistory, price, status, paymentDate
	 */
	@Override
	public BillResponseDto findBillByTutor(Long userId, Long billId) {

		BillEntity bill = billRepository.findByIdWithTutorAndStudent(billId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.BILL_NOT_FOUND));

		if (!bill.getTutorId().equals(userId)) {
			throw new NotFoundException(ErrorCode.BILL_NOT_FOUND);
		}

		if (bill.getTutorIsDeleted().equals(true)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_DELETED_BILL);
		}
		return new BillResponseDto(bill, getUser(bill.getTutorId()),
			getUser(bill.getStudentId()));
	}

	/**
	 * 결제내역 단건 조회(student 전용)
	 * @param userId 사용자
	 * @param billId 결제내역Id
	 * @return billId, tutorName, tutorNumber, studentName, studentNumber, billHistory, price, status, paymentDate
	 */
	@Override
	public BillResponseDto findBillByStudent(Long userId, Long billId) {

		BillEntity bill = billRepository.findByIdWithTutorAndStudent(billId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.BILL_NOT_FOUND));

		if (!bill.getStudentId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_ACCESS);
		}

		if (bill.getStudentIsDeleted().equals(true)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_DELETED_BILL);
		}

		return new BillResponseDto(bill, getUser(bill.getTutorId()),
			getUser(bill.getStudentId()));
	}

	/**
	 * 결제내역 삭제 (tutor 전용)
	 * @param userId 사용자Id
	 * @param billId 거래내역Id
	 */
	@Override
	public void deleteBillByTutor(Long userId, Long billId) {

		BillEntity bill = billRepository.findByIdWithTutorAndStudent(billId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.BILL_NOT_FOUND));

		if (!bill.getTutorId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_ACCESS);
		}

		if (bill.getTutorIsDeleted().equals(true)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_DELETED_BILL);
		}

		bill.billTutorDelete();
		billRepository.save(bill);
	}

	/**
	 * 결제내역 삭제 (student 전용)
	 * @param userId 사용자Id
	 * @param billId 거래내역Id
	 */
	@Override
	public void deleteBillByStudent(Long userId, Long billId) {

		BillEntity bill = billRepository.findByIdWithTutorAndStudent(billId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.BILL_NOT_FOUND));

		if (!bill.getStudentId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_ACCESS);
		}

		if (bill.getStudentIsDeleted().equals(true)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_DELETED_BILL);
		}

		bill.billStudentDelete();
		billRepository.save(bill);
	}

	@Override
	public BillEntityResponseDto findBillById(Long billId) {
		return new BillEntityResponseDto(billRepository.findByIdOrElseThrow(billId));
	}

	@Override
	@Transactional
	public BooleanStatusDto cancelBIll(BillCancelRequestDto billCancelRequestDto) {
		try {
			BillEntity bill = billRepository.findByIdOrElseThrowWithLock(billCancelRequestDto.getBillId());
			bill.cancelBill();
		}catch(Exception e) {
			return new BooleanStatusDto(false);
		}
		return new BooleanStatusDto(true);
	}

	private Page<BillResponseDto> findAllBillsByUsers(Pageable pageable, Page<BillEntity> users) {
		List<BillEntity> tutorBillList = users.getContent();
		List<BillResponseDto> billResponseDtoList = new ArrayList<>();
		List<Long> allUsers = new ArrayList<>();

		for (BillEntity bill : tutorBillList) {
			allUsers.add(bill.getStudentId());
			allUsers.add(bill.getTutorId());
		}

		Map<Long, UserResponseDto> allUsersEntity = findAllUsers(allUsers).stream().collect(
			Collectors.toMap(UserResponseDto::getId, user -> user));

		for (BillEntity bill : tutorBillList) {
			billResponseDtoList.add(new BillResponseDto(bill, allUsersEntity.get(bill.getTutorId()),
				allUsersEntity.get(bill.getStudentId())));
		}

		return new PageImpl<>(billResponseDtoList, pageable, users.getTotalElements());
	}

	public OrderEntityResponseDto getOrder(Long orderId) {
		try{
			return entityServiceClient.findOrderById(orderId);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}

	public UserResponseDto getUser(Long userId) {
		try{
			return userServiceClient.findUserById(userId);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}

	public List<UserResponseDto> findAllUsers(List<Long> userIds) {
		try{
			return userServiceClient.findAllUsers(userIds);
		}catch(FeignException e){
			throw new NetworkTimeoutException(e.contentUTF8());
		}
	}
}
