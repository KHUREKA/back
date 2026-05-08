package com.khureka.server.external.tmap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapItinerary {
    private TmapFare fare;
    private Integer totalTime;
    private Integer transferCount;
    private Integer totalWalkDistance;
    private Integer totalDistance;
    private Integer totalWalkTime;
    private Integer pathType;
    private List<TmapLeg> legs;
}
