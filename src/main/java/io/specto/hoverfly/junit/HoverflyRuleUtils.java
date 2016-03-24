/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
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
package io.specto.hoverfly.junit;

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

    static Optional<URL> getResource(String resourceName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Optional.ofNullable(classLoader.getResource(resourceName));
    }

    static String getOs() {
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

    static String getArchitectureType() {
        return SystemUtils.OS_ARCH.contains("64") ? ARCH_AMD64 : ARCH_386;
    }

    static int findUnusedPort() {
        try(final ServerSocket serverSocket = new ServerSocket(0)){
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Cannot find available port", e);
        }
    }
}
