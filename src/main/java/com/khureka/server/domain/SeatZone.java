package com.khureka.server.domain;

import com.khureka.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌석 구역 Entity.
 *
 * 사용자가 응모 시 선택하는 단위이다.
 * 예: "1층 A구역", "2층 중앙구역", "응원석" 등
 *
 * 하나의 EventSchedule에 여러 SeatZone이 존재한다.
 * 각 SeatZone 안에 여러 Seat(실제 좌석)이 존재한다.
 */
@Entity
@Table(name = "seat_zones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatZone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private EventSchedule schedule;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Builder
    public SeatZone(EventSchedule schedule, String name, Integer price) {
        this.schedule = schedule;
        this.name = name;
        this.price = price;
    }
}
