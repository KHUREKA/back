package com.khureka.server.external.tmap;

import com.khureka.server.domain.TicketEvent;
import com.khureka.server.external.tmap.dto.TmapItinerary;
import com.khureka.server.external.tmap.dto.TmapLeg;
import com.khureka.server.ticket.dto.TransitAccessibilityGuideResponse;
import com.khureka.server.ticket.dto.TransitRouteResponse;
import com.khureka.server.ticket.dto.TransitSegmentResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TmapRouteMapper {

    public TransitRouteResponse toResponse(
            TmapItinerary itinerary,
            TicketEvent ticketEvent,
            double userLat,
            double userLng
    ) {
        List<TmapLeg> legs = itinerary.getLegs() != null ? itinerary.getLegs() : List.of();

        String firstStation = resolveFirstStation(legs);
        String lastStation = resolveLastStation(legs, ticketEvent.getVenueName());

        int totalTime = ceilToMinutes(safeInt(itinerary.getTotalTime()));
        int payment = resolvePayment(itinerary);
        String summaryMessage = "약 " + totalTime + "분 소요됩니다. 공연장 도착 전 여유 있게 출발하세요.";

        TransitAccessibilityGuideResponse accessibilityGuide = TransitAccessibilityGuideResponse.builder()
                .nearestStation(lastStation)
                .recommendedExit(null)
                .caution("공연 시작 전 주변이 혼잡할 수 있습니다.")
                .build();

        return TransitRouteResponse.builder()
                .eventId(ticketEvent.getId())
                .eventTitle(ticketEvent.getTitle())
                .venueName(ticketEvent.getVenueName())
                .venueAddress(ticketEvent.getVenueAddress())
                .startLatitude(userLat)
                .startLongitude(userLng)
                .destinationLatitude(ticketEvent.getDestinationLatitude())
                .destinationLongitude(ticketEvent.getDestinationLongitude())
                .totalTime(totalTime)
                .payment(payment)
                .transferCount(safeInt(itinerary.getTransferCount()))
                .totalWalk(safeInt(itinerary.getTotalWalkDistance()))
                .totalDistance(safeInt(itinerary.getTotalDistance()))
                .firstStation(firstStation)
                .lastStation(lastStation)
                .recommendedDepartureTime(null)
                .summaryMessage(summaryMessage)
                .segments(buildSegments(legs))
                .accessibilityGuide(accessibilityGuide)
                .provider("TMAP")
                .build();
    }

    private String resolveFirstStation(List<TmapLeg> legs) {
        if (legs.isEmpty()) return "현재 위치";
        TmapLeg first = legs.get(0);
        if (first.getStart() == null || first.getStart().getName() == null) return "현재 위치";
        return first.getStart().getName();
    }

    private String resolveLastStation(List<TmapLeg> legs, String fallback) {
        if (legs.isEmpty()) return fallback;
        TmapLeg last = legs.get(legs.size() - 1);
        if (last.getEnd() == null || last.getEnd().getName() == null) return fallback;
        return last.getEnd().getName();
    }

    private int resolvePayment(TmapItinerary itinerary) {
        if (itinerary.getFare() == null) return 0;
        if (itinerary.getFare().getRegular() == null) return 0;
        return safeInt(itinerary.getFare().getRegular().getTotalFare());
    }

    private List<TransitSegmentResponse> buildSegments(List<TmapLeg> legs) {
        List<TransitSegmentResponse> segments = new ArrayList<>();
        for (int i = 0; i < legs.size(); i++) {
            segments.add(buildSegment(i + 1, legs.get(i)));
        }
        return segments;
    }

    private TransitSegmentResponse buildSegment(int order, TmapLeg leg) {
        String rawMode = leg.getMode() != null ? leg.getMode() : "";
        String mode = toKoreanMode(rawMode);
        int sectionTime = ceilToMinutes(safeInt(leg.getSectionTime()));
        int distance = safeInt(leg.getDistance());
        String startName = leg.getStart() != null && leg.getStart().getName() != null ? leg.getStart().getName() : "";
        String endName = leg.getEnd() != null && leg.getEnd().getName() != null ? leg.getEnd().getName() : "";
        String color = resolveColor(rawMode, leg.getRouteColor());

        String busLabel = null;
        List<String> busNumbers = null;
        String subwayLineName = null;
        Integer stationCount = null;
        String displayName;

        if ("BUS".equals(rawMode) || "EXPRESSBUS".equals(rawMode)) {
            String[] parsed = parseBusRoute(leg.getRoute());
            busLabel = "EXPRESSBUS".equals(rawMode) ? "고속/시외" : parsed[0];
            busNumbers = parsed[1] != null ? List.of(parsed[1]) : List.of();
            displayName = buildBusDisplayName(busNumbers, leg.getRoute());
        } else if ("SUBWAY".equals(rawMode)) {
            subwayLineName = leg.getRoute();
            if (leg.getPassStopList() != null && leg.getPassStopList().getStationList() != null) {
                stationCount = leg.getPassStopList().getStationList().size();
            }
            displayName = subwayLineName != null ? subwayLineName : "지하철";
        } else if ("WALK".equals(rawMode)) {
            displayName = "도보 " + sectionTime + "분";
        } else {
            displayName = mode.isBlank() ? "이동" : mode + " 이동";
        }

        return TransitSegmentResponse.builder()
                .order(order)
                .mode(mode)
                .sectionTime(sectionTime)
                .distance(distance)
                .startName(startName)
                .endName(endName)
                .displayName(displayName)
                .color(color)
                .busLabel(busLabel)
                .busNumbers(busNumbers)
                .subwayLineName(subwayLineName)
                .stationCount(stationCount)
                .build();
    }

    private String toKoreanMode(String mode) {
        return switch (mode) {
            case "WALK" -> "도보";
            case "BUS" -> "버스";
            case "SUBWAY" -> "지하철";
            case "EXPRESSBUS" -> "고속/시외버스";
            case "TRAIN" -> "기차";
            case "AIRPLANE" -> "항공";
            case "FERRY" -> "해운";
            default -> mode.isBlank() ? "이동" : mode;
        };
    }

    private String resolveColor(String mode, String routeColor) {
        String hex = normalizeColor(routeColor);
        return switch (mode) {
            case "WALK" -> "#999999";
            case "BUS" -> hex != null ? hex : "#0068B7";
            case "SUBWAY" -> hex != null ? hex : "#999999";
            case "EXPRESSBUS" -> "#E60012";
            case "TRAIN" -> "#003DA5";
            case "AIRPLANE" -> "#7B61FF";
            case "FERRY" -> "#00A8A8";
            default -> "#999999";
        };
    }

    private String normalizeColor(String routeColor) {
        if (routeColor == null || routeColor.isBlank()) return null;
        return routeColor.startsWith("#") ? routeColor : "#" + routeColor;
    }

    // "지선:1128" → ["지선", "1128"], "5100" → ["버스", "5100"], null → ["버스", null]
    private String[] parseBusRoute(String route) {
        if (route == null) return new String[]{"버스", null};
        if (route.contains(":")) {
            String[] parts = route.split(":", 2);
            return new String[]{parts[0], parts[1]};
        }
        return new String[]{"버스", route};
    }

    private String buildBusDisplayName(List<String> busNumbers, String route) {
        if (busNumbers != null && !busNumbers.isEmpty()) {
            return busNumbers.get(0) + "번 버스 탑승";
        }
        return route != null && !route.isBlank() ? route + " 탑승" : "버스 탑승";
    }

    private int ceilToMinutes(int seconds) {
        return (int) Math.ceil(seconds / 60.0);
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }
}
