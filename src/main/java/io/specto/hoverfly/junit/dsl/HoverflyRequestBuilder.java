package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.RequestDetails;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoverflyRequestBuilder {
    private static final String DEFAULT_SCHEME = "http";
    private String scheme = DEFAULT_SCHEME;
    private String path = "";
    private String method = "";
    private String destination;
    private String body = "";
    private MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
    private String query = "";
    private Map<String, List<String>> headers = new HashMap<>();

    private HoverflyRequestBuilder() {
    }

    public static HoverflyRequestBuilder requestPath(final String path) {
        return new HoverflyRequestBuilder().path(path);
    }

    public HoverflyRequestBuilder path(final String path) {
        this.path = path;
        return this;
    }

    public HoverflyRequestBuilder method(final String method) {
        this.method = method;
        return this;
    }

    public HoverflyRequestBuilder destination(final String destination) {
        this.destination = destination;
        return this;
    }

    public HoverflyRequestBuilder body(final String body) {
        this.body = body;
        return this;
    }

    public HoverflyRequestBuilder query(final String query) {
        this.query = query;
        return this;
    }

    private HoverflyRequestBuilder queryParam(final String key, final Object... values) {
        for(Object value : values) {
            queryParams.add(key, value.toString());
        }
        return this;
    }

    public HoverflyRequestBuilder headers(final String key, final String value) {
        headers.put(key, Collections.singletonList(value));
        return this;
    }

    public HoverflyRequestBuilder scheme(final String scheme) {
        this.scheme = scheme;
        return this;
    }

    public RequestDetails build() {
        // TODO Hoverfly only supports exact request query matching at the moment, will enable queryParams builder when the problem is resolved
//        query = queryParams.entrySet().stream()
//                .flatMap(e -> e.getValue().stream().map(v -> encodeUrl(e.getKey()) + "=" + encodeUrl(v)))
//                .collect(Collectors.joining("&"));
        return new RequestDetails(path, method, destination, scheme, query, body, headers);
    }

    private String encodeUrl(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
