package com.khureka.server.domain;

import com.khureka.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공연/경기의 일정 정보 Entity.
 *
 * 하나의 TicketEvent는 여러 EventSchedule을 가질 수 있다.
 * 각 일정에는 응모 기간, 추첨 시간, 상태가 관리된다.
 */
@Entity
@Table(name = "event_schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private TicketEvent event;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private LocalDateTime applicationOpenAt;

    @Column(nullable = false)
    private LocalDateTime applicationCloseAt;

    @Column(nullable = false)
    private LocalDateTime lotteryAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScheduleStatus status;

    @Builder
    public EventSchedule(TicketEvent event, LocalDateTime startTime, LocalDateTime endTime,
                          LocalDateTime applicationOpenAt, LocalDateTime applicationCloseAt,
                          LocalDateTime lotteryAt, ScheduleStatus status) {
        this.event = event;
        this.startTime = startTime;
        this.endTime = endTime;
        this.applicationOpenAt = applicationOpenAt;
        this.applicationCloseAt = applicationCloseAt;
        this.lotteryAt = lotteryAt;
        this.status = (status != null) ? status : ScheduleStatus.APPLICATION_OPEN;
    }

    /**
     * 추첨 완료 처리.
     */
    public void markLotteryDone() {
        this.status = ScheduleStatus.LOTTERY_DONE;
    }

    /**
     * 응모 마감 처리.
     */
    public void closeApplication() {
        this.status = ScheduleStatus.APPLICATION_CLOSED;
    }

    /**
     * 행사 종료 처리.
     */
    public void finish() {
        this.status = ScheduleStatus.FINISHED;
    }
}
