package io.specto.hoverfly.extensiontest;

import io.specto.hoverfly.junit5.HoverflySimulateResolver;
import io.specto.hoverfly.junit5.api.HoverflySimulate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@HoverflySimulate(
        adminPort = 8889, proxyPort = 8890,
        simulationSource = @HoverflySimulate.Source(classpath = "test-service.json")
)
@ExtendWith(HoverflySimulateResolver.class)
public class HoverflyRulePortConfigurationTest {


    private RestTemplate restTemplate = new RestTemplate();



    @Test
    public void shouldStillVirtualizeServiceAfterConfiguringPorts() {
        // When
        final ResponseEntity<String> getBookingResponse = restTemplate.getForEntity("http://www.my-test.com/api/bookings/1", String.class);

        // Then
        assertThat(getBookingResponse.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void shouldChangeAdminPortToConfiguredPort() {
        final ResponseEntity<String> health = restTemplate.getForEntity(String.format("http://localhost:%s/api/stats", 8889), String.class);
        assertThat(health.getStatusCode()).isEqualTo(OK);
    }

//    @Test
//    public void shouldBeAbleToGetPort() {
//        // Then
//        assertThat(hoverflyInstance.getHoverflyConfig().getProxyPort()).isEqualTo(8890);
//    }


}
