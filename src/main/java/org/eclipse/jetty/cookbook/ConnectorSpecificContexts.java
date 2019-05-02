//
//  ========================================================================
//  Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.cookbook;

import org.eclipse.jetty.cookbook.handlers.HelloHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

@SuppressWarnings("Duplicates")
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
        ctxHelloA.setVirtualHosts(new String[] { "@connA" });
        contexts.addHandler(ctxHelloA);

        // Hello Handler (connection B)
        ContextHandler ctxHelloB = new ContextHandler();
        ctxHelloB.setContextPath("/");
        ctxHelloB.setHandler(new HelloHandler("Greetings from Connection B"));
        ctxHelloB.setVirtualHosts(new String[] { "@connB" });
        contexts.addHandler(ctxHelloB);

        server.start();
        server.join();
    }
}
