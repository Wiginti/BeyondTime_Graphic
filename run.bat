@echo off
REM Compile le projet
echo Compilation du projet...
mvn clean compile

REM Vérifie si la compilation a réussi
if errorlevel 1 (
    echo La compilation a échoué
    exit /b 1
)

REM Configure les options de la JVM pour JavaFX
set JAVA_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED

REM Exécute l'application
echo Exécution de l'application...
mvn javafx:run -Djavafx.run.options="%JAVA_OPTS%" 