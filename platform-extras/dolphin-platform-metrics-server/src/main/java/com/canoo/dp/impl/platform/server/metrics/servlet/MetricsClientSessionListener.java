/*
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dp.impl.platform.server.metrics.servlet;

import com.canoo.dp.impl.platform.metrics.MetricsImpl;
import com.canoo.dp.impl.platform.core.context.ContextImpl;
import com.canoo.platform.core.context.Context;
import com.canoo.platform.server.ServerListener;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.client.ClientSessionListener;

import java.util.concurrent.atomic.AtomicLong;

@ServerListener
public class MetricsClientSessionListener implements ClientSessionListener {

    private final AtomicLong counter = new AtomicLong();


    @Override
    public void sessionCreated(final ClientSession clientSession) {
        final Context idTag = new ContextImpl("sessionId", clientSession.getId());
        MetricsImpl.getInstance().getOrCreateGauge("clientSessions", idTag)
                .setValue(counter.incrementAndGet());
    }

    @Override
    public void sessionDestroyed(final ClientSession clientSession) {
        final Context idTag = new ContextImpl("sessionId", clientSession.getId());
        MetricsImpl.getInstance().getOrCreateGauge("clientSessions", idTag)
                .setValue(counter.incrementAndGet());
    }
}
