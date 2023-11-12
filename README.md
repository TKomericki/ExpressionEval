# ExpressionEval

This is a simple api for storing and evaluating logical expressions.
It consists of Spring Boot backend with H2 in-memory database.

## API

API is written in Java 17 with [Spring Boot](https://spring.io/projects/spring-boot) framework, it uses [H2](https://www.h2database.com/) as a database and maven as a build tool. 
The code is located in default `src/main/java` directory as part of the `com.lw.expressioneval` package.
To be able to use the API, you only need to have Java 17 and Maven (tested on versions `3.6.3` and `3.9.5`) installed on your machine.
- Run the application:
    - Using maven to start (position yourself in the root directory of the project): `mvn spring-boot:run`

API consists of a single controller (with a mapping `/api`) containing two endpoints (as instructed in [Exercise](https://github.com/leapwise/expression-evaluator)):
- `/expression` - used for storing logical expressions 
- `/evaluate` - used for evaluating stored logical expressions based on given JSON object as input

The API will use the default port 8080, and can be accessed on url: http://localhost:8080

### /expression
To access this endpoint, a POST request must be sent to http://localhost:8080/api/expression. The body of the request must contain a name and the value of the logical expression.
If the logical expression passes API validation, an id of the logical expression will be returned. Otherwise, a status 400 error response will be returned, containing information of what went wrong.

### /evaluate
To access this endpoint, a GET request must be sent to http://localhost:8080/api/evaluate/{expressionId}. The body of the request must contain a JSON object that is required to validate the expression.
The endpoint returns an evaluation of the logical expression with identifier `expressionId` given in URL using the provided JSON object.
In case of expression not existing in database or an error during evaluation, a status 400 error response will be returned, containing information of what went wrong.
