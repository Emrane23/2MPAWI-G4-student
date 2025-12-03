# Étape 1 : utiliser une image Java légère
FROM eclipse-temurin:17-jdk-alpine

# Étape 2 : ajouter le jar généré dans le conteneur
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Étape 3 : exposer le port
EXPOSE 8083

# Étape 4 : exécuter ton app
ENTRYPOINT ["java", "-jar", "/app.jar"]