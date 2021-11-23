package com.assignment.service;

import com.assignment.model.tax.TaxRateResponse;
import com.assignment.model.tax.TaxRateType;
import com.assignment.model.ticket.TicketItemType;
import com.assignment.service.ticket.TicketItemPriceCalculator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TicketItemPriceCalculatorTests {
    private final static BigDecimal VAT = BigDecimal.valueOf(8);
    private final static BigDecimal BASE_PRICE = BigDecimal.valueOf(12.56);
    private final static List<TaxRateResponse> TAX_RATES = Collections.singletonList(new TaxRateResponse(TaxRateType.VAT, VAT));

    private final CurrencyService currencyService = new CurrencyService();
    private final TicketItemPriceCalculator ticketItemPriceCalculator =
            new TicketItemPriceCalculator(BASE_PRICE, TAX_RATES, currencyService);

    private static Stream<Arguments> provideItemParams() {
        return Stream.of(
            Arguments.of(TicketItemType.ADULT, 1, BigDecimal.valueOf(1356)),
            Arguments.of(TicketItemType.CHILD, 1, BigDecimal.valueOf(678)),
            Arguments.of(TicketItemType.LUGGAGE, 3, BigDecimal.valueOf(1221))
        );
    }

    @ParameterizedTest
    @MethodSource("provideItemParams")
    void testCalculateItemPrice(TicketItemType item, int count, BigDecimal result) {
        assertThat(ticketItemPriceCalculator.calculateItemPrice(item, count), is(result));
    }

    private static Stream<Arguments> provideTaxParams() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(312), BigDecimal.valueOf(337)),
            Arguments.of(BigDecimal.valueOf(201), BigDecimal.valueOf(217))
        );
    }

    @ParameterizedTest
    @MethodSource("provideTaxParams")
    void testApplyTax(BigDecimal amount, BigDecimal result) {
        assertThat(ticketItemPriceCalculator.applyTax(amount), is(result));
    }
}
