package com.assignment.service.ticket;

import com.assignment.model.tax.TaxRateResponse;
import com.assignment.model.ticket.TicketItemType;
import com.assignment.service.CurrencyService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class TicketItemPriceCalculator {
    private final Map<TicketItemType, BigDecimal> itemPrices;
    private final List<TaxRateResponse> taxRates;
    private final CurrencyService currencyService;

    private final static BigDecimal CHILD_DISCOUNT = BigDecimal.valueOf(0.5);
    private final static BigDecimal LUGGAGE_PERCENTAGE = BigDecimal.valueOf(0.3);
    private final static BigDecimal PERCENTAGE_DIVISOR = BigDecimal.valueOf(100);
    private final static int PERCENTAGE_FRACTION = 3;

    public TicketItemPriceCalculator(BigDecimal routeBasePrice, List<TaxRateResponse> taxRates, CurrencyService currencyService) {
        final var routeBasePriceMinor = currencyService.amountToMinorUnits(routeBasePrice);

        this.currencyService = currencyService;
        this.taxRates = taxRates;
        this.itemPrices = Map.of(
            TicketItemType.ADULT, routeBasePriceMinor,
            TicketItemType.CHILD, currencyService.roundMinor(routeBasePriceMinor.multiply(CHILD_DISCOUNT)),
            TicketItemType.LUGGAGE, currencyService.roundMinor(routeBasePriceMinor.multiply(LUGGAGE_PERCENTAGE))
        );
    }

    public BigDecimal calculateItemPrice(TicketItemType type, int itemCount) {
        final var itemPrice = itemPrices.get(type);
        return applyTax(itemPrice.multiply(BigDecimal.valueOf(itemCount)));
    }

    // This method is public for testing purposes
    public BigDecimal applyTax(BigDecimal amount) {
        // This should be guaranteed to exist
        final var vat = taxRates.get(0);
        // Formula to add percentage to a number = num * (1 + percent / 100)
        final var vatRate = vat.getPercentage().divide(PERCENTAGE_DIVISOR, PERCENTAGE_FRACTION, RoundingMode.HALF_UP);
        return currencyService.roundMinor(amount.multiply(vatRate.add(BigDecimal.ONE)));
    }
}
