package io.specto.hoverfly.ruletest;

import org.junit.ClassRule;
import io.specto.hoverfly.junit.rule.HoverflyRule;

import static io.specto.hoverfly.junit.core.SimulationResource.classpath;

public class ClasspathResourceHoverflyRuleTest {

    // tag::simulateModeQuickStart[]
    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode("test-service.json");
    // end::simulateModeQuickStart[]

}
