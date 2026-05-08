package com.khureka.server.ticket.controller;

import com.khureka.server.ticket.dto.KakaoRouteResponse;
import com.khureka.server.ticket.dto.TransitRouteResponse;
import com.khureka.server.ticket.service.TicketRouteService;
import com.khureka.server.ticket.service.TicketTmapTransitRouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketRouteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.khureka.server.common.exception.GlobalExceptionHandler.class)
class TicketRouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketRouteService ticketRouteService;

    @MockBean
    private TicketTmapTransitRouteService ticketTmapTransitRouteService;

    @Test
    void 카카오_라우트_API_호출시_응답_필드가_반환된다() throws Exception {
        KakaoRouteResponse response = KakaoRouteResponse.builder()
                .eventId(1L)
                .eventTitle("임영웅 전국투어 콘서트 2026")
                .venueName("KSPO DOME")
                .venueAddress("서울특별시 송파구 올림픽로 424")
                .startLatitude(37.2447)
                .startLongitude(127.0530)
                .destinationLatitude(37.5150176)
                .destinationLongitude(127.0729773)
                .kakaoMapUrl("https://map.kakao.com/link/from/a,37.2447,127.0530/to/b,37.5150176,127.0729773")
                .kakaoMapTransitUrl("https://map.kakao.com/link/by/traffic/a,37.2447,127.0530/b,37.5150176,127.0729773")
                .kakaoMapCarUrl("https://map.kakao.com/link/by/car/a,37.2447,127.0530/b,37.5150176,127.0729773")
                .kakaoMapWalkUrl("https://map.kakao.com/link/by/walk/a,37.2447,127.0530/b,37.5150176,127.0729773")
                .build();

        given(ticketRouteService.getKakaoRoute(1L, 37.2447, 127.0530)).willReturn(response);

        mockMvc.perform(get("/api/v1/ticket-events/1/kakao-route")
                        .queryParam("userLat", "37.2447")
                        .queryParam("userLng", "127.0530"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.kakaoMapUrl").exists())
                .andExpect(jsonPath("$.data.kakaoMapTransitUrl").exists())
                .andExpect(jsonPath("$.data.kakaoMapCarUrl").exists())
                .andExpect(jsonPath("$.data.kakaoMapWalkUrl").exists())
                .andExpect(jsonPath("$.data.eventTitle").exists())
                .andExpect(jsonPath("$.data.venueName").exists());
    }

    @Test
    void Tmap_대중교통_경로_API_호출시_200_OK와_필수_필드가_반환된다() throws Exception {
        TransitRouteResponse response = TransitRouteResponse.builder()
                .eventId(1L)
                .eventTitle("임영웅 전국투어 콘서트 2026")
                .venueName("KSPO DOME")
                .venueAddress("서울특별시 송파구 올림픽로 424")
                .startLatitude(37.2447)
                .startLongitude(127.0530)
                .destinationLatitude(37.5150)
                .destinationLongitude(127.0730)
                .totalTime(72)
                .payment(1850)
                .transferCount(1)
                .totalWalk(850)
                .totalDistance(15000)
                .firstStation("현재 위치")
                .lastStation("KSPO DOME")
                .summaryMessage("약 72분 소요됩니다. 공연장 도착 전 여유 있게 출발하세요.")
                .segments(List.of())
                .provider("MOCK")
                .build();

        given(ticketTmapTransitRouteService.getTmapTransitRoute(1L, 37.2447, 127.0530)).willReturn(response);

        mockMvc.perform(get("/api/v1/ticket-events/1/tmap-transit-route")
                        .queryParam("userLat", "37.2447")
                        .queryParam("userLng", "127.0530"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.eventTitle").exists())
                .andExpect(jsonPath("$.data.venueName").exists())
                .andExpect(jsonPath("$.data.totalTime").exists())
                .andExpect(jsonPath("$.data.payment").exists())
                .andExpect(jsonPath("$.data.transferCount").exists())
                .andExpect(jsonPath("$.data.segments").isArray())
                .andExpect(jsonPath("$.data.provider").exists());
    }
}
