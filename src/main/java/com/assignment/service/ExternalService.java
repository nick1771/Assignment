package com.assignment.service;

import com.assignment.model.route.Route;
import com.assignment.model.route.RouteBasePriceResponse;
import com.assignment.model.tax.TaxRateResponse;
import com.assignment.model.tax.TaxRateType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExternalService {
    private static final BigDecimal VAT_RATE = BigDecimal.valueOf(11);
    private static final Map<Route, BigDecimal> ROUTE_BASE_PRICES = Map.of(
        new Route("Riga", "Vilnius"), new BigDecimal(11)
    );

    public List<TaxRateResponse> getTaxRates() {
        return Collections.singletonList(new TaxRateResponse(TaxRateType.VAT, VAT_RATE));
    }

    public Optional<RouteBasePriceResponse> getRouteBasePrice(Route route) {
        return Optional
            .ofNullable(ROUTE_BASE_PRICES.get(route))
            .map(RouteBasePriceResponse::new);
    }
}
