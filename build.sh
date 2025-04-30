#!/bin/bash

# Création des répertoires nécessaires
mkdir -p bin
mkdir -p docs/javadoc

# Compilation des sources
echo "Compilation des sources..."
javac -d bin -cp src src/fr/beyondtime/main/Main.java

# Génération de la JavaDoc
echo "Génération de la documentation JavaDoc..."
javadoc -d docs/javadoc -sourcepath src -subpackages fr.beyondtime

# Création du JAR
echo "Création du JAR..."
cd bin
jar cvfe beyondtime.jar fr.beyondtime.main.Main fr/beyondtime/**/*.class
cd ..

echo "Build terminé !"
echo "Pour exécuter le jeu : java -jar bin/beyondtime.jar" 