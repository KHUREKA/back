package com.khureka.server.domain;

import com.khureka.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 실제 좌석 Entity.
 *
 * 사용자가 직접 선택하지 않으며, 서버가 추첨 후 랜덤 배정한다.
 * 하나의 SeatZone 안에 여러 Seat이 존재한다.
 *
 * Unique 제약: 같은 구역 내 (row_label, seat_number) 조합은 유일해야 한다.
 */
@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_seat_in_zone",
                columnNames = {"seat_zone_id", "row_label", "seat_number"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_zone_id", nullable = false)
    private SeatZone seatZone;

    @Column(nullable = false, length = 20)
    private String rowLabel;

    @Column(nullable = false, length = 20)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatStatus status;

    @Builder
    public Seat(SeatZone seatZone, String rowLabel, String seatNumber, SeatStatus status) {
        this.seatZone = seatZone;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.status = (status != null) ? status : SeatStatus.AVAILABLE;
    }

    /**
     * 좌석을 배정 상태로 변경한다.
     */
    public void assignSeat() {
        this.status = SeatStatus.ASSIGNED;
    }
}
