# Étape 1 : utiliser une image Java légère
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]