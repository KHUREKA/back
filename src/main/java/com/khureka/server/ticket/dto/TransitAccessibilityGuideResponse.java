package com.khureka.server.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransitAccessibilityGuideResponse {
    private String nearestStation;
    private String recommendedExit;
    private String caution;
}
