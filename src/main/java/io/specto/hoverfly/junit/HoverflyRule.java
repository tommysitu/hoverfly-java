package io.specto.hoverfly.junit;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jOutputStream;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.collect.Sets.newHashSet;
import static io.specto.hoverfly.junit.HoverflyRuleUtils.getResource;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;

public class HoverflyRule extends ExternalResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoverflyRule.class);
    private static final String BINARY_PATH = "hoverfly_%s_%s";

    private final URL serviceDataUrl;
    private StartedProcess startedProcess;

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

        final String binaryPath = String.format(BINARY_PATH, HoverflyRuleUtils.getOs(), HoverflyRuleUtils.getArchitectureType());
        LOGGER.info("Selecting the following binary based on the current operating system: " + binaryPath);

        final Path temporaryHoverflyPath = extractBinary(binaryPath);

        LOGGER.info("Executing binary at " + temporaryHoverflyPath);

        startedProcess = new ProcessExecutor()
                .command("./" + temporaryHoverflyPath.getFileName(), "-import", serviceDataUrl.getPath(), "-wipedb")
                .redirectOutput(Slf4jStream.of(LOGGER).asInfo())
                .directory(temporaryHoverflyPath.getParent().toFile())
                .start();
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
}
