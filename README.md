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

Il existe plusieurs façons d'exécuter le projet :

### Méthode 1 : Utilisation de Maven (Recommandée)

Cette méthode fonctionne sur tous les systèmes d'exploitation (Windows, macOS, Linux) :

1. Compilation et exécution en une seule commande :
```bash
mvn clean compile javafx:run
```

Ou étape par étape :

1. Compilation :
```bash
mvn clean compile
```

2. Exécution :
```bash
mvn javafx:run
```

### Méthode 2 : Utilisation du script shell (Linux/macOS)

1. Rendez le script exécutable :
```bash
chmod +x run.sh
```

2. Exécutez le script :
```bash
./run.sh
```

### Méthode 3 : Exécution via JAR (Tous les systèmes)

1. Création du JAR :
```bash
mvn clean package
```

2. Exécution du JAR :
- Windows :
```cmd
java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -jar target/beyondtime-1.0.0.jar
```
- Linux/macOS :
```bash
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -jar target/beyondtime-1.0.0.jar
```

Note : Remplacez `%PATH_TO_FX%` ou `$PATH_TO_FX` par le chemin vers votre installation JavaFX si vous n'utilisez pas les dépendances Maven.

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

- La documentation technique complète est disponible dans le dossier `docs/`
- La JavaDoc est générée dans `target/site/apidocs/`
- Le manuel utilisateur se trouve dans `docs/user-manual.pdf`

## Contribution

1. Fork le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Committez vos changements (`git commit -am 'Ajout d'une nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créez une Pull Request

