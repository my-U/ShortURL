FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} short-url.jar

ENTRYPOINT ["java", "-jar", "short-url.jar"]
