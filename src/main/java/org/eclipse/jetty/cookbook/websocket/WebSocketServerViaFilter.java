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

package org.eclipse.jetty.cookbook.websocket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.servlet.ServletException;

import org.eclipse.jetty.cookbook.servlets.TimeServlet;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

@SuppressWarnings("Duplicates")
public class WebSocketServerViaFilter
{
    public static class TimeSocketCreator implements WebSocketCreator
    {
        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp)
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
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setBaseResource(new PathResource(webRootPath));
        context.setWelcomeFiles(new String[] { "index.html" });
        server.setHandler(context);

        // Add the websocket filter
        WebSocketUpgradeFilter wsfilter = WebSocketUpgradeFilter.configureContext(context);
        // Configure websocket behavior
        wsfilter.getFactory().getPolicy().setIdleTimeout(5000);
        // Add websocket mapping
        wsfilter.addMapping(new ServletPathSpec("/time/"),new TimeSocketCreator());
        
        // Add time servlet
        context.addServlet(TimeServlet.class,"/time/");

        // Add default servlet
        ServletHolder holderDefault = new ServletHolder("default",DefaultServlet.class);
        holderDefault.setInitParameter("dirAllowed","true");
        context.addServlet(holderDefault,"/");

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
