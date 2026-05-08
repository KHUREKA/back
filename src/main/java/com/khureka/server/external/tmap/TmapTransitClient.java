package com.khureka.server.external.tmap;

import com.khureka.server.external.tmap.dto.TmapTransitRequest;
import com.khureka.server.external.tmap.dto.TmapTransitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmapTransitClient {

    private static final String TRANSIT_ROUTE_PATH = "/transit/routes";

    private final RestTemplate restTemplate;
    private final TmapProperties tmapProperties;

    /**
     * Tmap 대중교통 경로탐색 API 호출.
     *
     * @param startX 출발지 경도
     * @param startY 출발지 위도
     * @param endX   목적지 경도
     * @param endY   목적지 위도
     */
    public TmapTransitResponse searchTransitRoute(double startX, double startY, double endX, double endY) {
        TmapTransitRequest request = TmapTransitRequest.builder()
                .startX(String.valueOf(startX))
                .startY(String.valueOf(startY))
                .endX(String.valueOf(endX))
                .endY(String.valueOf(endY))
                .count(1)
                .lang(0)
                .format("json")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        headers.set("appKey", tmapProperties.getApiKey());

        HttpEntity<TmapTransitRequest> entity = new HttpEntity<>(request, headers);
        String url = tmapProperties.getBaseUrl() + TRANSIT_ROUTE_PATH;

        log.info("[TmapTransitClient] POST {} startX={} startY={} endX={} endY={}", url, startX, startY, endX, endY);

        return restTemplate.postForObject(url, entity, TmapTransitResponse.class);
    }
}
