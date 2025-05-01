#!/bin/bash

# Compile et crée le JAR
echo "Construction du JAR..."
mvn clean package

# Vérifie si la construction a réussi
if [ $? -ne 0 ]; then
    echo "La construction a échoué"
    exit 1
fi

# Trouve le chemin des modules JavaFX dans les dépendances Maven
JAVAFX_MODULES=$(find ~/.m2/repository/org/openjfx -name "*.jar" | tr '\n' ':')

# Configure les options de la JVM pour JavaFX
JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED --module-path $JAVAFX_MODULES --add-modules javafx.controls,javafx.fxml"

# Exécute le JAR
echo "Exécution du JAR..."
java $JAVA_OPTS -jar target/beyondtime-1.0.0.jar 