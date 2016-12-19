package io.specto.hoverfly.junit.dsl;

import static io.specto.hoverfly.junit.dsl.ResponseBuilder.response;

/**
 * Wrapper around a {@link ResponseBuilder} for building common types of responses
 */
public class ResponseCreators {

    private ResponseCreators() {
    }

    /**
     * Builds a 200 response with a given location header value
     *
     * @param locationHeaderValue the value of the location header
     * @return builder with the given fields set
     */
    public static ResponseBuilder created(final String locationHeaderValue) {
        return response()
                .status(201)
                .header("Location", locationHeaderValue);
    }

    /**
     * Builds a 200 response with the following content
     * @param body the body sent in the response
     * @param contentType the content type header value
     * @return builder with the given fields set
     */
    public static ResponseBuilder success(final String body, final String contentType) {
        return response()
                .status(200)
                .body(body)
                .header("Content-Type", contentType);
    }

    /**
     * Builds a 200 response
     * @return builder with the given fields set
     */
    public static ResponseBuilder success() {
        return response().status(200);
    }

    /**
     * Builds a 204 response
     * @return builder with the given fields set
     */
    public static ResponseBuilder noContent() {
        return response().status(204);
    }

    /**
     * Builds a 400 response
     * @return builder with the given fields set
     */
    public static ResponseBuilder badRequest() {
        return response().status(400);
    }

    /**
     * Builds a 500 response
     * @return builder with the given fields set
     */
    public static ResponseBuilder serverError() {
        return response().status(500);
    }

    /**
     * Builds a 403 response
     * @return builder with the given fields set
     */
    public static ResponseBuilder forbidden() {
        return response().status(403);
    }

    /**
     * Builds a 401 response
     * @return builder with the given fields set
     */
    public static ResponseBuilder unauthorised() {
        return response().status(401);
    }
}
