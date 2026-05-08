package com.khureka.server.ticket.dto;

import com.khureka.server.domain.EventSchedule;
import com.khureka.server.domain.ScheduleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventScheduleResponse {

    private Long id;
    private Long eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime applicationOpenAt;
    private LocalDateTime applicationCloseAt;
    private LocalDateTime lotteryAt;
    private ScheduleStatus status;

    public static EventScheduleResponse from(EventSchedule schedule) {
        return EventScheduleResponse.builder()
                .id(schedule.getId())
                .eventId(schedule.getEvent().getId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .applicationOpenAt(schedule.getApplicationOpenAt())
                .applicationCloseAt(schedule.getApplicationCloseAt())
                .lotteryAt(schedule.getLotteryAt())
                .status(schedule.getStatus())
                .build();
    }
}
