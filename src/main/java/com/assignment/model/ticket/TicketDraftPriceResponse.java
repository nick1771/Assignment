package com.assignment.model.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TicketDraftPriceResponse {
    private List<TicketDraftPriceItem> items;
    private String total;
}
