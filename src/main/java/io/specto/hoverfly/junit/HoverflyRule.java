package io.specto.hoverfly.junit;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.collect.Sets.newHashSet;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.getBinaryUrl;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.getResource;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;

public class HoverflyRule extends ExternalResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoverflyRule.class);

    private final URL serviceDataUrl;
    private Process hoverflyProcess;

    public HoverflyRule(final String serviceDataResourceName) {
        serviceDataUrl = getResource(serviceDataResourceName)
                .orElseThrow(() -> new IllegalArgumentException("Service data not found at " + serviceDataResourceName));

        LOGGER.info("Setting proxy host to " + "localhost");
        System.setProperty("http.proxyHost", "localhost");

        LOGGER.info("Setting proxy port to " + "8500");
        System.setProperty("http.proxyPort", "8500");
    }

    @Override
    protected void before() throws Throwable {

        final URL sourceHoverflyUrl = getBinaryUrl();
        final Path temporaryHoverflyPath = Files.createTempFile("hoverfly-binary", "");
        FileUtils.copyURLToFile(sourceHoverflyUrl, temporaryHoverflyPath.toFile());
        Files.setPosixFilePermissions(temporaryHoverflyPath, newHashSet(OWNER_EXECUTE, OWNER_READ));

        LOGGER.info("Executing binary at " + temporaryHoverflyPath);

        final ProcessBuilder builder = new ProcessBuilder()
                .inheritIO()
                .directory(temporaryHoverflyPath.getParent().toFile())
                .command("./" + temporaryHoverflyPath.getFileName(), "-import", serviceDataUrl.getPath(), "-wipedb");

        hoverflyProcess = builder.start();
    }

    @Override
    protected void after() {
        hoverflyProcess.destroy();
    }
}
