# Beyond Time

Un jeu d'aventure à travers différentes époques historiques.

## Prérequis

- Java 17 ou supérieur
- Maven
- Git

## Installation

1. Cloner le projet :
```bash
git clone https://github.com/Wiginti/BeyondTime_Graphic.git
cd BeyondTime_Graphic
```

2. Compiler et exécuter le jeu :

### Sur Unix (Linux/macOS) :
```bash
chmod +x run_jar.sh
./run_jar.sh
```

### Sur Windows :
```bash
mvn clean package
java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED --module-path %USERPROFILE%\.m2\repository\org\openjfx --add-modules javafx.controls,javafx.fxml -jar target/beyondtime-1.0.0.jar
```

## Résolution des problèmes courants

### Erreur "JavaFX runtime components are missing"
Cette erreur signifie que les composants JavaFX ne sont pas correctement configurés. Le script `run_jar.sh` gère automatiquement cette configuration. Sur Windows, assurez-vous d'utiliser la commande complète avec les options du module JavaFX.

### Erreur "Maven is not recognized"
Assurez-vous que Maven est installé et ajouté à votre PATH système.

## Structure du projet

- `src/` : Code source du jeu
- `assets/` : Ressources graphiques
- `saved_map/` : Cartes sauvegardées

## Fonctionnalités

- Système de jeu en 2D avec déplacement fluide
- Éditeur de niveaux intégré
- Support multilingue (Français/Anglais)
- Configuration personnalisable (résolution, langue)
- Système de sauvegarde des niveaux

## Documentation

La documentation JavaDoc est générée automatiquement lors de la compilation avec les scripts `build.bat` ou `build.sh`. Vous pouvez la consulter en ouvrant `docs/javadoc/index.html` dans votre navigateur.

## Contribution

1. Fork le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Committez vos changements (`git commit -am 'Ajout d'une nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créez une Pull Request

