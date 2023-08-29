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

package org.eclipse.jetty.cookbook.ee10.websocket.jsr;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.Resources;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tool to setup a WebSocket server with some static html/javascript for browsers
 */
public class JsrBrowserMain
{
    private static final Logger LOG = LoggerFactory.getLogger(JsrBrowserMain.class);

    public static void main(String[] args)
    {
        int port = 8080;
        int sslPort = 8443;

        for (int i = 0; i < args.length; i++)
        {
            String a = args[i];
            if ("-p".equals(a) || "--port".equals(a))
            {
                port = Integer.parseInt(args[++i]);
            }
            if ("-ssl".equals(a))
            {
                sslPort = Integer.parseInt(args[++i]);
            }
        }

        try
        {
            JsrBrowserMain tool = new JsrBrowserMain();
            tool.setupServer(port, sslPort);
            tool.runForever();
        }
        catch (Throwable t)
        {
            LOG.warn("Unable to start JsrBrowserMain", t);
        }
    }

    private Server server;

    private void runForever() throws Exception
    {
        server.start();
        server.dumpStdErr();
        server.join();
    }

    private void setupServer(int port, int sslPort) throws MalformedURLException, URISyntaxException, FileNotFoundException
    {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        // Setup SSL
        Resource keystoreResource = ResourceFactory.of(server).newSystemResource("ssl/keystore");
        if (!Resources.isReadableFile(keystoreResource))
            throw new FileNotFoundException("Unable to find ssl/keystore");

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStoreResource(keystoreResource);
        sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");

        // Setup HTTPS Configuration
        HttpConfiguration httpsConf = new HttpConfiguration();
        httpsConf.setSecurePort(sslPort);
        httpsConf.setSecureScheme("https");
        httpsConf.addCustomizer(new SecureRequestCustomizer()); // adds ssl info to request object

        // Establish the ServerConnector
        ServerConnector httpsConnector = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory, "http/1.1"),
            new HttpConnectionFactory(httpsConf));
        httpsConnector.setPort(sslPort);

        server.addConnector(httpsConnector);

        // Setup ServletContext
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        Resource websocketStaticBase = ResourceFactory.of(contextHandler).newSystemResource("websocket-statics/");
        if (!Resources.isReadableDirectory(websocketStaticBase))
            throw new FileNotFoundException("Unable to find websocket-statics/ dir");
        contextHandler.setBaseResource(websocketStaticBase);

        ServletHolder holder = contextHandler.addServlet(DefaultServlet.class, "/");
        holder.setInitParameter("dirAllowed", "true");
        server.setHandler(contextHandler);

        JakartaWebSocketServletContainerInitializer.configure(contextHandler, (context, wsContainer) ->
            wsContainer.addEndpoint(JsrBrowserSocket.class));

        LOG.info("{} setup on (http) port {} and (https) port {}", this.getClass().getName(), port, sslPort);
    }
}
