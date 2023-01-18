//
// ========================================================================
// Copyright (c) 2015 Mort Bay Consulting Pty Ltd and others.
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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.PathResource;

public class ResourceHandlerFromFileSystem
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);

        Path webRootPath = new File("webapps/static-root/").toPath().toRealPath();

        System.err.println("WebRoot is " + webRootPath);

        ResourceHandler handler = new ResourceHandler();
        handler.setBaseResource(new PathResource(webRootPath));
        handler.setDirectoriesListed(true);

        server.setHandler(handler);

        server.start();
        server.join();
    }
}
