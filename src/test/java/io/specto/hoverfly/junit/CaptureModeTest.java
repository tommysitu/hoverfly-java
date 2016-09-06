package io.specto.hoverfly.junit;

import com.google.common.io.Resources;
import io.specto.hoverfly.webserver.WebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class CaptureModeTest {

    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode("src/main/resources/recorded-simulation.json")
            .proxyLocalHost()
            .build();

    private URI webServerBaseUrl;
    private RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        webServerBaseUrl = WebServer.run();
        restTemplate = new RestTemplate();
    }

    @Test
    public void shouldRecordInteractions() throws Exception {
        // When
        restTemplate.getForObject(webServerBaseUrl, String.class);
    }

    // We have to assert after the rule has executed because that's when the file is written to the filesystem
    @After
    public void after() throws IOException {
        final String expectedSimulation = Resources.toString(Resources.getResource("expected-simulation.json"), Charset.defaultCharset());
        final String actualSimulation = Resources.toString(Resources.getResource("recorded-simulation.json"), Charset.defaultCharset());
        assertThatJson(actualSimulation).isEqualTo(expectedSimulation);
    }

}
