FROM eclipse-temurin:21-jdk-alpine

WORKDIR /plateplanner-paymentservice

COPY target/paymentservice-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java","-jar","app.jar"]