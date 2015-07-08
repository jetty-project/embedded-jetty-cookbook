package org.eclipse.jetty.cookbook.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet
{
    private String msg;

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        msg = config.getInitParameter("message");
        if (msg == null)
        {
            msg = "User";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain");
        response.getWriter().printf("%s%n",msg);
    }
}
