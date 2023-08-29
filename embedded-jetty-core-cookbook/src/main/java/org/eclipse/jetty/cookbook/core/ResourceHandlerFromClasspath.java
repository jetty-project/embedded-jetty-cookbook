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

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.Resources;

public class ResourceHandlerFromClasspath
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        ResourceFactory resourceFactory = ResourceFactory.of(server);

        // Figure out what path to serve content from
        Resource rootResource = resourceFactory.newSystemResource("root-static/");
        if (!Resources.isReadableDirectory(rootResource))
            throw new IOException("Unable to find static-root");

        System.err.println("Static Root is " + rootResource);

        ResourceHandler handler = new ResourceHandler();
        handler.setBaseResource(rootResource);
        handler.setDirAllowed(true);

        server.setHandler(handler);

        server.start();
        server.join();
    }
}
