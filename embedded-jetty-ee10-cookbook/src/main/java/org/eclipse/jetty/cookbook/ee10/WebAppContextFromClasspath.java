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

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.Resources;

public class WebAppContextFromClasspath
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");

        Resource webRoot = webapp.getResourceFactory().newSystemResource("hello-webapp/");
        if (!Resources.isReadableDirectory(webRoot))
            throw new FileNotFoundException("Unable to find hello-webapp/");
        System.err.println("WebRoot is " + webRoot);

        webapp.setWarResource(webRoot);
        webapp.setParentLoaderPriority(true);

        server.setHandler(webapp);

        server.start();
        server.join();
    }
}
