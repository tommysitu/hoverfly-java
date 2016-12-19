package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import jersey.repackaged.com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

import static io.specto.hoverfly.junit.dsl.RequestMatcherBuilder.requestMatcherBuilder;
import static io.specto.hoverfly.junit.dsl.RequestMethod.*;

/**
 * Used as part of the DSL for creating a {@link RequestResponsePair} used within a Hoverfly Simulation.  Each builder is locked to a single base URL.
 * If you want to use another base URL, you must chain things together by calling {@link StubServiceBuilder#anotherService}
 */
public class StubServiceBuilder {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String SEPARATOR = "://";

    private final String destination;
    private final String scheme;
    private final Set<RequestResponsePair> requestResponsePairs = new HashSet<>();

    /**
     * Instantiates builder for a given base URL
     *
     * @param baseUrl         the base URL you want all of these requestResponsePairs to use
     */
    StubServiceBuilder(String baseUrl) {
        if (baseUrl.startsWith(HTTPS + SEPARATOR)) {
            this.scheme = HTTPS;
            this.destination = baseUrl.substring(HTTPS.length() + SEPARATOR.length(), baseUrl.length());
        } else if (baseUrl.startsWith(HTTP + SEPARATOR)) {
            this.scheme = HTTP;
            this.destination = baseUrl.substring(HTTP.length() + SEPARATOR.length(), baseUrl.length());
        } else {
            this.scheme = HTTP;
            this.destination = baseUrl;
        }
    }

    /**
     * Creating a GET request matcher
     *
     * @param path the path you want the matcher to have
     * @return a builder for the response to send when matching
     * @see RequestMatcherBuilder
     */
    public RequestMatcherBuilder get(final String path) {
        return requestMatcherBuilder(this, GET, scheme, destination, path);
    }

    /**
     * Creating a DELETE request matcher
     *
     * @param path the path you want the matcher to have
     * @return a builder for the response to send when matching
     * @see RequestMatcherBuilder
     */
    public RequestMatcherBuilder delete(final String path) {
        return requestMatcherBuilder(this, DELETE, scheme, destination, path);
    }

    /**
     * Creating a PUT request matcher
     *
     * @param path the path you want the matcher to have
     * @return a builder for the response to send when matching
     * @see RequestMatcherBuilder
     */
    public RequestMatcherBuilder put(final String path) {
        return requestMatcherBuilder(this, PUT, scheme, destination, path);
    }

    /**
     * Creating a POST request matcher
     *
     * @param path the path you want the matcher to have
     * @return a builder for the response to send when matching
     * @see RequestMatcherBuilder
     */
    public RequestMatcherBuilder post(final String path) {
        return requestMatcherBuilder(this, POST, scheme, destination, path);
    }

    /**
     * Adds a pair to this builder.  Called by the {@link RequestMatcherBuilder#willReturn} in order for the DSL to be expressive such as:
     * <p>
     * <pre>
     *
     * pairsBuilder.method("/some/path").willReturn(created()).method("/some/other/path").willReturn(noContent())
     * <pre/>
     * @param requestResponsePair
     * @return
     */
    StubServiceBuilder addRequestResponsePair(final RequestResponsePair requestResponsePair) {
        this.requestResponsePairs.add(requestResponsePair);
        return this;
    }

    /**
     * Used for retrieving all the requestResponsePairs that the builder contains
     *
     * @return
     */
    public Set<RequestResponsePair> getRequestResponsePairs() {
        return ImmutableSet.copyOf(requestResponsePairs);
    }
}
