package com.khureka.server.ticket.controller;

import com.khureka.server.common.exception.BusinessException;
import com.khureka.server.common.exception.ErrorCode;
import com.khureka.server.domain.TicketEvent;
import com.khureka.server.ticket.repository.TicketEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final TicketEventRepository ticketEventRepository;

    @Value("${ncp.maps.client-id:${ncp.client-id:}}")
    private String ncpMapsClientId;

    @GetMapping("/map")
    public String map(@RequestParam("id") Long ticketEventId, Model model) {
        TicketEvent event = ticketEventRepository.findById(ticketEventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        model.addAttribute("eventId", ticketEventId);
        model.addAttribute("venueName", event.getVenueName());
        model.addAttribute("eventTitle", event.getTitle());
        model.addAttribute("lat", event.getDestinationLatitude());
        model.addAttribute("lng", event.getDestinationLongitude());
        model.addAttribute("ncpKeyId", ncpMapsClientId == null ? "" : ncpMapsClientId.trim());

        return "map";
    }
}
