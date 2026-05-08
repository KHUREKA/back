package com.khureka.server.ticket.service;

import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.TicketEvent;
import com.khureka.server.ticket.dto.KakaoRouteResponse;
import com.khureka.server.ticket.repository.TicketEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketRouteService {

    private static final String START_NAME = "현재 위치";
    private static final String BASE_URL = "https://map.kakao.com/link";

    private final TicketEventRepository ticketEventRepository;

    public KakaoRouteResponse getKakaoRoute(Long eventId, double userLat, double userLng) {
        validateLatitude(userLat, "userLat");
        validateLongitude(userLng, "userLng");

        TicketEvent event = ticketEventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        Double destinationLat = event.getDestinationLatitude();
        Double destinationLng = event.getDestinationLongitude();
        if (destinationLat == null || destinationLng == null) {
            throw new BusinessException(ErrorCode.EVENT_DESTINATION_LOCATION_NOT_FOUND);
        }

        validateLatitude(destinationLat, "destinationLatitude");
        validateLongitude(destinationLng, "destinationLongitude");

        String startNameEncoded = UriUtils.encodePathSegment(START_NAME, StandardCharsets.UTF_8);
        String destinationName = event.getVenueName();
        String destinationNameEncoded = UriUtils.encodePathSegment(destinationName, StandardCharsets.UTF_8);

        String startPart = startNameEncoded + "," + userLat + "," + userLng;
        String destinationPart = destinationNameEncoded + "," + destinationLat + "," + destinationLng;

        String kakaoMapUrl = BASE_URL + "/from/" + startPart + "/to/" + destinationPart;
        String kakaoMapTransitUrl = BASE_URL + "/by/traffic/" + startPart + "/" + destinationPart;
        String kakaoMapCarUrl = BASE_URL + "/by/car/" + startPart + "/" + destinationPart;
        String kakaoMapWalkUrl = BASE_URL + "/by/walk/" + startPart + "/" + destinationPart;

        return KakaoRouteResponse.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .venueName(event.getVenueName())
                .venueAddress(event.getVenueAddress())
                .startLatitude(userLat)
                .startLongitude(userLng)
                .destinationLatitude(destinationLat)
                .destinationLongitude(destinationLng)
                .kakaoMapUrl(kakaoMapUrl)
                .kakaoMapTransitUrl(kakaoMapTransitUrl)
                .kakaoMapCarUrl(kakaoMapCarUrl)
                .kakaoMapWalkUrl(kakaoMapWalkUrl)
                .build();
    }

    private void validateLatitude(double latitude, String fieldName) {
        if (latitude < -90 || latitude > 90) {
            throw new BusinessException(
                    ErrorCode.INVALID_LOCATION_RANGE,
                    fieldName + "는 -90 이상 90 이하여야 합니다."
            );
        }
    }

    private void validateLongitude(double longitude, String fieldName) {
        if (longitude < -180 || longitude > 180) {
            throw new BusinessException(
                    ErrorCode.INVALID_LOCATION_RANGE,
                    fieldName + "는 -180 이상 180 이하여야 합니다."
            );
        }
    }
}
