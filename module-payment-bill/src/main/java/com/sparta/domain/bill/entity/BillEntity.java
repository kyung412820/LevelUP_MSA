package com.sparta.domain.bill.entity;

import com.sparta.common.entity.BaseEntity;
import com.sparta.enums.BillStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bills")
public class BillEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tutorId;

    private Long studentId;

    private Long orderId;

    @Column(name = "bill_history", nullable = false)
    private String billHistory;

    @Column(name = "price")
    private Long price;

    @Setter
    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @Column(name = "tutor_is_deleted")
    private Boolean tutorIsDeleted;

    @Column(name = "student_is_deleted")
    private Boolean studentIsDeleted;

    public void billDelete() {
        this.delete();
    }

    public void billTutorDelete() {
        this.tutorIsDeleted = true;
    }

    public void billStudentDelete() {
        this.studentIsDeleted = true;
    }

    public void cancelBill() {
        this.status = BillStatus.PAYCANCELED;
    }
}
