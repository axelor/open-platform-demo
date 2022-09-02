# Open Platform Demo

Open Platform Demo is a simple application showing [Axelor Open Platform](https://github.com/axelor/axelor-open-platform) features.

## Installation

Make sure you have JDK 11 and Git installed.

Clone the latest sources:

```bash
$ git clone git@github.com:axelor/open-platform-demo.git
```

and build the project:

```bash
$ cd /path/to/open-platform-demo
$ ./gradlew -x test build
```

This should generate the war package for under build/libs directory. You can test the war by deploying on your tomcat server.

You can also test the application using the embedded tomcat server. First edit the `axelor-config.properties` and configure the database to use and then run the following command from the interactive shell.

```bash
$ ./gradlew --no-daemon run
```

The application should start printing some logs in your terminal window. After few seconds, you should see something like this:
```bash
...
Ready to serve...

Running at http://localhost:8080/open-platform-demo
```

Launch the browser and open the application url as printed on terminal. Use the default **admin/admin** as the user name and password. You should be in the application.

## Eclipse

The application project can be imported in Eclipse IDE. In order to import the project, you first have two options:

1. Using buildship 2.1 plugin

    ```bash
    $ ./gradlew classes copyWebapp
    ```

    This will make sure all the required classes are generated and resource required for web ui are copied.

    From eclipse, import the project using `File -> Import... -> Gradle -> Existing Gradle Project` menu.

2. Generate eclipse project files like this:

    ```bash
    $ cd /path/to/open-platform-demo
    $ ./gradlew classes copyWebapp cleanEclipse eclipse
    ```

    This will generate eclipse project files for the application project and all it’s submodules.

    From the eclipse, import the projects using `File -> Import... -> General -> Existing Projects into Workspace` menu. In the import project wizard check the Search for nested projects so that all the submodules are also imported.

You can also run the application inside eclipse using eclipse WTP tools. Create a tomcat8.5 server add the open-platform-demo module and run the server.
