# Automated tests for Spring Boot application

This project I made for training purposes is about creating automated tests on the Spring Boot application. For this, I developed a simple REST API to deal with data from fictional planets, which comes from the Star Wars franchise. To do that I followed the instructions from [@Giuliana Bezerra](https://github.com/giuliana-bezerra) in her [Udemy course](https://www.udemy.com/course/testes-automatizados-na-pratica-com-spring-boot).

## How to config and run the application:

To set up and run the server, you need to create a MySQL database, add a file `/src/main/resources/application.properties` which the following content, and grant permissions to the MySQL user as well. Don't forget to replace fields like `database name`, `username`, and `password`.

```
spring.jpa.hibernate.ddl-auto=update    
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/{database name}
spring.datasource.username={username}    
spring.datasource.password={password}    
spring.datasource.driver-class-name=com.mysql.jdbc.Driver    
```
Now, go to the root project and run the command below to locally download a specific version of Gradle. Note: Older versions of Gradle aren't able to perform this action. I tested it with Gradle 7.5.1, and it worked.

```sh 
gradle wrapper
```

After setting the configurations, Spring Boot should be able to run the application. To do that, run the following Gradle task and access `http://localhost:8080/planets`:

```sh
./gradlew bootRun
```
At this point, the API will retrieve an empty list. However, you can handle data using HTTP verbs. 

### REST API Methods
You might want to access the endpoints using the following cURL commands:

GET
```sh
curl http://localhost:8080/planets
curl http://localhost:8080/planets/{id}
curl http://localhost:8080/planets/name/{planet name}
```
POST
```sh
curl -X POST -H "Content-Type: application/json" \
    -d '{
	 "name": "Kamino",
	 "climate": "glacial",
	 "terrain": "ocean"
	}' \
     http://localhost:8080/planets
```
DELETE
```sh
curl -X DELETE http://localhost:8080/planets/{id}
```

## How to config and run the tests:
The tests cover unit, integration, component, and end-to-end tests. As the unit test, the integration test is ready to run because the [H2 Database](https://www.h2database.com/html/main.html) will be invoked in memory, and isn't necessarily taking additional action.

However, for the component and end-to-end tests, the test and production environments use the same type of database. So, in this case, you need to create another property file.
