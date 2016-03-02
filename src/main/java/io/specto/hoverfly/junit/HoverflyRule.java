/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * <p>
 * Copyright 2016-2016 SpectoLabs Ltd.
 */
package io.specto.hoverfly.junit;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.*;
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
    private Path binaryPath;

    private HoverflyRule(final String serviceDataResourceName, final int proxyPort, final int adminPort) {
        serviceDataUrl = getResource(serviceDataResourceName)
                .orElseThrow(() -> new IllegalArgumentException("Service data not found at " + serviceDataResourceName));

        this.proxyPort = proxyPort == 0 ? findUnusedPort() : proxyPort;
        this.adminPort = adminPort == 0 ? findUnusedPort() : adminPort;

        LOGGER.info("Setting proxy host to " + "localhost");
        System.setProperty("http.proxyHost", "localhost");

        LOGGER.info("Setting proxy proxyPort to " + this.proxyPort);
        System.setProperty("http.proxyPort", String.valueOf(this.proxyPort));

        LOGGER.info("Setting admin port to " + this.adminPort);
    }

    public static Builder builder(final String serviceData) {
        return new Builder(serviceData);
    }

    @Override
    protected void before() throws Throwable {

        final String binaryPath = String.format(BINARY_PATH, getOs(), getArchitectureType());
        LOGGER.info("Selecting the following binary based on the current operating system: " + binaryPath);

        this.binaryPath = extractBinary(binaryPath);

        LOGGER.info("Executing binary at " + this.binaryPath);

        startedProcess = new ProcessExecutor()
                .command("./" + this.binaryPath.getFileName(),
                        "-import", serviceDataUrl.getPath(),
                        "-wipedb",
                        "-pp", String.valueOf(proxyPort),
                        "-ap", String.valueOf(adminPort))
                .redirectOutput(Slf4jStream.of(LOGGER).asInfo())
                .directory(this.binaryPath.getParent().toFile())
                .start();

        waitForHoverflyToStart();
    }

    private void waitForHoverflyToStart() {
        final Instant now = Instant.now();
        Stream.generate(this::hoverflyHasStarted)
                .peek(b -> {
                    if (Duration.between(now, Instant.now()).getSeconds() > BOOT_TIMEOUT_SECONDS) {
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
        final File database = binaryPath.getParent().resolve("requests.db").toFile();
        if (database.exists()) database.delete();
        final File binary = binaryPath.toFile();
        if (binary.exists()) database.delete();
    }

    public static class Builder {
        private final String serviceDataResourceName;
        private int proxyPort = 0;
        private int adminPort = 0;

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
