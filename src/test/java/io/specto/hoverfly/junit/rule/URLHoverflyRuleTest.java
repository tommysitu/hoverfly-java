package io.specto.hoverfly.junit.rule;

import io.specto.hoverfly.webserver.ImportTestWebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;

import java.net.URL;

public class URLHoverflyRuleTest extends BaseHoverflyRuleTest {

    private static URL webServerUri;

    // tag::urlExample[]
    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode(webServerUri);
    // end::urlExample[]


    @BeforeClass
    public static void setUp() throws Exception {
        webServerUri = ImportTestWebServer.run();
    }


    @AfterClass
    public static void tearDown() throws Exception {
        ImportTestWebServer.terminate();
    }
}
