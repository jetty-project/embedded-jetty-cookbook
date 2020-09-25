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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;

/**
 * This is a Server setup in a default way, but is can be enhanced by providing
 * (0 to n) Jetty XML file arguments on the command line.
 * <p>
 * Run without a command line argument and pay attention to the contextPath for the
 * default Context. (It should be {@code "/"})
 * <br>
 * Now run with {@code src/test/resources/xml-enhanced/adjust-default-contextpath.xml} and see
 * that the context path is now {@code "/foobar"}
 * </p>
 * <p>
 * Run with {@code src/test/resources/xml-enhanced/configure-http.xml} and you will be
 * adjusting the {@link HttpConfiguration} parameters in use.
 * </p>
 * <p>
 * Run with {@code src/test/resources/xml-enhanced/add-rewrites.xml} and you will be
 * adjusting adding rewrite rules to the existing handler tree.
 * <br>
 * Request {@code http://localhost:8080/bar/blah} and you will receive a 302 redirect
 * to {@code http://localhost:8080/foo}
 * </p>
 * <p>
 * Run with {@code src/test/resources/xml-enhanced/add-https.xml} and you will be
 * adding an HTTPS connector with configuration.
 * </p>
 */
@SuppressWarnings("Duplicates")
public class XmlEnhancedServer
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        HttpConfiguration httpConfig = new HttpConfiguration();
        ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        httpConnector.setPort(8080);
        server.addConnector(httpConnector);

        // Figure out what path to serve content from
        ClassLoader cl = DefaultServletFileServer.class.getClassLoader();
        // We look for a file, as ClassLoader.getResource() is not
        // designed to look for directories (we resolve the directory later)
        URL f = cl.getResource("static-root/hello.html");
        if (f == null)
        {
            throw new RuntimeException("Unable to find resource directory");
        }

        // Resolve file to directory
        URI webRootUri = f.toURI().resolve("./").normalize();
        System.err.println("WebRoot is " + webRootUri);

        HandlerList handlers = new HandlerList();
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource(webRootUri));
        contexts.addHandler(context);

        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed", "true");
        context.addServlet(holderPwd, "/");

        handlers.addHandler(contexts);
        DefaultHandler defaultHandler = new DefaultHandler();
        handlers.addHandler(defaultHandler); // always last in handler list

        server.setHandler(handlers);

        // apply extra XML (provided on command line)
        if (args.length > 0)
        {
            // Map some well known objects via an id that can be referenced in the XMLs
            Map<String, Object> idMap = new HashMap<>();
            idMap.put("Server", server);
            idMap.put("httpConfig", httpConfig);
            idMap.put("httpConnector", httpConnector);
            idMap.put("Handlers", handlers);
            idMap.put("Contexts", contexts);
            idMap.put("Context", context);
            idMap.put("DefaultHandler", defaultHandler);

            // Map some well known properties
            Map<String,String> globalProps = new HashMap<>();
            URI resourcesUriBase = webRootUri.resolve("..");
            System.err.println("ResourcesUriBase is " + resourcesUriBase);
            globalProps.put("resources.location", resourcesUriBase.toASCIIString());

            List<Object> configuredObjects = new ArrayList<>();
            XmlConfiguration lastConfig = null;
            for (String xml : args)
            {
                URL url = new File(xml).toURI().toURL();
                System.err.println("Applying XML: " + url);
                XmlConfiguration configuration = new XmlConfiguration(url);
                if (lastConfig != null)
                    configuration.getIdMap().putAll(lastConfig.getIdMap());
                configuration.getProperties().putAll(globalProps);
                configuration.getIdMap().putAll(idMap);
                idMap.putAll(configuration.getIdMap());
                configuredObjects.add(configuration.configure());
                lastConfig = configuration;
            }

            // Dump what was configured
            for(Object configuredObject: configuredObjects)
            {
                System.err.printf("Configured (%s)%n", configuredObject.getClass().getName());
            }

            // Dump the resulting idMap
            idMap.forEach((id, obj) -> System.err.printf("IdMap[%s]: (%s)%n", id, obj.getClass().getName()));
        }

        server.setDumpAfterStart(true);
        server.start();
        server.join();
    }
}
