# Book Retailer Service

The purpose of this service is that providing some APIs for book retailer shop. The user can create `customer`,
`book`, `order` and then can see some `statistics` for each customer regarding of their orders.

## Run the application

1. First run the `gradle clean build` command to produce the application jar
2. Go to docker folder and run this command `docker-compose up` in order to connect to mongo database.
3. Then run the `gradle bootRun` command, the application will start on port `8080`
4. If you do not have gradle installed in your local environment then it would be enough to replace gradle command with `gradlew` command.
5. To be able to send the request the application you must have bearer token. To get bearer token you need to send the
request to `/authenticate` endpoint with following credentials.(username: `foo` password: `foo`)
6. You can see all postman requests under the postman folder.

## Run the tests
If you'd like to run the tests, then you should run `gradle test` command, and it will run all tests.

## Run the integration tests
If you'd like to run the integration tests, then you should run `gradle integrationTest` command, and it will run all tests.
To be able to run integration tests you must run docker compose file as well. 

You can run both unit tests and integration tests via either `gradle clean build` or `gradle check` command.

### Access the postman queries
You can find the related postman queries under `postman` folder.

### Access the Swagger
You can access the swagger via this link http://localhost:8080/swagger-ui.html. Security is disabled for swagger feature.

### Access the Actuator health endpoint
You can access the swagger via this link http://localhost:8080/actuator/health. Security is disabled for actuator health endpoint feature.