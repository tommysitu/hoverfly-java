package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.ResponseDetails;

public class HoverflyStubResponse {

    private HoverflyStubService hoverflyStubService;

    private HoverflyStubResponse() {
    }

    public HoverflyStubResponse(HoverflyStubService hoverflyStubService) {
        this.hoverflyStubService = hoverflyStubService;
    }

    public HoverflyStubService willReturn(HoverflyResponseBuilder hoverflyResponseBuilder) {
        ResponseDetails responseDetails = hoverflyResponseBuilder.build();
        RequestResponsePair pair = hoverflyStubService.getPairBuilder()
                .withResponseDetails(responseDetails)
                .build();
        hoverflyStubService.addPair(pair);
        return hoverflyStubService;
    }
}
