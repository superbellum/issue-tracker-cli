FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/issue-tracker-cli-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]