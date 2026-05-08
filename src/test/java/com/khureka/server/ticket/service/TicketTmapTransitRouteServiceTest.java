package com.khureka.server.ticket.service;

import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.EventCategory;
import com.khureka.server.domain.TicketEvent;
import com.khureka.server.external.tmap.MockTmapTransitRouteProvider;
import com.khureka.server.external.tmap.TmapProperties;
import com.khureka.server.external.tmap.TmapRouteMapper;
import com.khureka.server.external.tmap.TmapTransitClient;
import com.khureka.server.external.tmap.dto.TmapFare;
import com.khureka.server.external.tmap.dto.TmapItinerary;
import com.khureka.server.external.tmap.dto.TmapLeg;
import com.khureka.server.external.tmap.dto.TmapMetaData;
import com.khureka.server.external.tmap.dto.TmapPlan;
import com.khureka.server.external.tmap.dto.TmapPoint;
import com.khureka.server.external.tmap.dto.TmapRegularFare;
import com.khureka.server.external.tmap.dto.TmapTransitResponse;
import com.khureka.server.ticket.dto.TransitRouteResponse;
import com.khureka.server.ticket.repository.TicketEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketTmapTransitRouteServiceTest {

    @Mock
    private TicketEventRepository ticketEventRepository;
    @Mock
    private TmapTransitClient tmapTransitClient;
    @Mock
    private TmapRouteMapper tmapRouteMapper;

    private TmapProperties tmapProperties;
    private MockTmapTransitRouteProvider mockProvider;
    private TicketTmapTransitRouteService service;

    @BeforeEach
    void setUp() {
        tmapProperties = new TmapProperties();
        mockProvider = new MockTmapTransitRouteProvider();
        service = new TicketTmapTransitRouteService(
                ticketEventRepository, tmapProperties, tmapTransitClient, tmapRouteMapper, mockProvider
        );
    }

    @Test
    void TMAP_ENABLED가_false이면_mock_fallback이_반환된다() {
        tmapProperties.setEnabled(false);
        tmapProperties.setApiKey("any-key");

        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));

        TransitRouteResponse response = service.getTmapTransitRoute(1L, 37.2447, 127.0530);

        assertThat(response.getProvider()).isEqualTo("MOCK");
    }

    @Test
    void 정상_좌표이면_TransitRouteResponse가_생성된다() {
        tmapProperties.setEnabled(false);
        tmapProperties.setApiKey("");

        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));

        TransitRouteResponse response = service.getTmapTransitRoute(1L, 37.2447, 127.0530);

        assertNotNull(response);
        assertThat(response.getEventId()).isEqualTo(1L);
    }

    @Test
    void userLat가_마이너스90_미만이면_예외() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getTmapTransitRoute(1L, -90.1, 127.0));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_LOCATION_RANGE);
    }

    @Test
    void userLat가_90_초과이면_예외() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getTmapTransitRoute(1L, 90.1, 127.0));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_LOCATION_RANGE);
    }

    @Test
    void userLng가_마이너스180_미만이면_예외() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getTmapTransitRoute(1L, 37.2, -180.1));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_LOCATION_RANGE);
    }

    @Test
    void userLng가_180_초과이면_예외() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getTmapTransitRoute(1L, 37.2, 180.1));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_LOCATION_RANGE);
    }

    @Test
    void destinationLatitude가_null이면_예외() {
        tmapProperties.setEnabled(false);

        TicketEvent event = buildEvent(null, 127.0730, "KSPO DOME");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getTmapTransitRoute(1L, 37.2447, 127.0530));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EVENT_DESTINATION_LOCATION_NOT_FOUND);
    }

    @Test
    void destinationLongitude가_null이면_예외() {
        tmapProperties.setEnabled(false);

        TicketEvent event = buildEvent(37.5150, null, "KSPO DOME");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getTmapTransitRoute(1L, 37.2447, 127.0530));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EVENT_DESTINATION_LOCATION_NOT_FOUND);
    }

    @Test
    void Tmap_호출시_좌표_순서가_startX는경도_startY는위도다() {
        tmapProperties.setEnabled(true);
        tmapProperties.setApiKey("test-api-key");

        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));
        given(tmapTransitClient.searchTransitRoute(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(buildTmapResponse());
        given(tmapRouteMapper.toResponse(any(), eq(event), eq(37.2447), eq(127.0530)))
                .willReturn(buildMockTransitRouteResponse(event));

        service.getTmapTransitRoute(1L, 37.2447, 127.0530);

        // startX=userLng(경도), startY=userLat(위도), endX=destinationLng(경도), endY=destinationLat(위도)
        verify(tmapTransitClient).searchTransitRoute(127.0530, 37.2447, 127.0730, 37.5150);
    }

    @Test
    void Tmap_totalTime_986초는_분단위로_올림변환하면_17분이다() {
        TmapRouteMapper realMapper = new TmapRouteMapper();
        TmapItinerary itinerary = buildItinerary(986, 1850, 1, 850, 15000);
        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");

        TransitRouteResponse response = realMapper.toResponse(itinerary, event, 37.2447, 127.0530);

        // ceil(986 / 60.0) = ceil(16.433) = 17
        assertThat(response.getTotalTime()).isEqualTo(17);
    }

    @Test
    void mode_WALK가_도보로_변환된다() {
        TmapRouteMapper realMapper = new TmapRouteMapper();
        TmapLeg leg = buildLeg("WALK", 300, 300, null, null);
        TmapItinerary itinerary = buildItineraryWithLegs(List.of(leg));
        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");

        TransitRouteResponse response = realMapper.toResponse(itinerary, event, 37.2447, 127.0530);

        assertThat(response.getSegments().get(0).getMode()).isEqualTo("도보");
    }

    @Test
    void mode_BUS가_버스로_변환된다() {
        TmapRouteMapper realMapper = new TmapRouteMapper();
        TmapLeg leg = buildLeg("BUS", 2100, 12000, "5100", "0068B7");
        TmapItinerary itinerary = buildItineraryWithLegs(List.of(leg));
        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");

        TransitRouteResponse response = realMapper.toResponse(itinerary, event, 37.2447, 127.0530);

        assertThat(response.getSegments().get(0).getMode()).isEqualTo("버스");
    }

    @Test
    void mode_SUBWAY가_지하철로_변환된다() {
        TmapRouteMapper realMapper = new TmapRouteMapper();
        TmapLeg leg = buildLeg("SUBWAY", 1500, 7000, "수도권 9호선", "BDB092");
        TmapItinerary itinerary = buildItineraryWithLegs(List.of(leg));
        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");

        TransitRouteResponse response = realMapper.toResponse(itinerary, event, 37.2447, 127.0530);

        assertThat(response.getSegments().get(0).getMode()).isEqualTo("지하철");
    }

    @Test
    void legs가_null이어도_NPE가_발생하지_않는다() {
        TmapRouteMapper realMapper = new TmapRouteMapper();
        TmapItinerary itinerary = buildItineraryWithLegs(null);
        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");

        assertDoesNotThrow(() -> realMapper.toResponse(itinerary, event, 37.2447, 127.0530));
    }

    @Test
    void routeColor가_있으면_색상에_샵이_붙어서_변환된다() {
        TmapRouteMapper realMapper = new TmapRouteMapper();
        TmapLeg leg = buildLeg("SUBWAY", 1500, 7000, "수도권 9호선", "BDB092");
        TmapItinerary itinerary = buildItineraryWithLegs(List.of(leg));
        TicketEvent event = buildEvent(37.5150, 127.0730, "KSPO DOME");

        TransitRouteResponse response = realMapper.toResponse(itinerary, event, 37.2447, 127.0530);

        assertThat(response.getSegments().get(0).getColor()).isEqualTo("#BDB092");
    }

    // ===== helpers =====

    private TicketEvent buildEvent(Double lat, Double lng, String venueName) {
        TicketEvent event = TicketEvent.builder()
                .title("임영웅 전국투어 콘서트 2026")
                .category(EventCategory.CONCERT)
                .keyword("임영웅 콘서트")
                .venueName(venueName)
                .venueAddress("서울특별시 송파구 올림픽로 424")
                .destinationLatitude(lat)
                .destinationLongitude(lng)
                .description("desc")
                .thumbnailUrl("thumb")
                .build();
        ReflectionTestUtils.setField(event, "id", 1L);
        return event;
    }

    private TmapTransitResponse buildTmapResponse() {
        TmapPoint start = new TmapPoint();
        ReflectionTestUtils.setField(start, "name", "출발지");
        ReflectionTestUtils.setField(start, "lon", 127.0530);
        ReflectionTestUtils.setField(start, "lat", 37.2447);

        TmapPoint end = new TmapPoint();
        ReflectionTestUtils.setField(end, "name", "KSPO DOME");
        ReflectionTestUtils.setField(end, "lon", 127.0730);
        ReflectionTestUtils.setField(end, "lat", 37.5150);

        TmapLeg leg = new TmapLeg();
        ReflectionTestUtils.setField(leg, "mode", "WALK");
        ReflectionTestUtils.setField(leg, "sectionTime", 300);
        ReflectionTestUtils.setField(leg, "distance", 300);
        ReflectionTestUtils.setField(leg, "start", start);
        ReflectionTestUtils.setField(leg, "end", end);

        TmapRegularFare regularFare = new TmapRegularFare();
        ReflectionTestUtils.setField(regularFare, "totalFare", 1850);

        TmapFare fare = new TmapFare();
        ReflectionTestUtils.setField(fare, "regular", regularFare);

        TmapItinerary itinerary = new TmapItinerary();
        ReflectionTestUtils.setField(itinerary, "totalTime", 4320);
        ReflectionTestUtils.setField(itinerary, "transferCount", 1);
        ReflectionTestUtils.setField(itinerary, "totalWalkDistance", 850);
        ReflectionTestUtils.setField(itinerary, "totalDistance", 15000);
        ReflectionTestUtils.setField(itinerary, "fare", fare);
        ReflectionTestUtils.setField(itinerary, "legs", List.of(leg));

        TmapPlan plan = new TmapPlan();
        ReflectionTestUtils.setField(plan, "itineraries", List.of(itinerary));

        TmapMetaData metaData = new TmapMetaData();
        ReflectionTestUtils.setField(metaData, "plan", plan);

        TmapTransitResponse response = new TmapTransitResponse();
        ReflectionTestUtils.setField(response, "metaData", metaData);
        return response;
    }

    private TmapItinerary buildItinerary(int totalTime, int fare, int transferCount, int totalWalk, int totalDistance) {
        TmapRegularFare regularFare = new TmapRegularFare();
        ReflectionTestUtils.setField(regularFare, "totalFare", fare);

        TmapFare tmapFare = new TmapFare();
        ReflectionTestUtils.setField(tmapFare, "regular", regularFare);

        TmapItinerary itinerary = new TmapItinerary();
        ReflectionTestUtils.setField(itinerary, "totalTime", totalTime);
        ReflectionTestUtils.setField(itinerary, "fare", tmapFare);
        ReflectionTestUtils.setField(itinerary, "transferCount", transferCount);
        ReflectionTestUtils.setField(itinerary, "totalWalkDistance", totalWalk);
        ReflectionTestUtils.setField(itinerary, "totalDistance", totalDistance);
        ReflectionTestUtils.setField(itinerary, "legs", List.of());
        return itinerary;
    }

    private TmapItinerary buildItineraryWithLegs(List<TmapLeg> legs) {
        TmapRegularFare regularFare = new TmapRegularFare();
        ReflectionTestUtils.setField(regularFare, "totalFare", 1850);

        TmapFare tmapFare = new TmapFare();
        ReflectionTestUtils.setField(tmapFare, "regular", regularFare);

        TmapItinerary itinerary = new TmapItinerary();
        ReflectionTestUtils.setField(itinerary, "totalTime", 4320);
        ReflectionTestUtils.setField(itinerary, "fare", tmapFare);
        ReflectionTestUtils.setField(itinerary, "transferCount", 1);
        ReflectionTestUtils.setField(itinerary, "totalWalkDistance", 850);
        ReflectionTestUtils.setField(itinerary, "totalDistance", 15000);
        ReflectionTestUtils.setField(itinerary, "legs", legs);
        return itinerary;
    }

    private TmapLeg buildLeg(String mode, int sectionTime, int distance, String route, String routeColor) {
        TmapPoint start = new TmapPoint();
        ReflectionTestUtils.setField(start, "name", "출발지");

        TmapPoint end = new TmapPoint();
        ReflectionTestUtils.setField(end, "name", "도착지");

        TmapLeg leg = new TmapLeg();
        ReflectionTestUtils.setField(leg, "mode", mode);
        ReflectionTestUtils.setField(leg, "sectionTime", sectionTime);
        ReflectionTestUtils.setField(leg, "distance", distance);
        ReflectionTestUtils.setField(leg, "route", route);
        ReflectionTestUtils.setField(leg, "routeColor", routeColor);
        ReflectionTestUtils.setField(leg, "start", start);
        ReflectionTestUtils.setField(leg, "end", end);
        return leg;
    }

    private TransitRouteResponse buildMockTransitRouteResponse(TicketEvent event) {
        return TransitRouteResponse.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .venueName(event.getVenueName())
                .venueAddress(event.getVenueAddress())
                .startLatitude(37.2447)
                .startLongitude(127.0530)
                .destinationLatitude(event.getDestinationLatitude())
                .destinationLongitude(event.getDestinationLongitude())
                .totalTime(72)
                .payment(1850)
                .transferCount(1)
                .totalWalk(850)
                .totalDistance(15000)
                .firstStation("현재 위치")
                .lastStation(event.getVenueName())
                .summaryMessage("약 72분 소요됩니다.")
                .provider("TMAP")
                .build();
    }
}
