FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/dev-0.0.1-SNAPSHOT.jar /app/dev-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "dev-0.0.1-SNAPSHOT.jar"]