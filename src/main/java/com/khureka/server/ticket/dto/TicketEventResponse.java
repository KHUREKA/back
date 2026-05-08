package com.khureka.server.ticket.dto;

import com.khureka.server.domain.EventCategory;
import com.khureka.server.domain.TicketEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketEventResponse {

    private Long id;
    private String title;
    private EventCategory category;
    private String keyword;
    private String venueName;
    private String venueAddress;
    private String description;
    private String thumbnailUrl;

    public static TicketEventResponse from(TicketEvent event) {
        return TicketEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .category(event.getCategory())
                .keyword(event.getKeyword())
                .venueName(event.getVenueName())
                .venueAddress(event.getVenueAddress())
                .description(event.getDescription())
                .thumbnailUrl(event.getThumbnailUrl())
                .build();
    }
}
