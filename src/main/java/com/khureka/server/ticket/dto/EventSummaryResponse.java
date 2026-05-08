package com.khureka.server.ticket.dto;

import com.khureka.server.domain.EventCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventSummaryResponse {
    private Long id;
    private String title;
    private String venueName;
    private String dateRange; // 예: "2026.05.16 - 06.13"
    private EventCategory category;
    private String thumbnailUrl;
    private Double distance; // 단위: km
    private String distanceDisplay; // 예: "0.8km"
}
