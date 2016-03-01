package io.specto.hoverfly.junit;

import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.getBinaryUrl;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.getResource;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;

public class HoverflyRule extends ExternalResource {

    private static final String HOVERFLY_DB_PATH = "requests.db";

    private final URL serviceDataUrl;
    private final URL hoverflyUrl;

    private Process hoverflyProcess;
    private Optional<URL> databaseUrl = Optional.empty();

    public HoverflyRule(final String serviceDataResourceName) {
        serviceDataUrl = getResource(serviceDataResourceName)
                .orElseThrow(() -> new IllegalArgumentException("Service data not found at " + serviceDataResourceName));
        hoverflyUrl = getBinaryUrl();
        databaseUrl = getResource(HOVERFLY_DB_PATH);
        System.setProperty("http.proxyHost", "localhost");
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
        if (databaseUrl.isPresent()) {
            try {
                tearDownDatabaseIfExists();
            } catch (IOException e) {
                throw new RuntimeException("Unable to delete hoveryfly database", e);
            }
        }
    }

    private void tearDownDatabaseIfExists() throws IOException {
        databaseUrl = getResource(HOVERFLY_DB_PATH);
        if (databaseUrl.isPresent()) {
            Files.delete(Paths.get(databaseUrl.get().getPath()));
        }
    }
}
