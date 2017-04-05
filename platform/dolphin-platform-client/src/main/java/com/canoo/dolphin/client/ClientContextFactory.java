/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.dolphin.client;

import com.canoo.dolphin.client.impl.*;
import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.codec.OptimizedJsonCodec;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.impl.commands.InterruptLongPollCommand;
import com.canoo.dolphin.impl.commands.StartLongPollCommand;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.DefaultModelSynchronizer;
import org.opendolphin.core.client.ModelSynchronizer;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.RemotingExceptionHandler;
import org.opendolphin.util.DolphinRemotingException;
import org.opendolphin.util.Provider;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory to create a {@link ClientContext}. Normally you will create a {@link ClientContext} at the bootstrap of your
 * client by using the {@link #connect(ClientConfiguration)} method and use this context as a singleton in your client.
 * The {@link ClientContext} defines the connection between the client and the Dolphin Platform server endpoint.
 */
public class ClientContextFactory {

    private ClientContextFactory() {
    }

    /**
     * Create a {@link ClientContext} based on the given configuration. This method doesn't block and returns a
     * {@link CompletableFuture} to receive its result. If the {@link ClientContext} can't be created the
     * {@link CompletableFuture#get()} will throw a {@link ClientInitializationException}.
     *
     * @param clientConfiguration the configuration
     * @return the future
     */
    public static CompletableFuture<ClientContext> connect(final ClientConfiguration clientConfiguration) {
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        final CompletableFuture<ClientContext> result = new CompletableFuture<>();

        Level openDolphinLogLevel = clientConfiguration.getDolphinLogLevel();
        Logger openDolphinLogger = Logger.getLogger("org.opendolphin");
        openDolphinLogger.setLevel(openDolphinLogLevel);

        clientConfiguration.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClientDolphin clientDolphin = new ClientDolphin();
                    final ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<ClientConnector>() {
                        @Override
                        public ClientConnector get() {
                            return clientDolphin.getClientConnector();
                        }
                    });
                    clientDolphin.setClientModelStore(new ClientModelStore(defaultModelSynchronizer));

                    RemotingExceptionHandler contextExceptionHandler = clientConfiguration.getRemotingExceptionHandler();

                    RemotingExceptionHandler internalExceptionHandler = new RemotingExceptionHandler() {

                        @Override
                        public void handle(DolphinRemotingException e) {
                            contextExceptionHandler.handle(e);
                            if(!result.isDone()) {
                                result.completeExceptionally(new DolphinRemotingException("Can not create connection", e));
                            }
                        }
                    };

                    final AbstractClientConnector clientConnector = new DolphinPlatformHttpClientConnector(clientConfiguration, clientDolphin, new OptimizedJsonCodec(), internalExceptionHandler);

                    clientDolphin.setClientConnector(clientConnector);
                    final DolphinCommandHandler dolphinCommandHandler = new DolphinCommandHandler(clientConnector);
                    final EventDispatcher dispatcher = new ClientEventDispatcher(clientDolphin);
                    final BeanRepository beanRepository = new BeanRepositoryImpl(clientDolphin, dispatcher);
                    final Converters converters = new Converters(beanRepository);
                    final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientDolphin);
                    final ClassRepository classRepository = new ClassRepositoryImpl(clientDolphin, converters, builderFactory);
                    final ListMapper listMapper = new ListMapperImpl(clientDolphin, classRepository, beanRepository, builderFactory, dispatcher);
                    final BeanBuilder beanBuilder = new ClientBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
                    final ClientPlatformBeanRepository platformBeanRepository = new ClientPlatformBeanRepository(clientDolphin, beanRepository, dispatcher, converters);
                    final ClientBeanManagerImpl clientBeanManager = new ClientBeanManagerImpl(beanRepository, beanBuilder, clientDolphin);
                    final ControllerProxyFactory controllerProxyFactory = new ControllerProxyFactoryImpl(platformBeanRepository, dolphinCommandHandler, clientDolphin);
                    final ClientContext clientContext = new ClientContextImpl(clientConfiguration, clientConnector, controllerProxyFactory, dolphinCommandHandler, clientBeanManager);
                    clientConnector.startPushListening(new StartLongPollCommand(), new InterruptLongPollCommand());
                    clientConfiguration.getUiExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            result.complete(clientContext);
                        }
                    });
                } catch (Exception e) {
                    result.obtrudeException(new ClientInitializationException("Can not connect to server!", e));
                }
            }
        });
        return result;
    }

}
