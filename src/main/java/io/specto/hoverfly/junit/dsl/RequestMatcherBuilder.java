package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.RequestMatcher;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestMatcherBuilder {

    private final PairsBuilder invoker;
    private final String method;
    private final String scheme;
    private final String destination;
    private final String path;
    private final MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    private final Map<String, List<String>> headers = new HashMap<>();
    private String body = "";
    private String query = "";

    private RequestMatcherBuilder(final PairsBuilder invoker, final String method, final String scheme, final String destination, final String path) {
        this.invoker = invoker;
        this.method = method;
        this.scheme = scheme;
        this.destination = destination;
        this.path = path;
    }

    static RequestMatcherBuilder requestMatcherBuilder(final PairsBuilder invoker, final String method, final String scheme, final String destination, final String path) {
        return new RequestMatcherBuilder(invoker, method, scheme, destination, path);
    }


    public RequestMatcherBuilder body(final String body) {
        this.body = body;
        return this;
    }

    public RequestMatcherBuilder query(final String query) {
        this.query = query;
        return this;
    }

    private RequestMatcherBuilder queryParam(final String key, final Object... values) {
        for(Object value : values) {
            queryParams.add(key, value.toString());
        }
        return this;
    }

    public RequestMatcherBuilder header(final String key, final String value) {
        headers.put(key, Collections.singletonList(value));
        return this;
    }

    private RequestMatcher build() {
        // TODO Hoverfly only supports exact request query matching at the moment, will enable queryParams builder when the problem is resolved
//        query = queryParams.entrySet().stream()
//                .flatMap(e -> e.getValue().stream().map(v -> encodeUrl(e.getKey()) + "=" + encodeUrl(v)))
//                .collect(Collectors.joining("&"));
        return new RequestMatcher(path, method, destination, scheme, query, body, headers);
    }

//    private String encodeUrl(String str) {
//        try {
//            return URLEncoder.encode(str, "UTF-8").replaceAll("\\+", "%20");
//        } catch (UnsupportedEncodingException e) {
//            throw new UnsupportedOperationException(e);
//        }
//    }


    public PairsBuilder willReturn(final ResponseBuilder responseBuilder) {
        return invoker.addPair(new RequestResponsePair(this.build(), responseBuilder.build()));
    }
}
