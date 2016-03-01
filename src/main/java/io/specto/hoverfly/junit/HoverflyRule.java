package io.specto.hoverfly.junit;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static io.specto.hoverfly.junit.HoverflyRuleUtils.getBinaryUrl;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.getResource;

public class HoverflyRule extends ExternalResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoverflyRule.class);

    private static final String HOVERFLY_DB_PATH = "requests.db";

    private final URL serviceDataUrl;
    private final URL hoverflyUrl;

    private Process hoverflyProcess;

    public HoverflyRule(final String serviceDataResourceName) {
        serviceDataUrl = getResource(serviceDataResourceName)
                .orElseThrow(() -> new IllegalArgumentException("Service data not found at " + serviceDataResourceName));
        hoverflyUrl = getBinaryUrl();

        LOGGER.info("Setting proxy host to " + "localhost");
        System.setProperty("http.proxyHost", "localhost");

        LOGGER.info("Setting proxy port to " + "8500");
        System.setProperty("http.proxyPort", "8500");
    }

    @Override
    protected void before() throws Throwable {

        tearDownDatabaseIfExists();

        final Path pathToHoverfly = Paths.get(hoverflyUrl.toURI());
        final Path directoryOfBinary = pathToHoverfly.getParent();
        final Path binaryName = pathToHoverfly.getFileName();

        final ProcessBuilder builder = new ProcessBuilder()
                .inheritIO()
                .directory(directoryOfBinary.toFile())
                .command("./" + binaryName.toString(), "-import", serviceDataUrl.getPath(), "-wipedb");

        hoverflyProcess = builder.start();
    }

    @Override
    protected void after() {
        hoverflyProcess.destroy();
        try {
            tearDownDatabaseIfExists();
        } catch (IOException e) {
            throw new RuntimeException("Unable to delete hoveryfly database", e);
        }
    }

    private void tearDownDatabaseIfExists() throws IOException {
        final Optional<URL> databaseUrl = getResource(HOVERFLY_DB_PATH);
        if (databaseUrl.isPresent()) {
            LOGGER.info("Tearing down hoverfly database at " + databaseUrl.get());
            Files.delete(Paths.get(databaseUrl.get().getPath()));
        } else {
            LOGGER.info("Did not find hoverfly database at " + HOVERFLY_DB_PATH);
        }
    }
}
