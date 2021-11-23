package com.assignment.model.ticket;

import com.assignment.model.tax.TaxRateResponse;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TicketDraftPriceApi {
    @NotNull(message = "Route base price is required")
    @Min(value = 0)
    private BigDecimal routeBasePrice;

    @Valid
    @NotEmpty(message = "At least one passenger is required")
    private List<Passenger> passengers;

    @Valid
    @NotEmpty(message = "Tax rates are required")
    private List<TaxRateResponse> taxRates;
}
