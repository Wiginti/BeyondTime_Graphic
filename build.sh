#!/bin/bash

# Création des répertoires nécessaires
mkdir -p bin
mkdir -p docs/javadoc
mkdir -p bin/saved_map

# Copie des niveaux sauvegardés
echo "Copie des niveaux sauvegardés..."
cp -r saved_map/* bin/saved_map/ 2>/dev/null || true

# Compilation des sources
echo "Compilation des sources..."
javac -d bin -cp src src/fr/beyondtime/main/Main.java

# Génération de la JavaDoc
echo "Génération de la documentation JavaDoc..."
javadoc -d docs/javadoc -sourcepath src -subpackages fr.beyondtime

# Création du JAR
echo "Création du JAR..."
cd bin
jar cvfe beyondtime.jar fr.beyondtime.main.Main fr/beyondtime/**/*.class saved_map/
cd ..

echo "Build terminé !"
echo "Pour exécuter le jeu : java -jar bin/beyondtime.jar" 