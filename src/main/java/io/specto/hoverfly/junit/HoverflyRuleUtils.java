package io.specto.hoverfly.junit;

import com.google.common.base.MoreObjects;
import com.google.common.io.Resources;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Optional;

class HoverflyRuleUtils {

    private static final String DARWIN = "darwin";
    private static final String WINDOWS = "windows";
    private static final String LINUX = "linux";
    private static final String ARCH_AMD64 = "amd64";
    private static final String ARCH_386 = "386";

    public static Optional<URL> getResource(String resourceName) {
        ClassLoader loader = MoreObjects.firstNonNull(
                Thread.currentThread().getContextClassLoader(),
                Resources.class.getClassLoader());
        return Optional.ofNullable(loader.getResource(resourceName));
    }

    public static String getOs() {
        if (SystemUtils.IS_OS_MAC) {
            return DARWIN;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        } else if (SystemUtils.IS_OS_LINUX) {
            return LINUX;
        } else {
            throw new UnsupportedOperationException(SystemUtils.OS_NAME + " is not currently supported");
        }
    }

    public static String getArchitectureType() {
        return SystemUtils.OS_ARCH.contains("64") ? ARCH_AMD64 : ARCH_386;
    }

    public static int findUnusedPort() {
        try(final ServerSocket serverSocket = new ServerSocket(0)){
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Cannot find available port", e);
        }
    }
}
