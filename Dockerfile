# Étape 1 : utiliser une image Java 17 stable
FROM eclipse-temurin:17-jdk-focal

# Étape 2 : ajouter le jar généré
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Étape 3 : exposer le port utilisé par Spring Boot
EXPOSE 8083

# Étape 4 : exécuter l'application
ENTRYPOINT ["java", "-jar", "/app.jar"]
