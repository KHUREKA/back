package com.khureka.server.ticket.dto;

import com.khureka.server.domain.ApplicationStatus;
import com.khureka.server.domain.PaymentStatus;
import com.khureka.server.domain.TicketApplication;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 응모 내역 응답 DTO (마이페이지).
 */
@Getter
@Builder
public class ApplicationResponse {

    private Long id;
    private String status;
    private Integer requestedSeatCount;
    private Boolean autoAssign;
    private LocalDateTime appliedAt;
    private LocalDateTime lotteryResultAt;
    private LocalDateTime paidAt;
    private String applicationCode;

    // 이벤트 정보
    private Long eventId;
    private String eventTitle;
    private String venueName;
    private LocalDateTime startTime;
    private LocalDateTime lotteryAt;
    private String thumbnailUrl;

    // 구역 정보
    private String priority1ZoneName;
    private String priority2ZoneName;
    private String priority3ZoneName;
    private String assignedZoneName;

    // 결제 정보
    private String mockPaymentStatus;

    public static ApplicationResponse from(TicketApplication app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .status(app.getStatus().name())
                .requestedSeatCount(app.getRequestedSeatCount())
                .autoAssign(app.getAutoAssign())
                .appliedAt(app.getAppliedAt())
                .lotteryResultAt(app.getLotteryResultAt())
                .paidAt(app.getPaidAt())
                .applicationCode(app.getApplicationCode())
                .eventId(app.getSchedule().getEvent().getId())
                .eventTitle(app.getSchedule().getEvent().getTitle())
                .venueName(app.getSchedule().getEvent().getVenueName())
                .startTime(app.getSchedule().getStartTime())
                .lotteryAt(app.getSchedule().getLotteryAt())
                .thumbnailUrl(app.getSchedule().getEvent().getThumbnailUrl())
                .priority1ZoneName(app.getPriority1SeatZone() != null
                        ? app.getPriority1SeatZone().getName() : null)
                .priority2ZoneName(app.getPriority2SeatZone() != null
                        ? app.getPriority2SeatZone().getName() : null)
                .priority3ZoneName(app.getPriority3SeatZone() != null
                        ? app.getPriority3SeatZone().getName() : null)
                .assignedZoneName(app.getAssignedSeatZone() != null
                        ? app.getAssignedSeatZone().getName() : null)
                .mockPaymentStatus(app.getMockPaymentStatus() != null
                        ? app.getMockPaymentStatus().name() : null)
                .build();
    }
}
