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

package org.eclipse.jetty.cookbook.core;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualHostsExample
{
    private static final Logger LOG = LoggerFactory.getLogger(VirtualHostsExample.class);

    public static void main(String[] args)
    {
        VirtualHostsExample example = new VirtualHostsExample();
        try
        {
            example.startServer();
            example.testRequest("a.company.com", "/hello");
            example.testRequest("b.company.com", "/hello");
        }
        catch (Exception e)
        {
            LOG.warn("Test failed", e);
        }
        finally
        {
            example.stopServer();
        }
    }

    private Server server;

    private void stopServer()
    {
        LifeCycle.stop(server);
    }

    private void startServer() throws Exception
    {
        server = new Server(8080);
        ContextHandlerCollection handlers = new ContextHandlerCollection();
        server.setHandler(handlers);

        handlers.addHandler(createContext("/", "a.company.com"));
        handlers.addHandler(createContext("/", "b.company.com"));

        server.start();
    }

    private ContextHandler createContext(String contextPath, final String host)
    {
        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath(contextPath);
        contextHandler.setVirtualHosts(List.of(host));
        contextHandler.setHandler(new Handler.Abstract()
        {
            @Override
            public boolean handle(Request request, Response response, Callback callback) throws Exception
            {
                response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/plain; charset=utf-8");
                Content.Sink.write(response, true, String.format("Hello from [%s] context%n", host), callback);
                return true;
            }
        });
        return contextHandler;
    }

    private void testRequest(String host, String path)
    {
        try (Socket client = new Socket("localhost", 8080))
        {
            LOG.info("-- testRequest [{}] [{}] --", host, path);
            String req = String.format("GET %s HTTP/1.1\r\nHost: %s\r\nConnection: close\r\n\r\n", path, host);
            LOG.info(req);
            client.getOutputStream().write(req.getBytes(StandardCharsets.UTF_8));
            String response = IO.toString(client.getInputStream());
            LOG.info(response);
        }
        catch (Exception e)
        {
            LOG.warn("Failed request: host={}, path={}", host, path, e);
        }
    }
}
