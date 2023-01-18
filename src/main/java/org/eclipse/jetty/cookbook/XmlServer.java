//
// ========================================================================
// Copyright (c) 2021 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.cookbook;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;

/**
 * Demonstration of using the XmlConfiguration to build up a server with multiple XML files and Properties.
 */
public class XmlServer
{
    public static void main(String[] args) throws Exception
    {
        List<String> argList = Arrays.asList(args);

        // both http and https are enabled by default (command line can disable them)
        final boolean enableHttp = !argList.contains("--disable-http");
        final boolean enableHttps = !argList.contains("--disable-https");

        // The list of XMLs in the order they should be executed.
        List<Resource> xmls = new ArrayList<>();

        // Establish where to find the XMLs that we copied from jetty-home.
        // We got this order from just creating a simple jetty-base and asking what the order looked like
        // $ mkdir tmpdir
        // $ cd tmpdir
        // $ java -jar /path/to/jetty-home/start.jar --add-modules=http,https,requestlog
        // $ java -jar /path/to/jetty-home/start.jar --list-config
        // Bonus is we also learn what JAR files we need.
        // And if we look at tmpdir/start.ini we can also know what properties can be set.

        Path homeXmlPath = Paths.get("src/main/xml/home").toAbsolutePath();
        xmls.add(new PathResource(homeXmlPath.resolve("jetty-bytebufferpool.xml")));
        xmls.add(new PathResource(homeXmlPath.resolve("jetty-threadpool.xml")));
        xmls.add(new PathResource(homeXmlPath.resolve("jetty.xml")));
        if (enableHttp)
        {
            xmls.add(new PathResource(homeXmlPath.resolve("jetty-http.xml")));
        }
        if (enableHttps)
        {
            xmls.add(new PathResource(homeXmlPath.resolve("jetty-ssl.xml")));
            xmls.add(new PathResource(homeXmlPath.resolve("jetty-ssl-context.xml")));
            xmls.add(new PathResource(homeXmlPath.resolve("jetty-https.xml")));
        }
        xmls.add(new PathResource(homeXmlPath.resolve("jetty-requestlog.xml")));

        // Now we add our customizations
        // In this case, it's 2 ServletContextHandlers
        Path customBasePath = Paths.get("src/main/xml/base").toAbsolutePath();
        xmls.add(new PathResource(customBasePath.resolve("context-foo.xml")));
        xmls.add(new PathResource(customBasePath.resolve("context-bar.xml")));

        // Lets load our properties
        Map<String, String> customProps = loadProperties(customBasePath.resolve("custom.properties"));

        // Create a path suitable for output / work directory / etc.
        Path outputPath = Paths.get("target/xmlserver-output");
        Path resourcesPath = outputPath.resolve("resources");

        ensureDirExists(outputPath);
        ensureDirExists(outputPath.resolve("logs"));
        ensureDirExists(resourcesPath);
        ensureDirExists(resourcesPath.resolve("bar"));
        ensureDirExists(resourcesPath.resolve("foo"));

        // And define some common properties
        // These 2 properties are used in MANY PLACES, define them, even if you don't use them fully.
        customProps.put("jetty.home", outputPath.toString());
        customProps.put("jetty.base", outputPath.toString());
        // And define the resource paths for the contexts
        customProps.put("custom.resources", resourcesPath.toString());
        customProps.put("jetty.sslContext.keyStorePath", customBasePath.resolve("keystore.p12").toString());

        // Now lets tie it all together
        Map<String, Object> idMap = configure(xmls, customProps);

        // Fetch the configured Server
        Server server = (Server)idMap.get("Server");

        // Start the server
        server.start();

        // Show what connectors it's listening on
        System.out.println("Server is running, and listening on ...");
        for (ServerConnector connector : server.getBeans(ServerConnector.class))
        {
            for (HttpConnectionFactory connectionFactory : connector.getBeans(HttpConnectionFactory.class))
            {
                String scheme = "http";
                HttpConfiguration httpConfiguration = connectionFactory.getHttpConfiguration();
                if (httpConfiguration.getSecurePort() == connector.getLocalPort())
                    scheme = httpConfiguration.getSecureScheme();
                String host = connector.getHost();
                if (host == null)
                    host = InetAddress.getLocalHost().getHostAddress();
                System.out.printf("   %s://%s:%s/%n", scheme, host, connector.getLocalPort());
            }
        }
        server.join();
    }

    /**
     * Configure for the list of XML Resources and Properties.
     *
     * @param xmls the xml resources (in order of execution)
     * @param properties the properties to use with the XML
     * @return the ID Map of configured objects (key is the id name in the XML, and the value is configured object)
     * @throws Exception if unable to create objects or read XML
     */
    public static Map<String, Object> configure(List<Resource> xmls, Map<String, String> properties) throws Exception
    {
        Map<String, Object> idMap = new HashMap<>();

        // Configure everything
        for (Resource xmlResource : xmls)
        {
            XmlConfiguration configuration = new XmlConfiguration(xmlResource);
            configuration.getIdMap().putAll(idMap);
            configuration.getProperties().putAll(properties);
            configuration.configure();
            idMap.putAll(configuration.getIdMap());
        }

        return idMap;
    }

    private static void ensureDirExists(Path path) throws IOException
    {
        if (!Files.exists(path))
        {
            Files.createDirectories(path);
        }
    }

    private static Map<String, String> loadProperties(Path path) throws IOException
    {
        Properties properties = new Properties();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8))
        {
            properties.load(reader);
        }

        return properties.entrySet().stream().collect(
            Collectors.toMap(
                e -> String.valueOf(e.getKey()),
                e -> String.valueOf(e.getValue()),
                (prev, next) -> next, HashMap::new
            ));
    }
}
