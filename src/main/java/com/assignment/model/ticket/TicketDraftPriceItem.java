package com.assignment.model.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketDraftPriceItem {
    private TicketItemType type;
    private String total;
    private int count;

    public TicketDraftPriceItem(TicketItemType type, String total) {
        this.type = type;
        this.total = total;
    }
}
