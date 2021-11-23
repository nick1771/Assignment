package com.assignment.model.tax;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TaxRateResponse {
    @NotNull(message = "Tax rate type is required")
    private TaxRateType type;

    @NotNull(message = "Percentage is required")
    @Min(value = 0)
    private BigDecimal percentage;
}
