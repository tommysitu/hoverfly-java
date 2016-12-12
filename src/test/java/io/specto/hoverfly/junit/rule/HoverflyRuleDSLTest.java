package io.specto.hoverfly.junit.rule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HoverflyRequestBuilder.requestPath;
import static io.specto.hoverfly.junit.dsl.HoverflyResponseBuilder.response;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class HoverflyRuleDSLTest extends BaseHoverflyRuleTest {

    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode();

    @Before
    public void setUp() throws Exception {
        hoverflyRule.simulate(
                service("www.my-test.com")
                    .post(requestPath("/api/bookings")
                                .body("{\"flightId\": \"1\"}")
                    )
                    .willReturn(response()
                                .status(201)
                                .header("Location", "http://localhost/api/bookings/1")
                    )
                    .get(requestPath("/api/bookings/1")
                    )
                    .willReturn(response()
                                .status(200)
                                .body("{\"bookingId\":\"1\",\"origin\":\"London\",\"destination\":\"Singapore\",\"time\":\"2011-09-01T12:30\",\"_links\":{\"self\":{\"href\":\"http://localhost/api/bookings/1\"}}}")
                                .header("Content-Type", "application/json")
                    )
                    .put(requestPath("/api/bookings/1")
                            .body("{\"flightId\": \"1\", \"class\": \"PREMIUM\"}"))
                    .willReturn(response()
                            .status(200))
                    .delete(requestPath("/api/bookings/1"))
                    .willReturn(response()
                            .status(204))
                    .get(requestPath("/api/bookings")
                            .query("destination=new%20york"))
//                            .queryParam("class", "business", "premium")
//                            .queryParam("destination", "new york"))
                    .willReturn(response()
                            .status(200)
                            .body("{\"bookingId\":\"2\",\"origin\":\"London\",\"destination\":\"New York\",\"class\":\"BUSINESS\",\"time\":\"2011-09-01T12:30\",\"_links\":{\"self\":{\"href\":\"http://localhost/api/bookings/2\"}}}"))

                .anotherService("www.other-test.com")
                    .get(requestPath("/api/payment/1"))
                .willReturn(response()
                        .status(200)
                        .body("{\"status\":\"pending\"}"))

        );

    }

    @Test
    public void shouldBeAbleToAmendABookingUsingHoverfly() throws URISyntaxException {
        // Given
        final RequestEntity<String> bookFlightRequest = RequestEntity.put(new URI("http://www.my-test.com/api/bookings/1"))
                .contentType(APPLICATION_JSON)
                .body("{\"flightId\": \"1\", \"class\": \"PREMIUM\"}");

        // When
        final ResponseEntity<String> bookFlightResponse = restTemplate.exchange(bookFlightRequest, String.class);

        // Then
        assertThat(bookFlightResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldBeAbleToDeleteBookingUsingHoverfly() throws Exception {
        // Given
        final RequestEntity<Void> bookFlightRequest = RequestEntity.delete(new URI("http://www.my-test.com/api/bookings/1")).build();

        // When
        final ResponseEntity<Void> bookFlightResponse = restTemplate.exchange(bookFlightRequest, Void.class);

        // Then
        assertThat(bookFlightResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    public void shouldBeAbleToQueryBookingsUsingHoverfly() throws Exception {
        // When
        URI uri = UriComponentsBuilder.fromHttpUrl("http://www.my-test.com")
                .path("/api/bookings")
//                .queryParam("class", "business", "premium")
                .queryParam("destination", "new york")
                .build()
                .toUri();
        final ResponseEntity<String> getBookingResponse = restTemplate.getForEntity(uri, String.class);

        // Then
        assertThat(getBookingResponse.getStatusCode()).isEqualTo(OK);
        assertThatJson(getBookingResponse.getBody()).isEqualTo("{" +
                "\"bookingId\":\"2\"," +
                "\"origin\":\"London\"," +
                "\"destination\":\"New York\"," +
                "\"class\":\"BUSINESS\"," +
                "\"time\":\"2011-09-01T12:30\"," +
                "\"_links\":{\"self\":{\"href\":\"http://localhost/api/bookings/2\"}}" +
                "}");

    }

    @Test
    public void shouldBeAbleToSimulateAnotherService() throws Exception {
        // When
        URI uri = UriComponentsBuilder.fromHttpUrl("http://www.other-test.com")
                .path("/api/payment/1")
                .build()
                .toUri();
        final ResponseEntity<String> getPaymentResponse = restTemplate.getForEntity(uri, String.class);

        // Then
        assertThat(getPaymentResponse.getStatusCode()).isEqualTo(OK);
        assertThatJson(getPaymentResponse.getBody()).isEqualTo("{\"status\":\"pending\"}");

    }
}
