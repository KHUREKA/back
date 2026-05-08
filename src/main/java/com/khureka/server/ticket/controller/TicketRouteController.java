package com.khureka.server.ticket.controller;

import com.khureka.server.common.response.ApiResponse;
import com.khureka.server.ticket.dto.KakaoRouteResponse;
import com.khureka.server.ticket.service.TicketRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ticket Route", description = "공연장 길찾기 링크 생성 API")
@RestController
@RequestMapping("/api/v1/ticket-events")
@RequiredArgsConstructor
public class TicketRouteController {

    private final TicketRouteService ticketRouteService;

    @Operation(
            summary = "카카오맵 길찾기 링크 생성",
            description = "사용자의 현재 위치와 공연장 좌표를 이용해 카카오맵 길찾기 링크를 생성합니다. 출발지는 userLat/userLng이며, 목적지는 TicketEvent의 destinationLatitude/destinationLongitude입니다."
    )
    @GetMapping("/{eventId}/kakao-route")
    public ApiResponse<KakaoRouteResponse> getKakaoRoute(
            @Parameter(description = "공연/경기 ID", required = true)
            @PathVariable Long eventId,
            @Parameter(description = "사용자 현재 위도", required = true)
            @RequestParam double userLat,
            @Parameter(description = "사용자 현재 경도", required = true)
            @RequestParam double userLng
    ) {
        return ApiResponse.success(ticketRouteService.getKakaoRoute(eventId, userLat, userLng));
    }
}
