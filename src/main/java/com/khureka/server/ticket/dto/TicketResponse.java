package com.khureka.server.ticket.dto;

import com.khureka.server.domain.ApplicationAssignedSeat;
import com.khureka.server.domain.TicketApplication;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 티켓 보관함 응답 DTO.
 *
 * 하나의 응모(TicketApplication)에 배정된 좌석 목록을 포함한다.
 */
@Getter
@Builder
public class TicketResponse {

    private Long applicationId;
    private String applicationCode;
    private String status;
    private LocalDateTime paidAt;

    // 이벤트 정보
    private Long eventId;
    private String eventTitle;
    private String venueName;
    private String venueAddress;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private LocalDateTime startTime;
    private String thumbnailUrl;

    // 배정 구역 정보
    private String assignedZoneName;
    private Integer zonePrice;

    // 배정 좌석 목록
    private List<AssignedSeatInfo> seats;

    @Getter
    @Builder
    public static class AssignedSeatInfo {
        private String rowLabel;
        private String seatNumber;
        private String ticketCode;

        public static AssignedSeatInfo from(ApplicationAssignedSeat assigned) {
            return AssignedSeatInfo.builder()
                    .rowLabel(assigned.getSeat().getRowLabel())
                    .seatNumber(assigned.getSeat().getSeatNumber())
                    .ticketCode(assigned.getTicketCode())
                    .build();
        }
    }

    public static TicketResponse from(TicketApplication app, List<ApplicationAssignedSeat> assignedSeats) {
        return TicketResponse.builder()
                .applicationId(app.getId())
                .applicationCode(app.getApplicationCode())
                .status(app.getStatus().name())
                .paidAt(app.getPaidAt())
                .eventId(app.getSchedule().getEvent().getId())
                .eventTitle(app.getSchedule().getEvent().getTitle())
                .venueName(app.getSchedule().getEvent().getVenueName())
                .venueAddress(app.getSchedule().getEvent().getVenueAddress())
                .destinationLatitude(app.getSchedule().getEvent().getDestinationLatitude())
                .destinationLongitude(app.getSchedule().getEvent().getDestinationLongitude())
                .startTime(app.getSchedule().getStartTime())
                .thumbnailUrl(app.getSchedule().getEvent().getThumbnailUrl())
                .assignedZoneName(app.getAssignedSeatZone().getName())
                .zonePrice(app.getAssignedSeatZone().getPrice())
                .seats(assignedSeats.stream()
                        .map(AssignedSeatInfo::from)
                        .toList())
                .build();
    }
}
