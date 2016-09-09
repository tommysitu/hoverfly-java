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

public class CaptureBuilder extends AbstractBuilder {
    private static final HoverflyMode hoverflyMode = HoverflyMode.CAPTURE;
    private final String simulationOutputDirectory;

    public CaptureBuilder(final String simulationOutputDirectory) {
        this.simulationOutputDirectory = simulationOutputDirectory;
    }

    @Override
    public CaptureBuilder withProxyPort(final int proxyPort) {
        super.withProxyPort(proxyPort);
        return this;
    }

    @Override
    public CaptureBuilder withAdminPort(final int adminPort) {
        super.withAdminPort(adminPort);
        return this;
    }

    @Override
    public CaptureBuilder proxyLocalHost() {
        super.proxyLocalHost();
        return this;
    }

    public HoverflyRule build() {
        try {
            return new HoverflyRule(simulationOutputDirectory, proxyPort, adminPort, hoverflyMode, proxyLocalHost);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Unable to build rule", e);
        }
    }
}
