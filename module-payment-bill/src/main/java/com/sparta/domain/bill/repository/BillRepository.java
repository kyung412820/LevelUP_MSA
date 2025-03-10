package com.sparta.domain.bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

import com.sparta.domain.bill.entity.BillEntity;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.NotFoundException;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

public interface BillRepository extends JpaRepository<BillEntity, Long>, BillRepositoryCustom{

    default BillEntity findByIdOrElseThrow(Long billId) {
        return findById(billId).orElseThrow(() -> new NotFoundException(ErrorCode.BILL_NOT_FOUND));
    }

    @Query("SELECT b FROM BillEntity b WHERE b.id = :billId")
    Optional<BillEntity> findByIdWithTutorAndStudent(Long billId);

    Optional<BillEntity> findByOrderId(Long order);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})  // 락 획특 시간 설정
    @Query("SELECT o FROM BillEntity o WHERE o.id = :billId")
    Optional<BillEntity> getFindByIdWithLock(Long billId);

    default BillEntity findByIdOrElseThrowWithLock(Long billId) {
        return getFindByIdWithLock(billId).orElseThrow(() -> new NotFoundException(ErrorCode.BILL_NOT_FOUND));
    }

}
