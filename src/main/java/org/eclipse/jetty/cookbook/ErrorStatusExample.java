package org.eclipse.jetty.cookbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.IO;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ErrorStatusExample
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(9090);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(RangeHandlingServlet.class, "/demo");
        context.addServlet(DefaultServlet.class, "/"); // handle static content and errors for this context
        HandlerList handlers = new HandlerList();
        handlers.addHandler(context);
        handlers.addHandler(new DefaultHandler()); // handle non-context errors
        server.setHandler(context);
        server.start();

        try
        {
            demonstrateErrorHandling(server.getURI().resolve("/"));
        }
        finally
        {
            server.stop();
        }
    }

    private static void demonstrateErrorHandling(URI serverBaseUri) throws IOException
    {
        HttpURLConnection http = (HttpURLConnection)serverBaseUri.resolve("/demo").toURL().openConnection();
        dumpRequestResponse(http);
        System.out.println();
        try (InputStream in = http.getInputStream())
        {
            System.out.println(IO.toString(in, UTF_8));
        }
    }

    private static void dumpRequestResponse(HttpURLConnection http) throws IOException
    {
        System.out.println();
        System.out.println("----");
        System.out.printf("%s %s HTTP/1.1%n", http.getRequestMethod(), http.getURL());
        System.out.println("----");
        System.out.printf("%s%n", http.getHeaderField(null));
        http.getHeaderFields().entrySet().stream()
            .filter(entry -> entry.getKey() != null)
            .forEach((entry) -> System.out.printf("%s: %s%n", entry.getKey(), http.getHeaderField(entry.getKey())));
    }

    public static class RangeHandlingServlet extends HttpServlet
    {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
        {
            // To avoid triggering the standard Servlet Context Error Handling, we use .setStatus().
            // Using .sendError() will trigger Servlet Context Error Handling.
            resp.setStatus(416);
            resp.setHeader("Content-Range", "*/100");
            resp.addHeader("X-Example", "Yeah, your range isn't that great");
        }
    }
}
