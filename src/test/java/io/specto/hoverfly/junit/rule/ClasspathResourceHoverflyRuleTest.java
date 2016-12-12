package io.specto.hoverfly.junit.rule;

import org.junit.ClassRule;

public class ClasspathResourceHoverflyRuleTest {

    // tag::simulateModeQuickStart[]
    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode("test-service.json");
    // end::simulateModeQuickStart[]

}
