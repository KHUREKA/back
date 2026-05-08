package com.khureka.server.ticket.dto;

import com.khureka.server.domain.SeatZone;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatZoneResponse {

    private Long id;
    private Long scheduleId;
    private String name;
    private Integer price;
    private Long availableSeats;

    public static SeatZoneResponse from(SeatZone zone, long availableSeats) {
        return SeatZoneResponse.builder()
                .id(zone.getId())
                .scheduleId(zone.getSchedule().getId())
                .name(zone.getName())
                .price(zone.getPrice())
                .availableSeats(availableSeats)
                .build();
    }
}
