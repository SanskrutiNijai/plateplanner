FROM eclipse-temurin:21-jdk-alpine

WORKDIR /plateplanner-eurekaserver

COPY target/eurekaserver-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

CMD ["java", "-jar", "app.jar"]
