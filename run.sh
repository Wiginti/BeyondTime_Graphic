#!/bin/bash

# Compile le projet
mvn clean compile

# Vérifie si la compilation a réussi
if [ $? -ne 0 ]; then
    echo "La compilation a échoué"
    exit 1
fi

# Configure les options de la JVM pour JavaFX
JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED"

# Exécute l'application
mvn javafx:run -Djavafx.run.options="$JAVA_OPTS" 