package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;
import org.junit.Test;

import java.util.Set;

import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HoverflyRequestBuilder.requestPath;
import static io.specto.hoverfly.junit.dsl.HoverflyResponseBuilder.response;
import static org.assertj.core.api.Assertions.assertThat;

public class HoverflyStubServiceTest {

    @Test
    public void shouldExtractUrlScheme() throws Exception {

        Simulation simulation = service("https://www.my-test.com").get(requestPath("/")).willReturn(response()).build();

        Set<RequestResponsePair> pairs = simulation.getHoverflyData().getPairs();

        assertThat(pairs).hasSize(1);
        RequestResponsePair pair = pairs.iterator().next();
        assertThat(pair.getRequest().getDestination()).isEqualTo("www.my-test.com");
        assertThat(pair.getRequest().getScheme()).isEqualTo("https");
    }

    @Test
    public void shouldDefaultToHttpScheme() throws Exception {
        Simulation simulation = service("www.my-test.com").get(requestPath("/")).willReturn(response()).build();

        Set<RequestResponsePair> pairs = simulation.getHoverflyData().getPairs();

        assertThat(pairs).hasSize(1);
        RequestResponsePair pair = pairs.iterator().next();
        assertThat(pair.getRequest().getDestination()).isEqualTo("www.my-test.com");
        assertThat(pair.getRequest().getScheme()).isEqualTo("http");

    }
}