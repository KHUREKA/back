package com.khureka.server.ticket.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EventHomeResponse {
    private List<EventSummaryResponse> nearbyEvents;
    private List<EventSummaryResponse> recommendedEvents;
}
