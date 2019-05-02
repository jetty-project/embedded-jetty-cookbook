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

import java.net.URI;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

@SuppressWarnings("Duplicates")
public class WebAppContextFromClasspath
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        
        // Figure out what path to serve content from
        ClassLoader cl = WebAppContextFromClasspath.class.getClassLoader();
        // We look for a file, as ClassLoader.getResource() is not
        // designed to look for directories (we resolve the directory later)
        URL f = cl.getResource("hello-webapp/hello.html");
        if (f == null)
        {
            throw new RuntimeException("Unable to find resource directory");
        }

        // Resolve file to directory
        URI webRootUri = f.toURI().resolve("./").normalize();
        System.err.println("WebRoot is " + webRootUri);


        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(webRootUri.toASCIIString());
        webapp.setParentLoaderPriority(true);

        server.setHandler(webapp);

        server.start();
        server.join();
    }
}
