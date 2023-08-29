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

package org.eclipse.jetty.cookbook.core;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.cookbook.core.handlers.DumpHandler;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.Rule;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.Callback;

public class MovedPermanentlyExample
{
    public static class MovedPermanentlyRule extends Rule
    {
        private Pattern regex;
        private String replacement;

        public MovedPermanentlyRule()
        {
            setTerminating(true);
        }

        public String getRegex()
        {
            return regex == null ? null : regex.pattern();
        }

        public String getReplacement()
        {
            return replacement;
        }

        @Override
        public Handler matchAndApply(Handler input) throws IOException
        {
            Matcher matcher = regex.matcher(input.getHttpURI().asString());
            boolean matches = matcher.matches();
            if (matches)
            {
                return new Handler(input)
                {
                    @Override
                    protected boolean handle(Response response, Callback callback)
                    {
                        String location = replacement;
                        response.getHeaders().put(HttpHeader.LOCATION, Request.toRedirectURI(this, location));
                        response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);

                        // consume available request data
                        while (true)
                        {
                            Content.Chunk chunk = response.getRequest().read();
                            if (chunk == null)
                            {
                                response.getHeaders().put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
                                break;
                            }
                            chunk.release();
                            if (chunk.isLast())
                                break;
                        }

                        response.write(true, null, callback);
                        return true;
                    }
                };
            }
            return null;
        }

        public void setRegex(String regex)
        {
            this.regex = Pattern.compile(regex);
        }

        public void setReplacement(String replacement)
        {
            this.replacement = replacement;
        }

        @Override
        public String toString()
        {
            return super.toString() + "[" + regex + "]";
        }
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        Handler.Sequence handlers = new Handler.Sequence();
        server.setHandler(handlers);

        // Add Rewrite / Redirect handlers + Rules
        RewriteHandler rewriteHandler = new RewriteHandler();
        MovedPermanentlyRule movedRule = new MovedPermanentlyRule();
        movedRule.setRegex("http://www.company.com/dump/.*");
        movedRule.setReplacement("https://api.company.com/dump/");
        rewriteHandler.addRule(movedRule);
        handlers.addHandler(rewriteHandler);

        // Add a context
        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath("/dump");
        contextHandler.setHandler(new DumpHandler());

        server.start();
        server.join();
    }
}
