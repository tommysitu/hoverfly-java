package io.specto.hoverfly.junit.dsl;

import java.util.HashSet;

public class HoverflyDsl {

    public static HoverflyStubService service(String baseUrl) {
        return new HoverflyStubService(baseUrl, new HashSet<>());
    }
}
