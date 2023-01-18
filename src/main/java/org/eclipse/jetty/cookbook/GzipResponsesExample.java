//
// ========================================================================
// Copyright (c) ${copyright-range} Mort Bay Consulting Pty Ltd and others.
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

import java.io.File;
import java.nio.file.Path;

import org.eclipse.jetty.cookbook.servlets.TimeServlet;
import org.eclipse.jetty.cookbook.websocket.jsr.TimeSocket;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer;

public class GzipResponsesExample
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        GzipHandler gzip = new GzipHandler();
        gzip.setIncludedMethods("GET", "POST");
        gzip.setMinGzipSize(245);
        gzip.setIncludedMimeTypes("text/plain", "text/css", "text/html",
            "application/javascript");
        server.setHandler(gzip);

        Path webRootPath = new File("webapps/static-root/").toPath().toRealPath();

        ServletContextHandler contextHandler = new ServletContextHandler();
        gzip.setHandler(contextHandler);
        contextHandler.setContextPath("/");
        contextHandler.setBaseResource(new PathResource(webRootPath));
        contextHandler.setWelcomeFiles(new String[]{"index.html"});

        // Adding WebSockets
        JavaxWebSocketServletContainerInitializer.configure(contextHandler, (context, wsContainer) ->
            wsContainer.addEndpoint(TimeSocket.class));

        // Adding Servlets
        contextHandler.addServlet(TimeServlet.class, "/time/");
        contextHandler.addServlet(DefaultServlet.class, "/"); // always last, always on "/"

        server.start();
        server.join();
    }
}
