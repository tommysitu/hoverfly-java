package io.specto.hoverfly.junit.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.ws.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestResponsePair {
    private final RequestDetails request;
    private final ResponseDetails response;

    @JsonCreator
    public RequestResponsePair(@JsonProperty("request") RequestDetails request,
                               @JsonProperty("response") ResponseDetails response) {
        this.request = request;
        this.response = response;
    }

    public RequestDetails getRequest() {
        return request;
    }

    public ResponseDetails getResponse() {
        return response;
    }

    public static class Builder {

        private RequestDetails requestDetails;
        private ResponseDetails responseDetails;

        public Builder withRequestDetails(RequestDetails requestDetails) {
            this.requestDetails = requestDetails;
            return this;
        }

        public Builder withResponseDetails(ResponseDetails responseDetails) {
            this.responseDetails = responseDetails;
            return this;
        }

        public RequestResponsePair build() {
            return new RequestResponsePair(requestDetails, responseDetails);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}