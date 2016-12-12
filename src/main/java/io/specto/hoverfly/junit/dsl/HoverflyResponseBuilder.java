package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.ResponseDetails;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoverflyResponseBuilder {

    private String body = "";
    private int status = 200;
    private Map<String, List<String>> headers = new HashMap<>();


    private HoverflyResponseBuilder() {
    }

    public HoverflyResponseBuilder body(final String body) {
        this.body = body;
        return this;
    }

    public HoverflyResponseBuilder status(final int status) {
        this.status = status;
        return this;
    }

    public HoverflyResponseBuilder header(final String key, final String value) {
        this.headers.put(key, Collections.singletonList(value));
        return this;
    }

    public ResponseDetails build() {
        return new ResponseDetails(status, body, false, headers);
    }

    public static HoverflyResponseBuilder response() {
        return new HoverflyResponseBuilder();
    }
}

