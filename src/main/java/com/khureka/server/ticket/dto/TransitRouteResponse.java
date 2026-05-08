package com.khureka.server.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransitRouteResponse {
    private Long eventId;
    private String eventTitle;
    private String venueName;
    private String venueAddress;

    private Double startLatitude;
    private Double startLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    /** 총 소요시간 (분 단위) */
    private Integer totalTime;
    /** 예상 요금 */
    private Integer payment;
    /** 환승 횟수 */
    private Integer transferCount;
    /** 총 도보 거리 (m) */
    private Integer totalWalk;
    /** 총 이동 거리 (m) */
    private Integer totalDistance;
    private String firstStation;
    private String lastStation;
    private LocalDateTime recommendedDepartureTime;
    private String summaryMessage;

    private List<TransitSegmentResponse> segments;
    private TransitAccessibilityGuideResponse accessibilityGuide;

    /** TMAP 또는 MOCK */
    private String provider;
}
