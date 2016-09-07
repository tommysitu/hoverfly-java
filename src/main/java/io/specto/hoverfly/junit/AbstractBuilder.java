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

public abstract class AbstractBuilder {
    protected int proxyPort = 0;
    protected int adminPort = 0;
    protected boolean proxyLocalHost = false;

    public AbstractBuilder withProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    public AbstractBuilder withAdminPort(int adminPort) {
        this.adminPort = adminPort;
        return this;
    }

    public AbstractBuilder proxyLocalHost() {
        this.proxyLocalHost = true;
        return this;
    }

    public abstract HoverflyRule build();
}
