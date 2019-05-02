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
import org.eclipse.jetty.webapp.WebAppContext;

@SuppressWarnings("Duplicates")
public class WebAppContextFromFileSystem
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);

        Path warPath = new File("webapps/hello.war").toPath().toRealPath();
        System.err.println("WAR File is " + warPath);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(warPath.toUri().toASCIIString());
        webapp.setParentLoaderPriority(true);

        server.setHandler(webapp);

        server.start();
        server.join();
    }
}
