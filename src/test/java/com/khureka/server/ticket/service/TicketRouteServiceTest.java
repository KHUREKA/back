package com.khureka.server.ticket.service;

import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.EventCategory;
import com.khureka.server.domain.TicketEvent;
import com.khureka.server.ticket.dto.KakaoRouteResponse;
import com.khureka.server.ticket.repository.TicketEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TicketRouteServiceTest {

    @Mock
    private TicketEventRepository ticketEventRepository;

    private TicketRouteService ticketRouteService;

    @BeforeEach
    void setUp() {
        ticketRouteService = new TicketRouteService(ticketEventRepository);
    }

    @Test
    void 정상_좌표면_길찾기_URL이_생성된다() {
        TicketEvent event = buildEvent(37.5150176, 127.0729773, "KSPO DOME");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));

        KakaoRouteResponse response = ticketRouteService.getKakaoRoute(1L, 37.2447, 127.0530);

        assertNotNull(response.getKakaoMapTransitUrl());
        assertTrue(response.getKakaoMapTransitUrl().contains("/by/traffic/"));
    }

    @Test
    void userLat가_마이너스90_미만이면_예외() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketRouteService.getKakaoRoute(1L, -90.1, 127.0));

        assertEquals(ErrorCode.INVALID_LOCATION_RANGE, ex.getErrorCode());
    }

    @Test
    void userLat가_90_초과이면_예외() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketRouteService.getKakaoRoute(1L, 90.1, 127.0));

        assertEquals(ErrorCode.INVALID_LOCATION_RANGE, ex.getErrorCode());
    }

    @Test
    void userLng가_마이너스180_미만이면_예외() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketRouteService.getKakaoRoute(1L, 37.2, -180.1));

        assertEquals(ErrorCode.INVALID_LOCATION_RANGE, ex.getErrorCode());
    }

    @Test
    void userLng가_180_초과이면_예외() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketRouteService.getKakaoRoute(1L, 37.2, 180.1));

        assertEquals(ErrorCode.INVALID_LOCATION_RANGE, ex.getErrorCode());
    }

    @Test
    void 목적지_좌표가_null이면_예외() {
        TicketEvent event = buildEvent(null, 127.0729773, "KSPO DOME");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketRouteService.getKakaoRoute(1L, 37.2447, 127.0530));

        assertEquals(ErrorCode.EVENT_DESTINATION_LOCATION_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 한글_목적지명이_URL_인코딩된다() {
        TicketEvent event = buildEvent(37.5665, 126.9780, "서울 월드컵 경기장");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));

        KakaoRouteResponse response = ticketRouteService.getKakaoRoute(1L, 37.2447, 127.0530);

        assertTrue(response.getKakaoMapUrl().contains("%EC%84%9C%EC%9A%B8"));
    }

    @Test
    void 링크_좌표_순서가_위도경도다() {
        TicketEvent event = buildEvent(37.5150176, 127.0729773, "KSPO DOME");
        given(ticketEventRepository.findById(1L)).willReturn(Optional.of(event));

        KakaoRouteResponse response = ticketRouteService.getKakaoRoute(1L, 37.2447, 127.0530);

        assertTrue(response.getKakaoMapCarUrl().contains("37.2447,127.053"));
        assertTrue(response.getKakaoMapCarUrl().contains("37.5150176,127.0729773"));
    }

    private TicketEvent buildEvent(Double destinationLatitude, Double destinationLongitude, String venueName) {
        TicketEvent event = TicketEvent.builder()
                .title("임영웅 전국투어 콘서트 2026")
                .category(EventCategory.CONCERT)
                .keyword("임영웅 콘서트")
                .venueName(venueName)
                .venueAddress("서울특별시 송파구 올림픽로 424")
                .destinationLatitude(destinationLatitude)
                .destinationLongitude(destinationLongitude)
                .description("desc")
                .thumbnailUrl("thumb")
                .build();
        ReflectionTestUtils.setField(event, "id", 1L);
        return event;
    }
}
