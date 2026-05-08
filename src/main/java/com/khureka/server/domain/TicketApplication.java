package com.khureka.server.domain;

import com.khureka.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 응모 Entity.
 *
 * 사용자가 특정 일정에 대해 좌석 구역을 선택하고,
 * 원하는 좌석 개수(1~4)를 지정하여 응모한다.
 *
 * 좌석 구역 선택 모드:
 * - autoAssign = false (수동): 1~3순위 좌석 구역을 직접 선택
 * - autoAssign = true  (자동): 서버가 좌석이 충분한 구역을 랜덤 배정
 *
 * 추첨 결과에 따라:
 * - APPLIED → TICKET_ISSUED (당첨)
 * - APPLIED → LOSE (미당첨)
 *
 * Unique 제약: 동일 사용자가 같은 일정에 중복 응모할 수 없다.
 */
@Entity
@Table(name = "ticket_applications", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_user_schedule",
                columnNames = {"user_id", "schedule_id"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── FK: 응모자 ───
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ─── FK: 일정 ───
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private EventSchedule schedule;

    // ─── 요청 좌석 수 (1~4) ───
    @Column(nullable = false)
    private Integer requestedSeatCount;

    // ─── 자동 배정 여부 ───
    // true: 서버가 좌석 충분한 구역을 자동 배정 (priority 1/2/3 모두 null)
    // false: 사용자가 1~3순위 구역을 직접 선택
    @Column(nullable = false)
    private Boolean autoAssign;

    // ─── FK: 1순위 좌석 구역 (수동 선택 시 필수, 자동 선택 시 null) ───
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_1_seat_zone_id")
    private SeatZone priority1SeatZone;

    // ─── FK: 2순위 좌석 구역 (선택) ───
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_2_seat_zone_id")
    private SeatZone priority2SeatZone;

    // ─── FK: 3순위 좌석 구역 (선택) ───
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_3_seat_zone_id")
    private SeatZone priority3SeatZone;

    // ─── FK: 최종 배정된 좌석 구역 (추첨 후 설정) ───
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_seat_zone_id")
    private SeatZone assignedSeatZone;

    // ─── 응모 상태 ───
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationStatus status;

    // ─── 자동 결제 동의 ───
    @Column(nullable = false)
    private Boolean autoPaymentAgreed;

    // ─── Mock 결제 상태 ───
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentStatus mockPaymentStatus;

    // ─── 결제 시간 ───
    private LocalDateTime paidAt;

    // ─── 응모/티켓 묶음 코드 ───
    @Column(length = 100)
    private String applicationCode;

    // ─── 응모 시간 ───
    @Column(nullable = false)
    private LocalDateTime appliedAt;

    // ─── 추첨 결과 시간 ───
    private LocalDateTime lotteryResultAt;

    @Builder
    public TicketApplication(User user, EventSchedule schedule,
                              Integer requestedSeatCount,
                              Boolean autoAssign,
                              SeatZone priority1SeatZone,
                              SeatZone priority2SeatZone,
                              SeatZone priority3SeatZone,
                              Boolean autoPaymentAgreed) {
        this.user = user;
        this.schedule = schedule;
        this.requestedSeatCount = (requestedSeatCount != null) ? requestedSeatCount : 1;
        this.autoAssign = (autoAssign != null) ? autoAssign : false;
        this.priority1SeatZone = priority1SeatZone;
        this.priority2SeatZone = priority2SeatZone;
        this.priority3SeatZone = priority3SeatZone;
        this.status = ApplicationStatus.APPLIED;
        this.autoPaymentAgreed = (autoPaymentAgreed != null) ? autoPaymentAgreed : false;
        this.mockPaymentStatus = PaymentStatus.READY;
        this.appliedAt = LocalDateTime.now();
    }

    /**
     * 미당첨 처리.
     */
    public void markLose() {
        this.status = ApplicationStatus.LOSE;
        this.mockPaymentStatus = null;
        this.lotteryResultAt = LocalDateTime.now();
    }

    /**
     * 당첨 + Mock 결제 + 티켓 발급 처리.
     *
     * @param zone 배정된 좌석 구역
     * @param code 응모/티켓 묶음 코드
     */
    public void issueTicket(SeatZone zone, String code) {
        this.assignedSeatZone = zone;
        this.status = ApplicationStatus.TICKET_ISSUED;
        this.mockPaymentStatus = PaymentStatus.SUCCESS;
        this.paidAt = LocalDateTime.now();
        this.lotteryResultAt = LocalDateTime.now();
        this.applicationCode = code;
    }

    /**
     * 응모 취소 처리.
     */
    public void cancel() {
        this.status = ApplicationStatus.CANCELLED;
        this.mockPaymentStatus = null;
    }
}
