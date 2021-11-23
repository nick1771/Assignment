package com.assignment.api;

import com.assignment.model.tax.TaxRateResponse;
import com.assignment.model.tax.TaxRateType;
import com.assignment.model.ticket.Passenger;
import com.assignment.model.ticket.PassengerType;
import com.assignment.model.ticket.TicketDraftPriceApi;
import com.assignment.model.ticket.TicketItemType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.AllOf.allOf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketDraftPriceServiceTests {
    private final static String POST_TICKET_DRAFT_PRICE = "/api/v1/ticket";
    private final static String CONTENT_TYPE = "application/vnd.api+json";

    @LocalServerPort
    private int port;

    private String buildRequestBody(BigDecimal basePrice, List<Passenger> passengers, BigDecimal vat) {
        // Ignoring exceptions usually is incorrect, but here exception should not be thrown
        // and manually constructing a request string is annoying
        try {
            final var api = TicketDraftPriceApi.builder()
                .taxRates(Collections.singletonList(new TaxRateResponse(TaxRateType.VAT, vat)))
                .routeBasePrice(basePrice)
                .passengers(passengers)
                .build();

            final var mapper = new ObjectMapper();
            return mapper.writeValueAsString(api);
        } catch (Exception ignored) {}

        return "";
    }

    private String defaultBody() {
        final var passengers = List.of(
            new Passenger(PassengerType.ADULT, 2),
            new Passenger(PassengerType.CHILD, 1)
        );
        return buildRequestBody(BigDecimal.valueOf(10), passengers, BigDecimal.valueOf(21));
    }

    @Test
    void ticketDraftPriceReturned() {
        given()
            .contentType(CONTENT_TYPE)
            .body(defaultBody())
       .when()
            .post("http://localhost:" + port + POST_TICKET_DRAFT_PRICE)
       .then()
            .log().all()
            .body(
                "items[0].type", is("ADULT"),
                "items[0].total", is("12.10 EUR"),
                "items[0].count", is(1),
                "items[1].type", is("LUGGAGE"),
                "items[1].total", is("7.26 EUR"),
                "items[1].count", is(2),
                "items[2].type", is("CHILD"),
                "items[2].total", is("6.05 EUR"),
                "items[2].count", is(1),
                "items[3].type", is("LUGGAGE"),
                "items[3].total", is("3.63 EUR"),
                "items[3].count", is(1),
                "total", is("29.04 EUR")
            )
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void ticketDraftPriceReturned2() {
        final var passengers = List.of(
            new Passenger(PassengerType.ADULT, 1),
            new Passenger(PassengerType.ADULT, 1),
            new Passenger(PassengerType.CHILD, 2),
            new Passenger(PassengerType.CHILD, 0)
        );
        final var body = buildRequestBody(BigDecimal.valueOf(8), passengers, BigDecimal.valueOf(18));

        given()
            .contentType(CONTENT_TYPE)
            .body(body)
        .when()
            .post("http://localhost:" + port + POST_TICKET_DRAFT_PRICE)
        .then()
            .log().all()
            .body(
                "items[0].type", is("ADULT"),
                "items[0].total", is("9.44 EUR"),
                "items[0].count", is(1),
                "items[1].type", is("LUGGAGE"),
                "items[1].total", is("2.83 EUR"),
                "items[1].count", is(1),
                "items[2].type", is("ADULT"),
                "items[2].total", is("9.44 EUR"),
                "items[2].count", is(1),
                "items[3].type", is("LUGGAGE"),
                "items[3].total", is("2.83 EUR"),
                "items[3].count", is(1),
                "items[4].type", is("CHILD"),
                "items[4].total", is("4.72 EUR"),
                "items[4].count", is(1),
                "items[5].type", is("LUGGAGE"),
                "items[5].total", is("5.66 EUR"),
                "items[5].count", is(2),
                "items[6].type", is("CHILD"),
                "items[6].total", is("4.72 EUR"),
                "items[6].count", is(1),
                "total", is("39.64 EUR")
            )
            .statusCode(HttpStatus.OK.value());
    }

    private static Stream<Arguments> provideSerializationParams() {
        return Stream.of(
            Arguments.of("\"type\":\"ADULT\"", "\"type\":\"asd\""),
            Arguments.of("\"routeBasePrice\":10", "\"routeBasePrice\":\"asd\""),
            Arguments.of("\"luggageCount\":2", "\"luggageCount\":\"asd\""),
            Arguments.of("\"percentage\":21", "\"percentage\":\"asd\"")
        );
    }

    @ParameterizedTest
    @MethodSource("provideSerializationParams")
    void serializationProblemsHandled(String replacement, String replacementValue) {
        final var body = defaultBody().replaceFirst(replacement, replacementValue);
        given()
            .contentType(CONTENT_TYPE)
            .body(body)
            .when()
            .post("http://localhost:" + port + POST_TICKET_DRAFT_PRICE)
            .then()
            .log().all()
            .body(
                "status", is("BAD_REQUEST"),
                "message", is("Invalid data format")
            )
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private static Stream<Arguments> provideMissingFieldParams() {
        return Stream.of(
            Arguments.of("\"type\":\"ADULT\",", "passengers[0].type - Passenger type is required"),
            Arguments.of("\"type\":\"VAT\",", "taxRates[0].type - Tax rate type is required"),
            Arguments.of(",\"percentage\":21", "taxRates[0].percentage - Percentage is required")
        );
    }

    @ParameterizedTest
    @MethodSource("provideMissingFieldParams")
    void missingSubfieldsHandled(String replacement, String errorMessage) {
        final var body = defaultBody().replaceFirst(replacement, "");
        given()
            .contentType(CONTENT_TYPE)
            .body(body)
        .when()
            .post("http://localhost:" + port + POST_TICKET_DRAFT_PRICE)
        .then()
            .log().all()
            .body(
                "status", is("BAD_REQUEST"),
                "message", is(errorMessage)
            )
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void missingFieldsHandled() {
        given()
            .contentType(CONTENT_TYPE)
            .body("{}")
        .when()
            .post("http://localhost:" + port + POST_TICKET_DRAFT_PRICE)
        .then()
            .log().all()
            .body("message",
                allOf(
                    containsString("taxRates - Tax rates are required"),
                    containsString("passengers - At least one passenger is required"),
                    containsString("routeBasePrice - Route base price is required")
                )
            )
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
