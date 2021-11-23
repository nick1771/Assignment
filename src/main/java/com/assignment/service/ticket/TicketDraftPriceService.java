package com.assignment.service.ticket;

import com.assignment.model.tax.TaxRateResponse;
import com.assignment.model.ticket.TicketDraftPriceApi;
import com.assignment.model.ticket.TicketDraftPriceItem;
import com.assignment.model.ticket.TicketDraftPriceResponse;
import com.assignment.model.ticket.TicketItemType;
import com.assignment.service.CurrencyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketDraftPriceService {
    private final CurrencyService currencyService;

    public TicketDraftPriceService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public TicketDraftPriceResponse getTicketDraftPrice(TicketDraftPriceApi ticketDraftPriceApi) {
        final var responseBuilder = new TicketDraftPriceBuilder(ticketDraftPriceApi.getRouteBasePrice(), ticketDraftPriceApi.getTaxRates());
        for (final var passenger : ticketDraftPriceApi.getPassengers()) {
            responseBuilder.addItem(passenger.getType().toTicketItem(), 1);
            if (passenger.getLuggageCount() > 0) {
                responseBuilder.addItem(TicketItemType.LUGGAGE, passenger.getLuggageCount());
            }
        }

        return responseBuilder.build();
    }

    private class TicketDraftPriceBuilder {
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private final ArrayList<TicketDraftPriceItem> items = new ArrayList<>();
        private final TicketItemPriceCalculator itemPriceCalculator;

        public TicketDraftPriceBuilder(BigDecimal routeBasePrice, List<TaxRateResponse> taxRates) {
            this.itemPriceCalculator = new TicketItemPriceCalculator(routeBasePrice, taxRates, currencyService);
        }

        public void addItem(TicketItemType type, int itemCount) {
            final var itemPriceAmount = itemPriceCalculator.calculateItemPrice(type, itemCount);
            final var itemPrice = currencyService.formatAmountToMajorUnits(itemPriceAmount);

            totalAmount = totalAmount.add(itemPriceAmount);
            items.add(new TicketDraftPriceItem(type, itemPrice, itemCount));
        }

        public TicketDraftPriceResponse build() {
            return new TicketDraftPriceResponse(items, currencyService.formatAmountToMajorUnits(totalAmount));
        }
    }
}
