//
// ========================================================================
// Copyright (c) 2017 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.cookbook;

import org.eclipse.jetty.cookbook.handlers.HelloHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class ConnectorSpecificContexts
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server();

        ServerConnector connectorA = new ServerConnector(server);
        connectorA.setPort(8080);
        connectorA.setName("connA");
        ServerConnector connectorB = new ServerConnector(server);
        connectorB.setPort(9090);
        connectorB.setName("connB");

        server.addConnector(connectorA);
        server.addConnector(connectorB);

        // Collection of Contexts
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        server.setHandler(contexts);

        // Hello Handler (connection A)
        ContextHandler ctxHelloA = new ContextHandler();
        ctxHelloA.setContextPath("/");
        ctxHelloA.setHandler(new HelloHandler("Hello Connection A"));
        ctxHelloA.setVirtualHosts(new String[]{"@connA"});
        contexts.addHandler(ctxHelloA);

        // Hello Handler (connection B)
        ContextHandler ctxHelloB = new ContextHandler();
        ctxHelloB.setContextPath("/");
        ctxHelloB.setHandler(new HelloHandler("Greetings from Connection B"));
        ctxHelloB.setVirtualHosts(new String[]{"@connB"});
        contexts.addHandler(ctxHelloB);

        server.start();
        server.join();
    }
}
