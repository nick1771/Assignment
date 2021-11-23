package com.assignment.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Service
public class CurrencyService {
    private final static DecimalFormat CURRENCY_FORMAT;
    private final static BigDecimal MULTIPLICAND = BigDecimal.valueOf(100);
    private final static int EUR_FRACTION = 2;

    static {
        final var decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');

        CURRENCY_FORMAT = new DecimalFormat();
        CURRENCY_FORMAT.setMinimumFractionDigits(EUR_FRACTION);
        CURRENCY_FORMAT.setMaximumFractionDigits(EUR_FRACTION);
        CURRENCY_FORMAT.setDecimalFormatSymbols(decimalFormatSymbols);
        CURRENCY_FORMAT.setGroupingUsed(false);
    }

    public BigDecimal roundMinor(BigDecimal amount) {
        return amount.setScale(0, RoundingMode.HALF_UP);
    }

    public BigDecimal amountToMinorUnits(BigDecimal amount) {
        return roundMinor(amount.setScale(EUR_FRACTION, RoundingMode.HALF_UP).multiply(MULTIPLICAND));
    }

    public String formatAmountToMajorUnits(BigDecimal amount) {
        final var majorAmount = amount.divide(MULTIPLICAND, EUR_FRACTION, RoundingMode.HALF_UP);
        return CURRENCY_FORMAT.format(majorAmount) + " EUR";
    }
}
