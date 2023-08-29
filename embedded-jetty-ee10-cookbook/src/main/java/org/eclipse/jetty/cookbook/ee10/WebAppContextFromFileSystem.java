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

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.Resources;

public class WebAppContextFromFileSystem
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");

        Path warPath = Path.of("webapps/hello.war");
        Resource war = webapp.getResourceFactory().newResource(warPath);
        if (!Resources.isReadableFile(war))
            throw new FileNotFoundException("Unable to find " + warPath);
        System.err.println("WebRoot is " + war);

        webapp.setWarResource(war);
        webapp.setParentLoaderPriority(true);

        server.setHandler(webapp);

        server.start();
        server.join();
    }
}
