package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import jersey.repackaged.com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static io.specto.hoverfly.junit.dsl.RequestMatcherBuilder.requestMatcherBuilder;
import static io.specto.hoverfly.junit.dsl.RequestMethod.*;

public class PairsBuilder implements Iterator<PairsBuilder> {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String SEPARATOR = "://";

    private final String destination;
    private final String scheme;
    private final Set<RequestResponsePair> pairs = new HashSet<>();
    private final PairsBuilder previousService;

    PairsBuilder(String baseUrl, PairsBuilder previousService) {
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

        this.previousService = previousService;
    }

    public RequestMatcherBuilder get(final String path) {
        return requestMatcherBuilder(this, GET, scheme, destination, path);
    }

    public RequestMatcherBuilder delete(final String path) {
        return requestMatcherBuilder(this, DELETE, scheme, destination, path);
    }

    public RequestMatcherBuilder put(final String path) {
        return requestMatcherBuilder(this, PUT, scheme, destination, path);
    }

    public RequestMatcherBuilder post(final String path) {
        return requestMatcherBuilder(this, POST, scheme, destination, path);
    }

    PairsBuilder addPair(final RequestResponsePair requestResponsePair) {
        this.pairs.add(requestResponsePair);
        return this;
    }

    public Set<RequestResponsePair> getPairs() {
        return ImmutableSet.copyOf(pairs);
    }

    public PairsBuilder service(final String baseUrl) {
        return new PairsBuilder(baseUrl, this);
    }

    @Override
    public boolean hasNext() {
        return previousService != null;
    }

    @Override
    public PairsBuilder next() {
        return previousService;
    }
}
