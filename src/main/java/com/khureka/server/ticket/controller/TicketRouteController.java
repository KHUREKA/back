package com.khureka.server.ticket.controller;

import com.khureka.server.common.response.ApiResponse;
import com.khureka.server.ticket.dto.KakaoRouteResponse;
import com.khureka.server.ticket.dto.TransitRouteResponse;
import com.khureka.server.ticket.service.TicketRouteService;
import com.khureka.server.ticket.service.TicketTmapTransitRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ticket Route", description = "공연장 길찾기 API")
@RestController
@RequestMapping("/api/v1/ticket-events")
@RequiredArgsConstructor
public class TicketRouteController {

    private final TicketRouteService ticketRouteService;
    private final TicketTmapTransitRouteService ticketTmapTransitRouteService;

    @Operation(summary = "카카오맵 길찾기 링크 생성", description = "사용자의 현재 위치와 공연장 좌표를 이용해 카카오맵 길찾기 링크를 생성합니다. 출발지는 userLat/userLng이며, 목적지는 TicketEvent의 destinationLatitude/destinationLongitude입니다.")
    @GetMapping("/{eventId}/kakao-route")
    public ApiResponse<KakaoRouteResponse> getKakaoRoute(
            @Parameter(description = "공연/경기 ID", required = true) @PathVariable Long eventId,
            @Parameter(description = "사용자 현재 위도", required = true) @RequestParam double userLat,
            @Parameter(description = "사용자 현재 경도", required = true) @RequestParam double userLng) {
        return ApiResponse.success(ticketRouteService.getKakaoRoute(eventId, userLat, userLng));
    }

    @Operation(
            summary = "Tmap 대중교통 경로 조회",
            description = "사용자의 현재 위치와 공연장 좌표를 이용해 Tmap 대중교통 경로 요약 정보를 조회합니다. " +
                    "출발지는 userLat/userLng이며, 목적지는 TicketEvent의 destinationLatitude/destinationLongitude입니다. " +
                    "Tmap 호출 실패 또는 비활성화 시 mock 경로를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "대중교통 경로 조회 성공. provider=TMAP(실제) 또는 MOCK(fallback)",
                    content = @Content(schema = @Schema(implementation = TransitRouteResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "좌표 범위 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공연/경기 없음 또는 목적지 좌표 없음")
    })
    @GetMapping("/{eventId}/tmap-transit-route")
    public ApiResponse<TransitRouteResponse> getTmapTransitRoute(
            @Parameter(description = "공연/경기 ID", required = true) @PathVariable Long eventId,
            @Parameter(description = "사용자 현재 위도 (-90 ~ 90)", required = true) @RequestParam double userLat,
            @Parameter(description = "사용자 현재 경도 (-180 ~ 180)", required = true) @RequestParam double userLng
    ) {
        return ApiResponse.success(ticketTmapTransitRouteService.getTmapTransitRoute(eventId, userLat, userLng));
    }
}
