# Étape 1 : utiliser une image Java légère
FROM openjdk:17-jdk-slim

# Étape 2 : ajouter le jar généré dans le conteneur
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Étape 3 : exposer le port (change-le si ton app utilise un autre)
EXPOSE 8083

# Étape 4 : exécuter ton app
ENTRYPOINT ["java", "-jar", "/app.jar"]
