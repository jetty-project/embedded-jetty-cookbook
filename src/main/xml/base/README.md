This list of XML was obtained from using jetty-home.

``` shell
[tmp]$ mkdir ssl9
[ssl9]$ cd ssl9
[ssl9]$ java -jar /opt/jetty-home-9.4.44.v20210927/start.jar --add-to-start=http,https,customrequestlog
INFO  : server          transitively enabled, ini template available with --add-to-start=server
INFO  : requestlog      initialized in ${jetty.base}/start.ini
INFO  : http            initialized in ${jetty.base}/start.ini
INFO  : https           initialized in ${jetty.base}/start.ini
INFO  : threadpool      transitively enabled, ini template available with --add-to-start=threadpool
INFO  : ssl             transitively enabled, ini template available with --add-to-start=ssl
INFO  : bytebufferpool  transitively enabled, ini template available with --add-to-start=bytebufferpool
MKDIR : ${jetty.base}/logs
MKDIR : ${jetty.base}/etc
COPY  : ${jetty.home}/modules/ssl/keystore to ${jetty.base}/etc/keystore
INFO  : Base directory was modified

[ssl9]$ java -jar /opt/jetty-home-9.4.44.v20210927/start.jar --list-config

Java Environment:
-----------------
 java.home = /home/joakim/java/jvm/jdk-11.0.12+7 (null)
 java.vm.vendor = Eclipse Foundation (null)
 java.vm.version = 11.0.12+7 (null)
 java.vm.name = OpenJDK 64-Bit Server VM (null)
 java.vm.info = mixed mode (null)
 java.runtime.name = OpenJDK Runtime Environment (null)
 java.runtime.version = 11.0.12+7 (null)
 java.io.tmpdir = /tmp (null)
 user.dir = /home/joakim/code/jetty/distros/bases/ssl9 (null)
 user.language = en (null)
 user.country = US (null)

Jetty Environment:
-----------------
 jetty.version = 9.4.44.v20210927
 jetty.tag.version = jetty-9.4.44.v20210927
 jetty.build = 8da83308eeca865e495e53ef315a249d63ba9332
 jetty.home = /opt/jetty-home-9.4.44.v20210927
 jetty.base = /home/joakim/tmp/ssl9

Config Search Order:
--------------------
 <command-line>
 ${jetty.base} -> /home/joakim/tmp/ssl9
 ${jetty.home} -> /opt/jetty-home-9.4.44.v20210927


JVM Arguments:
--------------
 (no jvm args specified)

System Properties:
------------------
 (no system properties specified)

Properties:
-----------
 java.version = 11.0.12
 java.version.major = 11
 java.version.micro = 12
 java.version.minor = 0
 java.version.platform = 11
 jetty.base = /home/joakim/tmp/ssl9
 jetty.base.uri = file:///home/joakim/tmp/ssl9
 jetty.home = /opt/jetty-home-9.4.44.v20210927
 jetty.home.uri = file:///opt/jetty-home-9.4.44.v20210927
 runtime.feature.alpn = true

Jetty Server Classpath:
-----------------------
Version Information on 7 entries in the classpath.
Note: order presented here is how they would appear on the classpath.
      changes to the --module=name command line options will be reflected here.
 0:                    3.1.0 | ${jetty.home}/lib/servlet-api-3.1.jar
 1:                 3.1.0.M0 | ${jetty.home}/lib/jetty-schemas-3.1.jar
 2:         9.4.44.v20210927 | ${jetty.home}/lib/jetty-http-9.4.44.v20210927.jar
 3:         9.4.44.v20210927 | ${jetty.home}/lib/jetty-server-9.4.44.v20210927.jar
 4:         9.4.44.v20210927 | ${jetty.home}/lib/jetty-xml-9.4.44.v20210927.jar
 5:         9.4.44.v20210927 | ${jetty.home}/lib/jetty-util-9.4.44.v20210927.jar
 6:         9.4.44.v20210927 | ${jetty.home}/lib/jetty-io-9.4.44.v20210927.jar

Jetty Active XMLs:
------------------
 ${jetty.home}/etc/jetty-bytebufferpool.xml
 ${jetty.home}/etc/jetty-threadpool.xml
 ${jetty.home}/etc/jetty.xml
 ${jetty.home}/etc/jetty-http.xml
 ${jetty.home}/etc/jetty-ssl.xml
 ${jetty.home}/etc/jetty-ssl-context.xml
 ${jetty.home}/etc/jetty-https.xml
 ${jetty.home}/etc/jetty-customrequestlog.xml
```