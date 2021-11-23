package com.assignment.controller;

import com.assignment.model.route.Route;
import com.assignment.model.route.RouteBasePriceResponse;
import com.assignment.model.tax.TaxRateResponse;
import com.assignment.service.ExternalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/external")
public class ExternalController {
    private final ExternalService externalService;

    public ExternalController(ExternalService externalService) {
        this.externalService = externalService;
    }

    @GetMapping(path = "tax")
    public List<TaxRateResponse> getTaxRates() {
        return this.externalService.getTaxRates();
    }

    @GetMapping(path = "route_price")
    public RouteBasePriceResponse getRouteBasePrice(@RequestParam String source, @RequestParam String destination) {
        final var route = new Route(source, destination);
        final var routeBasePrice = externalService.getRouteBasePrice(route);
        return routeBasePrice.get();
    }
}
