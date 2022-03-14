FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=build/libs/book-service.jar
COPY ${JAR_FILE} book-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/book-service.jar"]