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
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final TicketEventRepository ticketEventRepository;

    @Value("${ncp.client-id}")
    private String ncpClientId;

    @GetMapping("/map/{ticketEventId}")
    public String map(@PathVariable Long ticketEventId, Model model) {
        TicketEvent event = ticketEventRepository.findById(ticketEventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));

        model.addAttribute("destName", event.getTitle());
        model.addAttribute("lat", event.getDestinationLatitude());
        model.addAttribute("lng", event.getDestinationLongitude());
        model.addAttribute("clientId", ncpClientId);

        return "map";
    }
}
