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
