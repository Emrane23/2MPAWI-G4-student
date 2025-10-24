FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]