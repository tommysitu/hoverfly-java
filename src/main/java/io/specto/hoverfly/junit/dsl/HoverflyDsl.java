package io.specto.hoverfly.junit.dsl;

/**
 * Entry point to a DSL which can be used to generate a Hoverfly simulation.  Example code:
 * <p>
 * <pre>
 * hoverfly.import(
 *
 *      service("www.my-test.com")
 *
 *          .post("/api/bookings").body("{\"flightId\": \"1\"}")
 *          .willReturn(created("http://localhost/api/bookings/1"))
 *
 *          .get("/api/bookings/1")
 *          .willReturn(success("{\"bookingId\":\"1\"}", "application/json")),
 *
 *      .service("www.other-anotherService.com")
 *
 *          .put("/api/bookings/1").body("{\"flightId\": \"1\", \"class\": \"PREMIUM\"}")
 *          .willReturn(success())
 *
 *          .delete("/api/bookings/1")
 *          .willReturn(noContent())
 *
 *          .get("/api/bookings").query("destination=new%20york")
 *          .willReturn(success("{\"bookingId\":\"2\"}", "application/json")))
 * );
 * </pre>
 *
 * @see StubServiceBuilder
 * @see RequestMatcherBuilder
 * @see ResponseBuilder
 * @see ResponseCreators
 */
public class HoverflyDsl {

    private HoverflyDsl() {
    }

    /**
     * Instantiates a DSL for a given service.  Once you do this, you can create request matchers to response mappings by following these semantics:
     * <p>
     * <pre>
     *
     * service("www.service.com").method("/some/path").willReturn(created())
     * </pre>
     *
     * @param baseUrl the base URL you want all subsequent requests mappings to contain
     * @return the {@link StubServiceBuilder}
     * @see ResponseCreators
     * @see ResponseBuilder
     */
    public static StubServiceBuilder service(final String baseUrl) {
        return new StubServiceBuilder(baseUrl);
    }
}
