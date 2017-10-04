package io.specto.hoverfly.extensiontest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import io.specto.hoverfly.junit5.HoverflyCaptureResolver;
import io.specto.hoverfly.junit5.HoverflyExtension;
import io.specto.hoverfly.junit5.api.HoverflyCapture;
import io.specto.hoverfly.webserver.CaptureModeTestWebServer;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static io.specto.hoverfly.junit.core.HoverflyConfig.configs;
import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;

@HoverflyCapture(recordFile = "recorded-simulation.json",
        proxyLocalhost = true,
        captureAllHeaders = true)
@ExtendWith(HoverflyCaptureResolver.class)
public class CaptureModeTest {


    private static final Path RECORDED_SIMULATION_FILE = Paths.get("src/test/resources/hoverfly", "recorded-simulation.json");
    private static final Path EXPECTED_SIMULATION_FILE = Paths.get("src/test/resources", "expected-simulation.json");

    private static URI webServerBaseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    // We have to assert after the rule has executed because that's when the classpath is written to the filesystem
    @AfterAll
    public static void after() throws IOException, JSONException {

        // In Junit 5, this AfterAll is executed before the AfterAll in the HoverflyCaptureResolver
//        final String expectedSimulation = new String(Files.readAllBytes(EXPECTED_SIMULATION_FILE), defaultCharset());
//        final String actualSimulation = new String(Files.readAllBytes(RECORDED_SIMULATION_FILE), defaultCharset());
//        JSONAssert.assertEquals(expectedSimulation, actualSimulation, JSONCompareMode.LENIENT);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        Simulation simulation = objectMapper.readValue(actualSimulation, Simulation.class);
//        Set<RequestResponsePair> pairs = simulation.getHoverflyData().getPairs();
//        assertThat(pairs.iterator().next().getRequest().getHeaders()).isNotEmpty();

        CaptureModeTestWebServer.terminate();
    }

    @BeforeAll
    public static void setUp() throws Exception {
        Files.deleteIfExists(RECORDED_SIMULATION_FILE);
        webServerBaseUrl = CaptureModeTestWebServer.run();
    }

    @Test
    public void shouldRecordInteractions() throws Exception {
        // When
        restTemplate.getForObject(webServerBaseUrl, String.class);
    }

}
