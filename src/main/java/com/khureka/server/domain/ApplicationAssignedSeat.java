package com.khureka.server.domain;

import com.khureka.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 응모 1건에 배정된 실제 좌석 Entity.
 *
 * 사용자가 2장을 신청했다면, 이 테이블에 2개의 레코드가 생긴다.
 *
 * 예시:
 *   application_id: 10, seat_id: 101, ticket_code: "TICKET-10-101"
 *   application_id: 10, seat_id: 102, ticket_code: "TICKET-10-102"
 *
 * Unique 제약:
 * - UNIQUE(seat_id)                → 같은 좌석이 여러 응모에 중복 배정되는 것을 DB 차원에서 방지
 * - UNIQUE(application_id, seat_id) → 동일 응모에 같은 좌석이 중복 배정되는 것을 방지
 */
@Entity
@Table(name = "application_assigned_seats", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_assigned_seat",
                columnNames = {"seat_id"}
        ),
        @UniqueConstraint(
                name = "uk_application_seat",
                columnNames = {"application_id", "seat_id"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationAssignedSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private TicketApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false, length = 100)
    private String ticketCode;

    @Builder
    public ApplicationAssignedSeat(TicketApplication application, Seat seat, String ticketCode) {
        this.application = application;
        this.seat = seat;
        this.ticketCode = ticketCode;
    }
}
