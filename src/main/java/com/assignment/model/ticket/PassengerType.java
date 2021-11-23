package com.assignment.model.ticket;

public enum PassengerType {
    ADULT,
    CHILD;

    public TicketItemType toTicketItem() {
        switch (this) {
            case ADULT: return TicketItemType.ADULT;
            case CHILD: return TicketItemType.CHILD;
        }
        return null;
    }
}
