package com.assignment.controller;

import com.assignment.model.ticket.TicketDraftPriceApi;
import com.assignment.model.ticket.TicketDraftPriceResponse;
import com.assignment.service.ticket.TicketDraftPriceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/ticket")
public class TicketDraftPriceController {
    private final TicketDraftPriceService ticketDraftPriceService;

    public TicketDraftPriceController(TicketDraftPriceService ticketDraftPriceService) {
        this.ticketDraftPriceService = ticketDraftPriceService;
    }

    @PostMapping
    public TicketDraftPriceResponse getTicketDraftPrice(@Valid @RequestBody TicketDraftPriceApi ticketDraftPriceApi) {
        return ticketDraftPriceService.getTicketDraftPrice(ticketDraftPriceApi);
    }
}
