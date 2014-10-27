Axelor Demo
================================

Axelor Demo is a simple application showing [Axelor Development Kit](https://github.com/axelor/axelor-development-kit) features. Axelor Demo is built on top of **Axelor Development Kit**. Before continuing, you should install [Axelor Development Kit](https://github.com/axelor/axelor-development-kit).

Installation
-----------------------------------------
#### Clone the latest release.
```bash
$ git clone git@github.com:axelor/axelor-demo.git
```
#### Build the package
```bash
$ cd /path/to/axelor-demo
$ ./gradlew -x test build
```
This should generate the war package for under build/libs directory. You can test the war by deploying on your tomcat server.
#### Run the tomcat server
Now to test the application using the embedded tomcat server, first edit the application.properties and configure the database to use and then run the following command from the interactive shell.
```bash
$ ./gradlew -x test tomcatRun
```
The application should start printing some logs in your terminal window. After few seconds, you should see something like this:
```bash
...
Started Tomcat Server
The Server is running at http://localhost:8080/axelor-hello
```
Launch the browser and open the application url as printed on terminal. Use the default **admin/admin** as the user name and password. You should be in the application.

Eclipse
-----------------------------------------
The application project can be imported in Eclipse IDE. In order to import the project, you first have to generate eclipse project files like this:
```bash
$ cd /path/to/axelor-demo
$ ./gradlew cleanEclipse eclipse
```
This will generate eclipse project files for the application project and all it’s submodules.

From the eclipse, import the projects using `File -> Import... -> General -> Existing Projects into Workspace` menu. In the import project wizard check the Search for nested projects so that all the submodules are also imported.

Same steps should be done for Axelor Development Kit in order to link ADK sources with Demo application.

You can also run the application inside eclipse using eclipse WTP tools. Create a tomcat7 server add the axelor-demo module and run the server.
