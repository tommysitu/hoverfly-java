package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import static javax.ws.rs.HttpMethod.*;

public class HoverflyStubService implements SimulationBuilder {

    private URL baseUrl;
    private Set<RequestResponsePair> pairs;
    private RequestResponsePair.Builder pairBuilder;


    private HoverflyStubService() {
    }

    HoverflyStubService(String baseUrl, Set<RequestResponsePair> pairs) {
        this.baseUrl = getURL(baseUrl);
        this.pairs = pairs;
    }

    public HoverflyStubResponse get(HoverflyRequestBuilder hoverflyRequestBuilder) {
        return stubRequest(hoverflyRequestBuilder, GET);
    }


    public HoverflyStubResponse delete(HoverflyRequestBuilder hoverflyRequestBuilder) {
        return stubRequest(hoverflyRequestBuilder, DELETE);
    }

    public HoverflyStubResponse put(HoverflyRequestBuilder hoverflyRequestBuilder) {
        return stubRequest(hoverflyRequestBuilder, PUT);
    }

    public HoverflyStubResponse post(HoverflyRequestBuilder hoverflyRequestBuilder) {
        return stubRequest(hoverflyRequestBuilder, POST);
    }

    public HoverflyStubService anotherService(String baseUrl) {
        this.baseUrl = getURL(baseUrl);
        return this;
    }

    private HoverflyStubResponse stubRequest(HoverflyRequestBuilder hoverflyRequestBuilder, String requestMethod) {
        RequestDetails requestDetails = hoverflyRequestBuilder
                .scheme(baseUrl.getProtocol())
                .destination(baseUrl.getHost())
                .method(requestMethod)
                .build();
        pairBuilder = new RequestResponsePair.Builder().withRequestDetails(requestDetails);
        return new HoverflyStubResponse(this);
    }

    @Override
    public Simulation build() {
        return new Simulation(new HoverflyData(pairs, new GlobalActions(Collections.emptyList())), new HoverflyMetaData());
    }

    void addPair(RequestResponsePair pair) {
        pairs.add(pair);
    }

    RequestResponsePair.Builder getPairBuilder() {
        return pairBuilder;
    }

    private URL getURL(String baseUrl) {
        try {
            URI uri = new URI(baseUrl);
            if (uri.getScheme() == null) {
                baseUrl = "http://" + baseUrl;
            }
            return new URL(baseUrl);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Invalid url", e);
        }
    }
}
