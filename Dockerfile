# Use Java 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy JAR to container
COPY target/student-management-0.0.3-RELEASE.jar app.jar

# Expose port
EXPOSE 8089

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
