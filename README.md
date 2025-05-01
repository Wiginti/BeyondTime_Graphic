# BeyondTime

BeyondTime est un jeu d'aventure en 2D développé en Java avec JavaFX, où le joueur voyage à travers différentes époques historiques.

## Prérequis

- Java JDK 17 ou supérieur
- Maven 3.8 ou supérieur
- JavaFX (inclus dans les dépendances Maven)

## Installation

1. Clonez le dépôt :
```bash
git clone https://github.com/votre-username/BeyondTime.git
cd BeyondTime
```

## Exécution

Il existe plusieurs façons d'exécuter le projet selon votre système d'exploitation :

### Méthode 1 : Utilisation de Maven (Recommandée, tous systèmes)

Cette méthode fonctionne sur tous les systèmes d'exploitation (Windows, macOS, Linux) :

```bash
mvn clean compile javafx:run
```

### Méthode 2 : Utilisation des scripts

#### Pour Windows :
```batch
# Compilation et création du JAR
build.bat

# OU pour exécuter directement avec Maven
run.bat
```

#### Pour macOS/Linux :
```bash
# Rendre les scripts exécutables
chmod +x build.sh run.sh

# Compilation et création du JAR
./build.sh

# OU pour exécuter directement avec Maven
./run.sh
```

### Méthode 3 : Exécution du JAR (après compilation)

Si vous avez déjà compilé le projet avec `build.bat` ou `build.sh` :

```bash
# Windows
java --module-path "chemin/vers/javafx/lib" --add-modules javafx.controls,javafx.fxml -jar bin/beyondtime.jar

# macOS/Linux
java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -jar bin/beyondtime.jar
```

## Structure du Projet

Le projet suit une architecture MVC (Modèle-Vue-Contrôleur) :

- `model/` : Classes de données et logique métier
- `view/` : Interface utilisateur JavaFX
- `controller/` : Gestion des interactions utilisateur
- `resources/` : Assets du jeu (images, traductions)

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

