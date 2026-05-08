package com.khureka.server.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransitSegmentResponse {
    private Integer order;
    private String mode;
    private Integer sectionTime;
    private Integer distance;
    private String startName;
    private String endName;
    private String displayName;
    private String color;

    // 버스 전용
    private String busLabel;
    private List<String> busNumbers;

    // 지하철 전용
    private String subwayLineName;
    private String way;
    private String exitNo;
    private Integer stationCount;
}
