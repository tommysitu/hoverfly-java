package io.specto.hoverfly.junit.dsl;

public class HoverflyDsl {

    private HoverflyDsl() {
    }

    public static PairsBuilder service(final String baseUrl) {
        return new PairsBuilder(baseUrl, null);
    }
}
