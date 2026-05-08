package com.khureka.server.external.tmap;

import com.khureka.server.domain.TicketEvent;
import com.khureka.server.ticket.dto.TransitAccessibilityGuideResponse;
import com.khureka.server.ticket.dto.TransitRouteResponse;
import com.khureka.server.ticket.dto.TransitSegmentResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Tmap API 키가 없거나 호출 실패 시 반환할 Mock 경로 데이터.
 *
 * ── 카카오맵 데이터 입력 방법 ──────────────────────────────────
 * 1. 카카오맵 → 길찾기 → 대중교통 선택
 * 2. 출발지: 원하는 위치, 목적지: 공연장 주소 입력
 * 3. 추천 경로에서 아래 값 확인 후 해당 공연장 메서드에 입력:
 *    - totalTime    : 총 소요시간 (분)
 *    - payment      : 요금 (원)
 *    - transferCount: 환승 횟수
 *    - segments     : 구간별 교통수단, 시간, 역명 순서대로 입력
 * ──────────────────────────────────────────────────────────────
 */
@Component
public class MockTmapTransitRouteProvider {

    public TransitRouteResponse provide(TicketEvent ticketEvent, double userLat, double userLng) {
        String venueName = ticketEvent.getVenueName();

        return switch (venueName) {
            case "고려대학교 화정체육관"   -> hwajungGymnasium(ticketEvent, userLat, userLng);
            case "대구삼성라이온즈파크"     -> daeguBaseballPark(ticketEvent, userLat, userLng);
            case "블루스퀘어 신한카드홀"   -> blueSquare(ticketEvent, userLat, userLng);
            case "인천 송도컨벤시아"       -> songdoConvencia(ticketEvent, userLat, userLng);
            case "명동예술극장"            -> myeongdongTheater(ticketEvent, userLat, userLng);
            case "디큐브 아트센터"         -> dcubeArtCenter(ticketEvent, userLat, userLng);
            default                      -> generic(ticketEvent, userLat, userLng);
        };
    }

    // ──────────────────────────────────────────────────────────
    // 1. 고려대학교 화정체육관 (서울 성북구 안암로 145)
    //    가장 가까운 지하철: 6호선 고려대역 2번 출구 (도보 5분)
    // ──────────────────────────────────────────────────────────
    private TransitRouteResponse hwajungGymnasium(TicketEvent event, double userLat, double userLng) {
        List<TransitSegmentResponse> segments = List.of(
                bus(1, "사색의광장", "경희대학교", 3, "버스", "5100"),
                bus(2, "경희대학교", "국가인권위,안중근센터(중)", 55, "버스", "M5107"),
                bus(3, "국가인권위,안중근센터(중)", "미아리고개,미아리예술극장", 22, "버스", "140"),
                walk(4, "미아리고개,미아리예술극장", "고려대학교 화정체육관",24)

        );

        return build(event, userLat, userLng,
                126, 3700, 2,
                "사색의광장", "고려대학교 화정체육관",
                "고려대역", "2",
                segments);
    }

    // ──────────────────────────────────────────────────────────
    // 2. 대구삼성라이온즈파크 (대구 수성구 야구전설로 1)
    //    가장 가까운 지하철: 대구 2호선 수성구민운동장역 (도보 8분)
    // ──────────────────────────────────────────────────────────
    private TransitRouteResponse daeguBaseballPark(TicketEvent event, double userLat, double userLng) {
        List<TransitSegmentResponse> segments = List.of(
                walk(1, "사색의광장", "서천효성해링턴",24),
                bus(2, "서천효성해링턴", "농서근린공원", 6, "버스", "1560A"),
                bus(3, "신동탄롯데캐슬", "동탄역(서측)", 22, "버스", "H1"),
                train(4, "동탄역", "동대구역", 86, "SRT", "#4B6584"),
                bus(5, "동대구역건너", "수성알파시티역", 26, "버스", "937"),
                walk(6, "수성알파시티역", "대구삼성라이온즈파크",5)


        );

        return build(event, userLat, userLng,
                181, 34300, 2,
                "사색의광장", "대구삼성라이온즈파크",
                "수성구민운동장역", null,
                segments);
    }

    // ──────────────────────────────────────────────────────────
    // 3. 블루스퀘어 신한카드홀 (서울 용산구 이태원로 294)
    //    가장 가까운 지하철: 6호선 한강진역 2번 출구 (도보 5분)
    // ──────────────────────────────────────────────────────────
    private TransitRouteResponse blueSquare(TicketEvent event, double userLat, double userLng) {
        List<TransitSegmentResponse> segments = List.of(
                bus(1, "사색의 광장", "신논현역,금강빌딩", 55, "버스", "5100"),
                bus(2, "지하철2호선강남역(중)", "블루스퀘어 신한카드홀", 14, "버스", "420")

        );

        return build(event, userLat, userLng,
                41, 1450, 0,
                "사색의 광장", "블루스퀘어 신한카드홀",
                "한강진역", "2",
                segments);
    }

    // ──────────────────────────────────────────────────────────
    // 4. 인천 송도컨벤시아 (인천 연수구 센트럴로 123)
    //    가장 가까운 지하철: 인천 1호선 국제업무지구역 (도보 10분)
    // ──────────────────────────────────────────────────────────
    private TransitRouteResponse songdoConvencia(TicketEvent event, double userLat, double userLng) {
        List<TransitSegmentResponse> segments = List.of(
                bus(1, "사색의광장", "살구골현대아파트", 8, "버스" ,"5100"),
                subway(2, "영통역", "원인재역", 69, "수인분당선", "#003DA5"),
                bus(1, "사색의광장", "살구골현대아파트", 8, "버스" ,"5100"),
                walk(3, "국제업무지구역", "인천 송도컨벤시아", 10)
        );

        return build(event, userLat, userLng,
                134, 3800, 0,
                "사색의광장", "인천 송도컨벤시아",
                "국제업무지구역", null,
                segments);
    }

    // ──────────────────────────────────────────────────────────
    // 5. 명동예술극장 (서울 중구 남대문로 2길 12)
    //    가장 가까운 지하철: 4호선 명동역 6번 출구 (도보 3분)
    // ──────────────────────────────────────────────────────────
    private TransitRouteResponse myeongdongTheater(TicketEvent event, double userLat, double userLng) {
        List<TransitSegmentResponse> segments = List.of(
                bus(1, "사색의광장", "사당역(중)", 45, "버스", "5100"),
                subway(2, "사당역", "명동역", 14, "수도권 4호선", "#00A2D6"),
                walk(3, "명동역 6번 출구", "명동예술극장", 3)
        );

        return build(event, userLat, userLng,
                65, 3200, 1,
                "사색의광장", "명동예술극장",
                "명동역", "6",
                segments);
    }

    // ──────────────────────────────────────────────────────────
    // 6. 디큐브 아트센터 (서울 구로구 경인로 662)
    //    가장 가까운 지하철: 1·2호선 신도림역 (도보 5분)
    // ──────────────────────────────────────────────────────────
    private TransitRouteResponse dcubeArtCenter(TicketEvent event, double userLat, double userLng) {
        List<TransitSegmentResponse> segments = List.of(
                bus(1, "사색의광장", "강남역(중)", 55, "버스", "5100"),
                subway(2, "강남역", "신도림역", 12, "수도권 2호선", "#009246"),
                walk(3, "신도림역", "디큐브 아트센터", 5)
        );

        return build(event, userLat, userLng,
                72, 3300, 1,
                "사색의광장", "디큐브 아트센터",
                "신도림역", null,
                segments);
    }

    // ──────────────────────────────────────────────────────────
    // 기본 fallback (새 공연장 추가 시 자동 적용)
    // ──────────────────────────────────────────────────────────
    private TransitRouteResponse generic(TicketEvent event, double userLat, double userLng) {
        List<TransitSegmentResponse> segments = List.of(
                walk(1, "출발지", "인근 정류장", 5),
                bus(2, "인근 정류장", "환승 정류장", 35, "버스", "5100"),
                subway(3, "환승역", event.getVenueName() + " 인근역", 25, "9호선", "#BDB092"),
                walk(4, event.getVenueName() + " 인근역", event.getVenueName(), 7)
        );

        return build(event, userLat, userLng,
                72, 1850, 1,
                "현재 위치", event.getVenueName(),
                event.getVenueName(), null,
                segments);
    }

    // ══════════════════════════════════════════════════════════
    // 헬퍼 메서드들
    // ══════════════════════════════════════════════════════════

    private TransitSegmentResponse walk(int order, String from, String to, int minutes) {
        return TransitSegmentResponse.builder()
                .order(order).mode("도보").sectionTime(minutes)
                .startName(from).endName(to)
                .displayName("도보 " + minutes + "분").color("#999999")
                .build();
    }

    private TransitSegmentResponse bus(int order, String from, String to,
                                       int minutes, String label, String number) {
        return TransitSegmentResponse.builder()
                .order(order).mode("버스").sectionTime(minutes)
                .startName(from).endName(to)
                .busLabel(label).busNumbers(List.of(number))
                .displayName(number + "번 버스 탑승").color("#0068B7")
                .build();
    }

    private TransitSegmentResponse subway(int order, String from, String to,
                                          int minutes, String lineName, String color) {
        return TransitSegmentResponse.builder()
                .order(order).mode("지하철").sectionTime(minutes)
                .startName(from).endName(to)
                .subwayLineName(lineName)
                .displayName(lineName).color(color)
                .build();
    }

    private TransitSegmentResponse train(int order, String from, String to,
                                         int minutes, String lineName, String color) {
        return TransitSegmentResponse.builder()
                .order(order).mode("기차").sectionTime(minutes)
                .startName(from).endName(to)
                .subwayLineName(lineName)
                .displayName(lineName).color(color)
                .build();
    }

    private TransitRouteResponse build(TicketEvent event, double userLat, double userLng,
                                       int totalTime, int payment, int transferCount,
                                       String firstStation, String lastStation,
                                       String nearestStation, String recommendedExit,
                                       List<TransitSegmentResponse> segments) {
        String summary = "약 " + totalTime + "분 소요됩니다. 공연장 도착 전 여유 있게 출발하세요.";

        TransitAccessibilityGuideResponse guide = TransitAccessibilityGuideResponse.builder()
                .nearestStation(nearestStation)
                .recommendedExit(recommendedExit)
                .caution("공연 시작 전 주변이 혼잡할 수 있습니다.")
                .build();

        return TransitRouteResponse.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .venueName(event.getVenueName())
                .venueAddress(event.getVenueAddress())
                .startLatitude(userLat).startLongitude(userLng)
                .destinationLatitude(event.getDestinationLatitude())
                .destinationLongitude(event.getDestinationLongitude())
                .totalTime(totalTime).payment(payment)
                .transferCount(transferCount)
                .totalWalk(calculateTotalWalk(segments))
                .firstStation(firstStation).lastStation(lastStation)
                .recommendedDepartureTime(null)
                .summaryMessage(summary)
                .segments(segments)
                .accessibilityGuide(guide)
                .provider("MOCK")
                .build();
    }

    private int calculateTotalWalk(List<TransitSegmentResponse> segments) {
        int metersPerMinute = 80;
        return segments.stream()
                .filter(segment -> "도보".equals(segment.getMode()))
                .mapToInt(segment -> segment.getSectionTime() == null ? 0 : segment.getSectionTime() * metersPerMinute)
                .sum();
    }
}
