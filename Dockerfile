FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/student-management-0.0.4-RELEASE.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8089"]