package com.khureka.server.ticket.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoRouteResponse {

    private Long eventId;
    private String eventTitle;
    private String venueName;
    private String venueAddress;

    private Double startLatitude;
    private Double startLongitude;

    private Double destinationLatitude;
    private Double destinationLongitude;

    private String kakaoMapUrl;
    private String kakaoMapTransitUrl;
    private String kakaoMapCarUrl;
    private String kakaoMapWalkUrl;
}
