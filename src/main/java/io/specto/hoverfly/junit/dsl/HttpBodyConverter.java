package io.specto.hoverfly.junit.dsl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interface for converting a java object into a http request body, and storing the appropriate content type header value
 */
public interface HttpBodyConverter {
    /**
     * Converts a given object into json, and returns application/json content type
     *
     * @param body the body of the request or response
     * @return the converter
     */
    static HttpBodyConverter json(final Object body) {
        return json(body, new ObjectMapper());
    }

    /**
     * Converts a given object into json, and returns application/json content type
     *
     * @param body         the request / response body
     * @param objectMapper objectMapper to use
     * @return converter
     */
    static HttpBodyConverter json(final Object body, final ObjectMapper objectMapper) {
        return new HttpBodyConverter() {
            @Override
            public String body() {
                try {
                    return objectMapper.writeValueAsString(body);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("Cannot marshall: " + body, e);
                }
            }

            @Override
            public String contentType() {
                return "application/json";
            }
        };
    }

    String body();

    String contentType();
}
