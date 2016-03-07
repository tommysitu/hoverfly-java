import io.specto.hoverfly.junit.HoverflyRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class HoverflyRulePortConfigurationTest {

    private static final int EXPECTED_ADMIN_PORT = 8889;
    private static final int EXPECTED_PROXY_PORT = 8890;
    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.buildFromClassPathResource("test-service.json")
            .withAdminPort(EXPECTED_ADMIN_PORT)
            .withProxyPort(EXPECTED_PROXY_PORT)
            .build();

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void shouldStillVirtualizeServiceAfterConfiguringPorts() {
        // When
        final ResponseEntity<String> getBookingResponse = restTemplate.getForEntity("http://www.my-test.com/api/bookings/1", String.class);

        // Then
        assertThat(getBookingResponse.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void shouldSetProxyPortToWhatIsConfigured() {
        assertThat(System.getProperty("http.proxyPort")).isEqualTo(String.valueOf(EXPECTED_PROXY_PORT));
    }

    @Test
    public void shouldChangeAdminPortToConfiguredPort() {
        final ResponseEntity<String> health = restTemplate.getForEntity(String.format("http://localhost:%s/stats", EXPECTED_ADMIN_PORT), String.class);
        assertThat(health.getStatusCode()).isEqualTo(OK);
    }

}
