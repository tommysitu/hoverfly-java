package io.specto.hoverfly.junit;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.getResource;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;

public class HoverflyRule extends ExternalResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoverflyRule.class);
    private static final String BINARY_PATH = "hoverfly_%s_%s";
    private static final int BOOT_TIMEOUT_SECONDS = 3;
    private static final String HEALTH_CHECK_URL = "http://localhost:%s/stats";

    private final URL serviceDataUrl;
    private final int proxyPort;
    private final int adminPort;
    private StartedProcess startedProcess;

    private HoverflyRule(final String serviceDataResourceName, final int proxyPort, final int adminPort) {
        serviceDataUrl = getResource(serviceDataResourceName)
                .orElseThrow(() -> new IllegalArgumentException("Service data not found at " + serviceDataResourceName));

        this.proxyPort = proxyPort;
        this.adminPort = adminPort;

        LOGGER.info("Setting proxy host to " + "localhost");
        System.setProperty("http.proxyHost", "localhost");

        LOGGER.info("Setting proxy proxyPort to " + proxyPort);
        System.setProperty("http.proxyPort", String.valueOf(proxyPort));
    }

    @Override
    protected void before() throws Throwable {

        final String binaryPath = String.format(BINARY_PATH, HoverflyRuleUtils.getOs(), HoverflyRuleUtils.getArchitectureType());
        LOGGER.info("Selecting the following binary based on the current operating system: " + binaryPath);

        final Path temporaryHoverflyPath = extractBinary(binaryPath);

        LOGGER.info("Executing binary at " + temporaryHoverflyPath);

        startedProcess = new ProcessExecutor()
                .command("./" + temporaryHoverflyPath.getFileName(),
                        "-import", serviceDataUrl.getPath(),
                        "-wipedb",
                        "-pp", String.valueOf(proxyPort),
                        "-ap", String.valueOf(adminPort))

                .redirectOutput(Slf4jStream.of(LOGGER).asInfo())
                .directory(temporaryHoverflyPath.getParent().toFile())
                .start();

        waitForHoverflyToStart();
    }

    private void waitForHoverflyToStart() {
        final Instant now = Instant.now();
        Stream.generate(this::hoverflyHasStarted)
                .peek(b -> {
                    if(Duration.between(now, Instant.now()).getSeconds() > BOOT_TIMEOUT_SECONDS) {
                        throw new IllegalStateException("Hoverfly has not become healthy within " + BOOT_TIMEOUT_SECONDS + " seconds");
                    }
                })
                .anyMatch(b -> b.equals(true));
    }

    private boolean hoverflyHasStarted() {
        boolean healthy = false;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(String.format(HEALTH_CHECK_URL, adminPort)).openConnection();
            con.setRequestMethod("GET");
            LOGGER.debug("Hoverfly health check status code is: " + String.valueOf(con.getResponseCode()));
            healthy = con.getResponseCode() == 200;
            if (!healthy) {
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.debug("Exception curling health check", e);
        }
        return healthy;
    }

    private Path extractBinary(final String binaryPath) throws IOException {
        final URL sourceHoverflyUrl = getResource(binaryPath).get();
        final Path temporaryHoverflyPath = Files.createTempFile("hoverfly-binary", "");
        FileUtils.copyURLToFile(sourceHoverflyUrl, temporaryHoverflyPath.toFile());
        Files.setPosixFilePermissions(temporaryHoverflyPath, newHashSet(OWNER_EXECUTE, OWNER_READ));
        return temporaryHoverflyPath;
    }

    @Override
    protected void after() {
        LOGGER.info("Destroying hoverfly process");
        startedProcess.getProcess().destroy();
    }

    public static Builder builder(final String serviceData) {
        return new Builder(serviceData);
    }

    public static class Builder {
        private int proxyPort = 8500;
        private int adminPort = 8888;
        private final String serviceDataResourceName;

        public Builder(final String serviceDataResourceName) {
            this.serviceDataResourceName = serviceDataResourceName;
        }

        public Builder withProxyPort(int proxyPort) {
            this.proxyPort = proxyPort;
            return this;
        }

        public Builder withAdminPort(int adminPort) {
            this.adminPort = adminPort;
            return this;
        }

        public HoverflyRule build() {
            return new HoverflyRule(serviceDataResourceName, proxyPort, adminPort);
        }
    }
}
