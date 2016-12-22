/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this classpath except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2016-2016 SpectoLabs Ltd.
 */
package io.specto.hoverfly.junit.core;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Utils for Hoverfly
 */
class HoverflyUtils {

    private static final String OSX = "OSX";
    private static final String WINDOWS = "windows";
    private static final String LINUX = "linux";
    private static final String ARCH_AMD64 = "amd64";
    private static final String ARCH_386 = "386";
    private static final String BINARY_PATH = "hoverfly_%s_%s";

    /**
     * Calculates the binary to used based on OS and architecture
     */
    static String getBinaryName() {
        return String.format(BINARY_PATH, getOs(), getArchitectureType()) + (SystemUtils.IS_OS_WINDOWS ? ".exe" : "");
    }

    /**
     * Gets the correct operating system
     */
    private static String getOs() {
        if (SystemUtils.IS_OS_MAC) {
            return OSX;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        } else if (SystemUtils.IS_OS_LINUX) {
            return LINUX;
        } else {
            throw new UnsupportedOperationException(SystemUtils.OS_NAME + " is not currently supported");
        }
    }

    /**
     * Detects whether the application is 64 bits
     */
    private static String getArchitectureType() {
        return SystemUtils.OS_ARCH.contains("64") ? ARCH_AMD64 : ARCH_386;
    }

    /**
     * Looks for an unused port on the current machine
     */
    static int findUnusedPort() {
        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Cannot find available port", e);
        }
    }

    /**
     * Looks for a resource on the classpath with the given name
     */
    static URI findResourceOnClasspath(String resourceName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL resource = classLoader.getResource(resourceName);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found with name: " + resourceName);
        }
        try {
            return resource.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Looks for a file in the src/test/resources directory with the given name
     *
     * @param fileName name of the classpath
     * @return URI pointing to the classpath
     */
    static File fileRelativeToTestResources(String fileName) {
        return Paths.get("src/test/resources/", fileName).toFile();
    }
}