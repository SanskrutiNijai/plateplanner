FROM eclipse-temurin:21-jdk-alpine

WORKDIR /plateplanner-recipeintelligenceservice

COPY target/recipeintelligenceservice-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","app.jar"]