package com.khureka.server.ticket.service;

import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.TicketEvent;
import com.khureka.server.external.tmap.MockTmapTransitRouteProvider;
import com.khureka.server.external.tmap.TmapProperties;
import com.khureka.server.external.tmap.TmapRouteMapper;
import com.khureka.server.external.tmap.TmapTransitClient;
import com.khureka.server.external.tmap.dto.TmapItinerary;
import com.khureka.server.external.tmap.dto.TmapTransitResponse;
import com.khureka.server.ticket.dto.TransitRouteResponse;
import com.khureka.server.ticket.repository.TicketEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketTmapTransitRouteService {

    private final TicketEventRepository ticketEventRepository;
    private final TmapProperties tmapProperties;
    private final TmapTransitClient tmapTransitClient;
    private final TmapRouteMapper tmapRouteMapper;
    private final MockTmapTransitRouteProvider mockTmapTransitRouteProvider;

    public TransitRouteResponse getTmapTransitRoute(Long eventId, double userLat, double userLng) {
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

        if (!tmapProperties.isEnabled() || isApiKeyEmpty()) {
            log.info("[TmapService] fallback to mock: enabled={}, hasApiKey={}", tmapProperties.isEnabled(), !isApiKeyEmpty());
            return mockTmapTransitRouteProvider.provide(event, userLat, userLng);
        }

        try {
            // 좌표 순서 주의: startX=경도(lng), startY=위도(lat), endX=경도(lng), endY=위도(lat)
            TmapTransitResponse tmapResponse = tmapTransitClient.searchTransitRoute(
                    userLng,        // startX = 출발지 경도
                    userLat,        // startY = 출발지 위도
                    destinationLng, // endX   = 목적지 경도
                    destinationLat  // endY   = 목적지 위도
            );

            if (tmapResponse == null
                    || tmapResponse.getMetaData() == null
                    || tmapResponse.getMetaData().getPlan() == null) {
                log.warn("[TmapService] Tmap 응답 없음 → mock fallback");
                return mockTmapTransitRouteProvider.provide(event, userLat, userLng);
            }

            List<TmapItinerary> itineraries = tmapResponse.getMetaData().getPlan().getItineraries();
            if (itineraries == null || itineraries.isEmpty()) {
                log.warn("[TmapService] itineraries 없음 → mock fallback");
                return mockTmapTransitRouteProvider.provide(event, userLat, userLng);
            }

            return tmapRouteMapper.toResponse(itineraries.get(0), event, userLat, userLng);

        } catch (Exception e) {
            log.warn("[TmapService] Tmap API 호출 실패 → mock fallback. reason={}", e.getMessage());
            return mockTmapTransitRouteProvider.provide(event, userLat, userLng);
        }
    }

    private boolean isApiKeyEmpty() {
        String key = tmapProperties.getApiKey();
        return key == null || key.isBlank();
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
