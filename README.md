# Book Retailer Service

The purpose of this service is that providing some APIs for book retailer shop. The user can create `customer`,
`book`, `order` and then can see some `statistics` for each customer regarding of their orders.

## Run the application

This service can run either via `gradle` or via `docker`.

#### Run the service via Gradle

1. First run the `gradle clean build` command to produce the application jar
2. Then run the `gradle bootRun` command, the application will start on the http://localhost:8080
3. If you do not have gradle installed in your local environment then it would be enough to replace gradle command with `gradlew` command.
