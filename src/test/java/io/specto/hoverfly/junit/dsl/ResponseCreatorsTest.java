package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.Response;
import org.junit.Test;

import static io.specto.hoverfly.assertions.Assertions.assertThat;
import static io.specto.hoverfly.assertions.ResponseAssert.header;

public class ResponseCreatorsTest {
    @Test
    public void shouldBuildCreatedResponse() {
        // When
        final Response response = ResponseCreators.created("http://location.com").build();

        // Then
        assertThat(response)
                .hasStatus(201)
                .hasNoBody()
                .hasOnlyHeaders(header("Location", "http://location.com"));
    }

    @Test
    public void shouldBuildSuccessResponseWithBodyAndContentType() {
        // When
        final Response response = ResponseCreators.success("body", "contentType").build();

        // Then
        assertThat(response)
                .hasStatus(200)
                .hasBody("body")
                .hasOnlyHeaders(header("Content-Type", "contentType"));
    }

    @Test
    public void shouldBuildSuccessResponse() {
        // When
        final Response response = ResponseCreators.success().build();

        // Then
        assertThat(response)
                .hasStatus(200)
                .hasNoBody()
                .hasNoHeaders();
    }

    @Test
    public void shouldBuildNoContentResponse() {
        // When
        final Response response = ResponseCreators.noContent().build();

        // Then
        assertThat(response)
                .hasStatus(204)
                .hasNoBody()
                .hasNoHeaders();
    }

}