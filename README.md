# Point-of-sale API

This is a multi-module Maven project.

## Modules Description

 - **pos-model** contains classes and interfaces common to server and client
 - **pos-server** contains Spring Boot application which serves REST Point-of-sale API
 - **pos-client** contains Java client for the Point-of-sale API (used in controller tests)
 
To build and run tests, execute from command line:

```shell
mvn clean install
```

or without tests

```shell
mvn clean install -DskipTests
```
