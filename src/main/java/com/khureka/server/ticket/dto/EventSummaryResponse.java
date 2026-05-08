package com.khureka.server.ticket.dto;

import com.khureka.server.domain.EventCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventSummaryResponse {
    private Long id;
    private String title;
    private String venueName;
    private String dateRange;
    private String representativeDate;
    private EventCategory category;
    private String thumbnailUrl;
    private Double distance;
    private String distanceDisplay;
    private Integer minPrice;
    private Integer maxPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EventSummaryResponse that = (EventSummaryResponse) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
