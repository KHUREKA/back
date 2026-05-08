package com.khureka.server.external.tmap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapLeg {
    private String mode;
    private Integer sectionTime;
    private Integer distance;
    private String route;
    private String routeColor;
    private Integer type;
    private Integer service;
    private TmapPoint start;
    private TmapPoint end;
    private List<TmapStep> steps;
    private TmapPassStopList passStopList;
}
