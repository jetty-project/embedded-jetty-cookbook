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

import java.io.File;
import java.nio.file.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;

public class ResourceHandlerFromFileSystem
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        ResourceFactory resourceFactory = ResourceFactory.of(server);

        Path webRootPath = new File("webapps/static-root/").toPath().toRealPath();

        System.err.println("WebRoot is " + webRootPath);

        ResourceHandler handler = new ResourceHandler();
        Resource rootResource = resourceFactory.newResource(webRootPath);
        handler.setBaseResource(rootResource);
        handler.setDirAllowed(true);

        server.setHandler(handler);

        server.start();
        server.join();
    }
}
