# Assignment

This project is created to show Spring boot and JMS capabilities.

## Building

Assignment uses [Apache Maven](http://maven.apache.org/) for a build system. 
To build the library run maven from the root of the repository.

    % mvn clean install

To build and run Spring boot application run:

    % mvn spring-boot:run

Before running application please make sure you properly configured AppacheMQ connection parameters at [application.yml](src/main/resources/application.yml)

##### Standalone JAR file

  To run standalone application without compiling, please use [assignment-1.0.jar](assignment-1.0.jar)
  