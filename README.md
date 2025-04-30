# BeyondTime

BeyondTime est un jeu d'aventure en 2D développé en Java avec JavaFX, où le joueur voyage à travers différentes époques historiques.

## Prérequis

- Java JDK 17 ou supérieur
- Maven 3.8 ou supérieur

## Installation

1. Clonez le dépôt :
```bash
git clone https://github.com/votre-username/BeyondTime.git
cd BeyondTime
```

2. Compilez le projet avec Maven :
```bash
mvn clean install
```

3. Exécutez le jeu :
```bash
java -jar target/beyondtime-1.0.0.jar
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

- La documentation technique complète est disponible dans le dossier `docs/`
- La JavaDoc est générée dans `target/site/apidocs/`
- Le manuel utilisateur se trouve dans `docs/user-manual.pdf`

## Contribution

1. Fork le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Committez vos changements (`git commit -am 'Ajout d'une nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créez une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.
