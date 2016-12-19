package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import org.junit.Test;

import java.util.Set;

import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseBuilder.response;
import static org.assertj.core.api.Assertions.assertThat;

public class PairsBuilderTest {

    @Test
    public void shouldExtractHttpsUrlScheme() throws Exception {

        final Set<RequestResponsePair> pairs = service("https://www.my-test.com").get("/").willReturn(response()).getPairs();

        assertThat(pairs).hasSize(1);
        RequestResponsePair pair = pairs.iterator().next();
        assertThat(pair.getRequest().getDestination()).isEqualTo("www.my-test.com");
        assertThat(pair.getRequest().getScheme()).isEqualTo("https");
    }

    @Test
    public void shouldDefaultToHttpScheme() throws Exception {
        final Set<RequestResponsePair> pairs = service("www.my-test.com").get("/").willReturn(response()).getPairs();

        assertThat(pairs).hasSize(1);
        RequestResponsePair pair = pairs.iterator().next();
        assertThat(pair.getRequest().getDestination()).isEqualTo("www.my-test.com");
        assertThat(pair.getRequest().getScheme()).isEqualTo("http");

    }

    @Test
    public void shouldExtractHttpScheme() throws Exception {
        final Set<RequestResponsePair> pairs = service("http://www.my-test.com").get("/").willReturn(response()).getPairs();

        assertThat(pairs).hasSize(1);
        RequestResponsePair pair = pairs.iterator().next();
        assertThat(pair.getRequest().getDestination()).isEqualTo("www.my-test.com");
        assertThat(pair.getRequest().getScheme()).isEqualTo("http");

    }
}