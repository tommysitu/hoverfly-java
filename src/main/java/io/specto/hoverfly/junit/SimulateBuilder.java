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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class SimulateBuilder extends AbstractBuilder {
    private static final HoverflyMode hoverflyMode = HoverflyMode.SIMULATE;
    private final Optional<String> serviceDataClasspath;
    private final Optional<URL> serviceDataURL;

    public SimulateBuilder(final String serviceDataClasspath) {
        this.serviceDataClasspath = Optional.of(serviceDataClasspath);
        this.serviceDataURL = Optional.empty();
    }

    public SimulateBuilder(final URL url) {
        this.serviceDataClasspath = Optional.empty();
        this.serviceDataURL = Optional.of(url);
    }

    @Override
    public SimulateBuilder withProxyPort(final int proxyPort) {
        super.withProxyPort(proxyPort);
        return this;
    }

    @Override
    public SimulateBuilder withAdminPort(final int adminPort) {
        super.withAdminPort(adminPort);
        return this;
    }

    @Override
    public SimulateBuilder proxyLocalHost() {
        super.proxyLocalHost();
        return this;
    }

    public HoverflyRule build() {
        try {
            if (serviceDataClasspath.isPresent()) {
                return new HoverflyRule(serviceDataClasspath.get(), proxyPort, adminPort, hoverflyMode, proxyLocalHost);
            } else {
                return new HoverflyRule(serviceDataURL.get(), proxyPort, adminPort, hoverflyMode, proxyLocalHost);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Unable to build rule", e);
        }
    }
}
