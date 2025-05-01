@echo off
REM Création des répertoires nécessaires
mkdir bin 2>nul
mkdir docs\javadoc 2>nul
mkdir bin\fr\beyondtime\resources 2>nul

REM Nettoyage du répertoire bin
echo Nettoyage du répertoire bin...
rmdir /s /q bin
mkdir bin\fr\beyondtime\resources

REM Copie des ressources
echo Copie des ressources...
xcopy /s /y src\fr\beyondtime\resources\* bin\fr\beyondtime\resources\

REM Compilation des sources
echo Compilation des sources...
dir /s /b src\fr\beyondtime\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt

REM Génération de la JavaDoc
echo Génération de la documentation JavaDoc...
javadoc -d docs\javadoc -sourcepath src -subpackages fr.beyondtime

REM Création du JAR
echo Création du JAR...
cd bin
jar cvfe beyondtime.jar fr.beyondtime.main.Main .
cd ..

echo Build terminé !
echo Pour exécuter le jeu : java --module-path "chemin/vers/javafx/lib" --add-modules javafx.controls,javafx.fxml -jar bin/beyondtime.jar 