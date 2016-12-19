package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseBuilder {

    private final Map<String, List<String>> headers = new HashMap<>();
    private String body = "";
    private int status = 200;


    private ResponseBuilder() {
    }

    public static ResponseBuilder response() {
        return new ResponseBuilder();
    }

    public ResponseBuilder body(final String body) {
        this.body = body;
        return this;
    }

    public ResponseBuilder status(final int status) {
        this.status = status;
        return this;
    }

    public ResponseBuilder header(final String key, final String value) {
        this.headers.put(key, Collections.singletonList(value));
        return this;
    }

    public Response build() {
        return new Response(status, body, false, headers);
    }
}

