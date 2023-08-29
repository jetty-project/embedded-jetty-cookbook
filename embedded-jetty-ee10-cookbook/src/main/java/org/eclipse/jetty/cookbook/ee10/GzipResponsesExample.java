//
// ========================================================================
// Copyright (c) 1995 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.cookbook.ee10;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.eclipse.jetty.cookbook.ee10.servlets.TimeServlet;
import org.eclipse.jetty.cookbook.ee10.websocket.jsr.TimeSocket;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.Resources;

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

        Path webRootPath = Path.of("webapps/static-root/").toAbsolutePath();

        ServletContextHandler contextHandler = new ServletContextHandler();
        gzip.setHandler(contextHandler);
        Resource staticBase = ResourceFactory.of(contextHandler).newResource(webRootPath);
        if (!Resources.isReadableDirectory(staticBase))
            throw new FileNotFoundException("Unable to find static-root/ dir");
        contextHandler.setContextPath("/");
        contextHandler.setBaseResource(staticBase);
        contextHandler.setWelcomeFiles(new String[]{"index.html"});

        // Adding WebSockets
        JakartaWebSocketServletContainerInitializer.configure(contextHandler, (context, wsContainer) ->
            wsContainer.addEndpoint(TimeSocket.class));

        // Adding Servlets
        contextHandler.addServlet(TimeServlet.class, "/time/");
        contextHandler.addServlet(DefaultServlet.class, "/"); // always last, always on "/"

        server.start();
        server.join();
    }
}
