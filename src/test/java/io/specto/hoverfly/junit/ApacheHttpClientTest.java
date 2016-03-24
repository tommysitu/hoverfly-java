package io.specto.hoverfly.junit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class ApacheHttpClientTest {

    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.buildFromClassPathResource("test-service.json").build();

    private HttpClient httpClient;

    @Before
    public void setUp() {
        httpClient = HttpClientBuilder.create().useSystemProperties().build();
    }

    @Test
    public void shouldBeAbleToGetABookingUsingApacheHttpClientAndHoverfly() throws IOException {
        // Given
        final HttpGet httpGet = new HttpGet("http://www.my-test.com/api/bookings/1");

        // When
        final HttpResponse response = httpClient.execute(httpGet);

        // Then
        assertThatJson(EntityUtils.toString(response.getEntity())).isEqualTo("{" +
                "\"bookingId\":\"1\"," +
                "\"origin\":\"London\"," +
                "\"destination\":\"Singapore\"," +
                "\"time\":\"2011-09-01T12:30\"," +
                "\"_links\":{\"self\":{\"href\":\"http://localhost/api/bookings/1\"}}" +
                "}");
    }

}
