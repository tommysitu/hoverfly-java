package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A builder for building responses
 *
 * @see ResponseCreators
 */
public class ResponseBuilder {

    private final Map<String, List<String>> headers = new HashMap<>();
    private String body = "";
    private int status = 200;


    private ResponseBuilder() {
    }

    /**
     * Instantiates a new instance
     * @return the builder
     */
    public static ResponseBuilder response() {
        return new ResponseBuilder();
    }

    /**
     * Sets the body
     * @param body body of the response
     * @return this
     */
    public ResponseBuilder body(final String body) {
        this.body = body;
        return this;
    }

    /**
     * Sets the status
     * @param status status of the response
     * @return this
     */
    public ResponseBuilder status(final int status) {
        this.status = status;
        return this;
    }

    /**
     * Sets a header
     * @param key header name
     * @param value header value
     * @return this
     */
    public ResponseBuilder header(final String key, final String value) {
        this.headers.put(key, Collections.singletonList(value));
        return this;
    }

    /**
     * Builds a {@link Response}
     * @return the response
     */
    public Response build() {
        return new Response(status, body, false, headers);
    }
}

