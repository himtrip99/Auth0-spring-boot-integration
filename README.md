# Auth0-spring-boot-integration



1.Clone the repository (git clone https://github.com/himtrip99/Auth0-spring-boot-integration)

2.Go to directory: Auth0-spring-boot-integration\src\main\resources , and update application.properties file with actual AUDIENCE,DOMAIN,CLIENT,SECRET values from the Custom Application

3.Open the repository locally in an IDE like IntelliJ or Eclipse which will automatically download project dependencies and you can run the application from IDE itself by navigating 
to 'Auth0-spring-boot-integration\src\main\java\com\auth0\app\SpringBootAuth0Application.java'.

2.or if you don't have IDE installed, Install maven to run the project directly from commandline. Maven installation guide https://maven.apache.org/install.html

3.cd Auth0-spring-boot-integration\

4.Once maven installed run : mvn spring-boot:run

5.Access below endpoint to get application and actions details:

http://localhost:8090/api/getApplicationActions

