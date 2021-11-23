package com.assignment.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class CurrencyServiceTests {
    private final CurrencyService currencyService = new CurrencyService();

    private static Stream<Arguments> provideAmountToMinorUnitsParams() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(3.12), BigDecimal.valueOf(312)),
            Arguments.of(BigDecimal.valueOf(3.576), BigDecimal.valueOf(358)),
            Arguments.of(BigDecimal.valueOf(10), BigDecimal.valueOf(1000))
        );
    }

    @ParameterizedTest
    @MethodSource("provideAmountToMinorUnitsParams")
    void testAmountToMinorUnits(BigDecimal amount, BigDecimal minorAmount) {
        assertThat(currencyService.amountToMinorUnits(amount), is(minorAmount));
    }

    private static Stream<Arguments> provideFormatAmountToMajorUnits() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(312), "3.12 EUR"),
            Arguments.of(BigDecimal.valueOf(3579), "35.79 EUR"),
            Arguments.of(BigDecimal.valueOf(405.5), "4.06 EUR")
        );
    }

    @ParameterizedTest
    @MethodSource("provideFormatAmountToMajorUnits")
    void testFormatAmountToMajorUnits(BigDecimal amount, String formatted) {
        assertThat(currencyService.formatAmountToMajorUnits(amount), is(formatted));
    }

    private static Stream<Arguments> provideRoundMinor() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(4.5), BigDecimal.valueOf(5)),
            Arguments.of(BigDecimal.valueOf(6.1), BigDecimal.valueOf(6)),
            Arguments.of(BigDecimal.valueOf(10), BigDecimal.valueOf(10))
        );
    }

    @ParameterizedTest
    @MethodSource("provideRoundMinor")
    void testRoundMinor(BigDecimal amount, BigDecimal rounded) {
        assertThat(currencyService.roundMinor(amount), is(rounded));
    }
}
