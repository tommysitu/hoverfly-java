/*
  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
  the License. You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
  specific language governing permissions and limitations under the License.

  Copyright 2016-2016 SpectoLabs Ltd.
 */
package io.specto.hoverfly.junit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static io.specto.hoverfly.junit.HoverflyMode.CAPTURE;
import static io.specto.hoverfly.junit.HoverflyMode.SIMULATE;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.*;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.util.Arrays.asList;

public class HoverflyRule extends ExternalResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoverflyRule.class);
    private static final String BINARY_PATH = "hoverfly_v0.8.1_%s_%s";
    private static final int BOOT_TIMEOUT_SECONDS = 10;
    private static final String HEALTH_CHECK_URL = "http://localhost:%s/api/stats";
    private static final String RECORDS_URL = "http://localhost:%s/api/records";
    private final int proxyPort;
    private final int adminPort;
    private final HoverflyMode hoverflyMode;
    private final boolean proxyLocalHost;
    private String serviceDataURI;
    private StartedProcess startedProcess;
    private Path binaryPath;

    HoverflyRule(final String dateResourcePath, final int proxyPort, final int adminPort, final HoverflyMode hoverflyMode, final boolean proxyLocalHost) throws URISyntaxException, IOException {
        this(proxyPort, adminPort, hoverflyMode, proxyLocalHost);
        final URI pathToData = hoverflyMode == SIMULATE ? findResourceOnClasspath(dateResourcePath) : createFileRelativeToClasspath(dateResourcePath);
        serviceDataURI = Paths.get(pathToData).toString();
    }

    HoverflyRule(final URL dataResourcePath, final int proxyPort, final int adminPort, final HoverflyMode hoverflyMode, final boolean proxyLocalHost) {
        this(proxyPort, adminPort, hoverflyMode, proxyLocalHost);
        serviceDataURI = dataResourcePath.toString();
    }

    private HoverflyRule(final int proxyPort, final int adminPort, final HoverflyMode hoverflyMode, final boolean proxyLocalHost) {
        this.proxyPort = proxyPort == 0 ? findUnusedPort() : proxyPort;
        this.adminPort = adminPort == 0 ? findUnusedPort() : adminPort;
        this.hoverflyMode = hoverflyMode;
        this.proxyLocalHost = proxyLocalHost;

        LOGGER.info("Setting proxy host to " + "localhost");
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("https.proxyHost", "localhost");

        if (this.proxyLocalHost) {
            System.setProperty("http.nonProxyHosts", "");
        } else {
            System.setProperty("http.nonProxyHosts", "local|*.local|169.254/16|*.169.254/16");
        }

        LOGGER.info("Setting proxy proxyPort to " + this.proxyPort);
        System.setProperty("http.proxyPort", String.valueOf(this.proxyPort));
        System.setProperty("https.proxyPort", String.valueOf(this.proxyPort));

        try {
            setHoverflyTrustStore();
        } catch (Exception e) {
            throw new RuntimeException("Unable to set hoverfly trust store", e);
        }

        LOGGER.info("Setting admin port to " + this.adminPort + "\n");
    }

    public static SimulateBuilder buildFromClassPathResource(final String serviceDataClasspath) {
        return new SimulateBuilder(serviceDataClasspath);
    }

    public static SimulateBuilder buildFromUrl(final String serviceDataUrl) {
        try {
            return new SimulateBuilder(new URL(serviceDataUrl));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to build URL", e);
        }
    }

    public static CaptureBuilder inCaptureMode(final String outputDirectory) {
        return new CaptureBuilder(outputDirectory);
    }

    @Override
    protected void before() throws Throwable {

        if (hoverflyMode == CAPTURE) {
            final Path path = Paths.get(serviceDataURI);
            Files.deleteIfExists(path);
        }

        final String binaryName = String.format(BINARY_PATH, getOs(), getArchitectureType()) + (SystemUtils.IS_OS_WINDOWS ? ".exe" : "");
        LOGGER.info("Selecting the following binary based on the current operating system: " + binaryName);

        this.binaryPath = extractBinary(binaryName);

        LOGGER.info("Executing binary at " + this.binaryPath);

        final List<String> commands = new ArrayList<>();

        commands.add(this.binaryPath.toString());
        commands.add("-db");
        commands.add("memory");
        commands.add("-pp");
        commands.add(String.valueOf(proxyPort));
        commands.add("-ap");
        commands.add(String.valueOf(adminPort));

        if (hoverflyMode == CAPTURE) {
            commands.add("-capture");
        } else {
            commands.add("-import");
            commands.add(serviceDataURI);
        }

        startedProcess = new ProcessExecutor()
                .command(commands)
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

    private Path extractBinary(final String binaryName) throws IOException, URISyntaxException {
        final URI sourceHoverflyUrl = findResourceOnClasspath(binaryName);
        final Path temporaryHoverflyPath = Files.createTempFile(binaryName, "");
        LOGGER.info("Storing binary in temporary directory " + temporaryHoverflyPath);
        final File temporaryHoverflyFile = temporaryHoverflyPath.toFile();
        FileUtils.copyURLToFile(sourceHoverflyUrl.toURL(), temporaryHoverflyFile);
        if (SystemUtils.IS_OS_WINDOWS) {
            temporaryHoverflyFile.setExecutable(true);
            temporaryHoverflyFile.setReadable(true);
            temporaryHoverflyFile.setWritable(true);
        } else {
            Files.setPosixFilePermissions(temporaryHoverflyPath, new HashSet<>(asList(OWNER_EXECUTE, OWNER_READ)));
        }

        return temporaryHoverflyPath;
    }

    @Override
    protected void after() {

        if (hoverflyMode == CAPTURE) {
            LOGGER.info("Storing captured data");
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(String.format(RECORDS_URL, adminPort)).openConnection();
                con.setRequestMethod("GET");
                final Path path = Paths.get(serviceDataURI);
                Files.copy(con.getInputStream(), path);
            } catch (IOException e) {
                throw new RuntimeException("Unable to persist captured data", e);
            }
        }

        LOGGER.info("Destroying hoverfly process");
        startedProcess.getProcess().destroy();
        final File binary = binaryPath.toFile();
        if (binary.exists()) binary.delete();
    }

    public Path getBinaryPath() {
        return binaryPath;
    }
}
