Guidelines for Cookbook
=======================

Naming:
-------

Use the name of the feature you are highlighting, then the role you are applying.

eg:

    {Feature}{Role}

    DefaultServletBasic
    DefaultServletMultipleBases
    ResourceHandlerFromFileSystem
    ResourceHandlerFromJar
    ServerConnectorSecureHttp1
    ServerConnectorSecureHttp2
    ServerConnectorRedirectToSecure
    JndiBasedDataSource
    JmxEnabledServer
    StatisticsHandlerExample
    LowResourceMonitorExample

If your example is uses only the feautre name, then use "Example" for your Role.

What Makes a Good Example:
--------------------------

 * Favor Classpath references for external configuration
   (such as keystore or resource bases)
 * If a FileSystem reference example is created, also ensure that a
   classpath reference version of the example exists as well
 * Each example should be as standalone as possible, avoid DRY principles.
 * Base each example off of classes found in `<dependency>` sections, avoid
   creating "utility" or "common" classes in the project that multiple
   examples reference.
 * Keep the example simple and focused on the role you are attempting to
   present.
 * Avoid decomposing your example (such as creating too many methods) this
   just increases the size of the example and makes it more difficult to
   follow.
 * Minimize error handling
   (like try/catch blocks on server.start() / server.join())
   this will simplify the example greatly.
 * Avoid code that shows all of the features and configurations of a component
   (like HttpConfiguration), keep it to a minimum in order to keep the
   examples easier to follow
