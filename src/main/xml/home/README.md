This contents of custom.properties was obtained form using jetty-home / jetty-base

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

[ssl9]$ cat start.ini
```