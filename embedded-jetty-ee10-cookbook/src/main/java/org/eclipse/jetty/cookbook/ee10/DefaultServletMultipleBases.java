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

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.Resources;

public class DefaultServletMultipleBases
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);


        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Resource staticBase = ResourceFactory.of(context).newSystemResource("static-root/");
        if (!Resources.isReadableDirectory(staticBase))
            throw new FileNotFoundException("Unable to find static-root/ dir");
        System.err.println("Main Base Resource is " + staticBase);
        context.setBaseResource(staticBase);
        context.setWelcomeFiles(new String[]{"index.html", "index.htm", "alt-index.html"});
        server.setHandler(context);

        // Find altPath
        Path altPath = new File("webapps/alt-root").toPath().toRealPath();
        System.err.println("Alt Base Resource is " + altPath);

        // add special pathspec of "/alt/" content mapped to the altPath
        ServletHolder holderAlt = new ServletHolder("static-alt", DefaultServlet.class);
        holderAlt.setInitParameter("resourceBase", altPath.toUri().toASCIIString());
        holderAlt.setInitParameter("dirAllowed", "true");
        holderAlt.setInitParameter("pathInfoOnly", "true");
        context.addServlet(holderAlt, "/alt/*");

        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // It is important that this is last.
        ServletHolder holderDef = new ServletHolder("default", DefaultServlet.class);
        holderDef.setInitParameter("dirAllowed", "true");
        context.addServlet(holderDef, "/");

        server.start();
        server.join();
    }
}
