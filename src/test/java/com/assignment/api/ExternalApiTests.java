package com.assignment.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class ExternalApiTests {
    private final static String GET_ROUTE_BASE_PRICE = "/api/v1/external/route_price?source={source}&destination={destination}";
    private final static String GET_VAT = "/api/v1/external/tax";

    @LocalServerPort
    private int port;

    @Test
    void routeBasePriceReturned() {
        when()
            .get("http://localhost:" + port + GET_ROUTE_BASE_PRICE, "Vilnius", "Lithuania")
        .then()
            .log().all()
            .body("amount", is(11))
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void invalidRouteHandled() {
        when()
            .get("http://localhost:" + port + GET_ROUTE_BASE_PRICE, "asd", "Lithuania")
        .then()
            .log().all()
            .body(
                "status", is("NOT_FOUND"),
                "message", is("Requested data not found")
            )
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void missingParamForRouteHandled() {
        when()
            .get("http://localhost:" + port + "/api/v1/external/route_price?source=asd")
        .then()
            .log().all()
            .body(
                "status", is("BAD_REQUEST"),
                "message", is("Missing required request parameter - destination")
            )
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void taxRatesReturned() {
        when()
            .get("http://localhost:" + port + GET_VAT)
        .then()
            .log().all()
            .body(
                "type[0]", is("VAT"),
                "percentage[0]", is(11)
            )
            .statusCode(HttpStatus.OK.value());
    }
}
