package io.specto.hoverfly.junit.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RequestResponsePair {
    private final RequestMatcher request;
    private final Response response;

    @JsonCreator
    public RequestResponsePair(@JsonProperty("request") RequestMatcher request,
                               @JsonProperty("response") Response response) {
        this.request = request;
        this.response = response;
    }

    public RequestMatcher getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "response");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "response");
    }
}