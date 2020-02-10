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

import org.eclipse.jetty.server.Server;

public class SimplestServer
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(9090);
        // This has a connector listening on port specified
        // and no handlers, meaning all requests will result
        // in a 404 response
        server.start();
        System.err.println("Hint: Hit Ctrl+C to stop Jetty.");
        server.join();
    }
}
