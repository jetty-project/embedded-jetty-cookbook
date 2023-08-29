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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MultiPart;
import org.eclipse.jetty.http.MultiPartFormData;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.Resources;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipartMimeUploadExample
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        int httpPort = 8080;
        int httpsPort = 8443;

        ResourceFactory resourceFactory = ResourceFactory.of(server);

        // Setup HTTP Connector
        HttpConfiguration httpConf = new HttpConfiguration();
        httpConf.setSecurePort(httpsPort);
        httpConf.setSecureScheme("https");

        // Establish the HTTP ServerConnector
        ServerConnector httpConnector = new ServerConnector(server,
            new HttpConnectionFactory(httpConf));
        httpConnector.setPort(httpPort);
        server.addConnector(httpConnector);

        // Setup SSL
        Resource keystoreResource = resourceFactory.newSystemResource("ssl/keystore");
        if (!Resources.isReadableFile(keystoreResource))
            throw new FileNotFoundException("Unable to find keystore");
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStoreResource(keystoreResource);
        sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");

        // Setup HTTPS Configuration
        HttpConfiguration httpsConf = new HttpConfiguration(httpConf);
        httpsConf.addCustomizer(new SecureRequestCustomizer()); // adds ssl info to request object

        // Establish the HTTPS ServerConnector
        ServerConnector httpsConnector = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory, "http/1.1"),
            new HttpConnectionFactory(httpsConf));
        httpsConnector.setPort(httpsPort);

        server.addConnector(httpsConnector);

        // Establish output directory
        Path outputDir = Paths.get("target", "upload-dir");
        outputDir = ensureDirExists(outputDir);

        // Find static html directory
        Resource staticBase = resourceFactory.newSystemResource("static-upload/");
        if (!Resources.isReadableDirectory(staticBase))
            throw new FileNotFoundException("Unable to find static-upload/ directory");

        // Create ResourceHandler
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(staticBase);

        // Add a Handlers for requests
        Handler.Sequence handlers = new Handler.Sequence();
        handlers.addHandler(new SecuredRedirectHandler());
        handlers.addHandler(new UploadHandler("/handler/upload", outputDir));
        handlers.addHandler(resourceHandler);
        server.setHandler(handlers);

        server.start();
        server.join();
    }

    private static Path ensureDirExists(Path path) throws IOException
    {
        Path dir = path.toAbsolutePath();

        if (!Files.exists(dir))
        {
            Files.createDirectories(dir);
        }

        return dir;
    }

    public static class UploadHandler extends Handler.Abstract
    {
        private static final Logger LOG = LoggerFactory.getLogger(UploadHandler.class);
        private final String contextPath;
        private final Path outputDir;

        public UploadHandler(String contextPath, Path outputDir) throws IOException
        {
            super();
            this.contextPath = contextPath;
            this.outputDir = outputDir.resolve("handler");
            ensureDirExists(this.outputDir);
        }

        @Override
        public boolean handle(Request request, Response response, Callback callback) throws Exception
        {
            String target = Request.getContextPath(request);
            if (!target.startsWith(contextPath))
            {
                // not meant for us, skip it.
                return false;
            }

            if (!request.getMethod().equalsIgnoreCase("POST"))
            {
                Response.writeError(request, response, callback, HttpStatus.METHOD_NOT_ALLOWED_405);
                return true;
            }

            String contentType = request.getHeaders().get(HttpHeader.CONTENT_TYPE);
            if (!HttpField.getValueParameters(contentType, null).equalsIgnoreCase("multipart/form-data"))
            {
                Response.writeError(request, response, callback, HttpStatus.NOT_FOUND_404);
                return true;
            }

            String boundary = MultiPart.extractBoundary(contentType);
            MultiPartFormData.Parser formData = new MultiPartFormData.Parser(boundary);
            formData.setFilesDirectory(outputDir);

            try
            {
                for (MultiPart.Part part : formData.parse(request).join())
                {
                    String line = String.format("Got part: name=%s filename=%s length=%d", part.getName(), part.getFileName(), part.getLength());
                    Content.Sink.write(response, false, line, Callback.NOOP);
                }

                response.write(true, BufferUtil.EMPTY_BUFFER, callback);
            }
            catch (Exception x)
            {
                Response.writeError(request, response, callback, x);
            }
            return true;
        }
    }
}
