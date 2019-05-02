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

package org.eclipse.jetty.cookbook.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TimeServlet extends HttpServlet
{
    private static final TimeZone TZ = TimeZone.getDefault();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Locale locale = req.getLocale();
        Calendar cal = Calendar.getInstance(TZ,locale);
        String dateStr = DateFormat.getDateInstance(DateFormat.DEFAULT,locale).format(cal.getTime());
        String timeStr = DateFormat.getTimeInstance(DateFormat.DEFAULT,locale).format(cal.getTime());
        String tzStr = TZ.getDisplayName(false,TimeZone.SHORT,locale);
        resp.getWriter().println(String.format("%s %s %s",dateStr,timeStr,tzStr));
    }
}
