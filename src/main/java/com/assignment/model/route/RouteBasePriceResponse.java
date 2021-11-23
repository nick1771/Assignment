package com.assignment.model.route;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class RouteBasePriceResponse {
    private BigDecimal amount;
}
