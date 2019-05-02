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
import java.nio.file.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.PathResource;

@SuppressWarnings("Duplicates")
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
