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

package org.eclipse.jetty.cookbook.websocket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import jakarta.servlet.ServletException;
import org.eclipse.jetty.cookbook.servlets.TimeServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.websocket.server.JettyServerUpgradeRequest;
import org.eclipse.jetty.websocket.server.JettyServerUpgradeResponse;
import org.eclipse.jetty.websocket.server.JettyWebSocketCreator;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

public class WebSocketServerViaFilter
{
    public static class TimeSocketCreator implements JettyWebSocketCreator
    {
        @Override
        public Object createWebSocket(JettyServerUpgradeRequest jettyServerUpgradeRequest, JettyServerUpgradeResponse jettyServerUpgradeResponse)
        {
            return new JettyTimeSocket();
        }
    }

    public static void main(String[] args) throws ServletException, IOException
    {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // The location of the webapp base resource (for resources and static file serving)
        Path webRootPath = new File("webapps/static-root/").toPath().toRealPath();

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        contextHandler.setBaseResource(new PathResource(webRootPath));
        contextHandler.setWelcomeFiles(new String[]{"index.html"});
        server.setHandler(contextHandler);

        JettyWebSocketServletContainerInitializer.configure(contextHandler, (context, configurator) ->
        {
            configurator.setIdleTimeout(Duration.ofMillis(5000));
            configurator.addMapping("/time/", new TimeSocketCreator());
        });

        // Add time servlet
        contextHandler.addServlet(TimeServlet.class, "/time/");

        // Add default servlet
        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        holderDefault.setInitParameter("dirAllowed", "true");
        contextHandler.addServlet(holderDefault, "/");

        try
        {
            server.start();
            server.join();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }
}
