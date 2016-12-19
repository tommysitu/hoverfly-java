package io.specto.hoverfly.junit.dsl;

import static io.specto.hoverfly.junit.dsl.ResponseBuilder.response;

public class ResponseCreators {
    public static ResponseBuilder created(final String locationHeaderValue) {
        return response()
                .status(201)
                .header("Location", locationHeaderValue);
    }

    public static ResponseBuilder success(final String body, final String contentType) {
        return response()
                .status(200)
                .body(body)
                .header("Content-Type", contentType);
    }

    public static ResponseBuilder success() {
        return response()
                .status(200);
    }

    public static ResponseBuilder noContent() {
        return response()
                .status(204);
    }
}
