# Manuel Utilisateur - BeyondTime

## Introduction

BeyondTime est un jeu d'aventure en 2D où vous incarnez un héros voyageant à travers différentes époques historiques. Affrontez des monstres, évitez les pièges et trouvez la sortie de chaque niveau pour progresser dans votre voyage temporel.

## Installation

1. Téléchargez la dernière version du jeu
2. Assurez-vous d'avoir Java 17 ou supérieur installé
3. Double-cliquez sur le fichier JAR ou exécutez :
   ```bash
   java -jar beyondtime-1.0.0.jar
   ```

## Menu Principal

Le menu principal propose les options suivantes :
- **Niveau 1 - Préhistoire** : Premier niveau du jeu
- **Niveau 2 - Égypte Antique** : Deuxième niveau
- **Niveau 3 - 2nde Guerre Mondiale** : Troisième niveau
- **Éditeur** : Accès à l'éditeur de niveaux
- **Configuration** : Paramètres du jeu
- **Quitter** : Fermer le jeu

## Contrôles du Jeu

### Déplacement
- **Z** ou **↑** : Haut
- **S** ou **↓** : Bas
- **Q** ou **←** : Gauche
- **D** ou **→** : Droite
- **SHIFT** : Sprint (consomme de la stamina)

### Combat
- **Clic Gauche** : Attaquer
- Période d'invincibilité après avoir subi des dégâts
- Dégâts affichés en pop-up au-dessus des entités

### Inventaire et Items
- **1-5** : Sélectionner un slot d'inventaire
- **F** : Utiliser l'item sélectionné
- Capacité : 5 slots
- Types d'items :
  - Potions de soin
  - Armes

### Système
- **ÉCHAP** : Menu pause
- **H** : Aide (dans le menu pause)

## Gameplay

### Stamina
- La barre de stamina se vide en sprintant
- Se régénère automatiquement au repos
- Gestion stratégique nécessaire pour les combats et la fuite

### Types de Terrain
- **Normal** : Terrain traversable
- **Obstacle** : Bloque le passage
- **Ralentissement** : Réduit la vitesse de déplacement
- **Poison** : Inflige des dégâts continus
- **Sortie** : Permet de terminer le niveau

### Monstres
- Poursuivent le joueur à proximité
- Infligent des dégâts au contact
- Réapparaissent après un certain temps
- Différents types selon les niveaux
- Points de vie variables

### Progression
- Chaque niveau doit être complété pour accéder au suivant
- Score basé sur les monstres vaincus
- Écran de victoire avec statistiques
- Possibilité de rejouer les niveaux

## Éditeur de Niveaux

### Création d'une Carte
1. Cliquez sur "Éditeur" dans le menu principal
2. Choisissez les dimensions de la carte
3. Utilisez les outils pour placer les éléments

### Outils Disponibles
- **Normal** : Créer une tuile traversable
- **Obstacle** : Placer un mur ou obstacle
- **Ralentissement** : Zone de ralentissement
- **Poison** : Zone de dégâts continus et ralentissement
- **Spawner** : Point d'apparition des monstres
- **Exit** : Point de sortie du niveau
- **Start** : Point de départ du héros
- **Gomme** : Effacer un élément
- **Assets** : Placer des éléments visuels

### Fonctionnalités
- Annulation de la dernière action (Undo)
- Sauvegarde automatique
- Prévisualisation des effets

### Sauvegarde
1. Cliquez sur "Sauvegarder"
2. Choisissez un nom pour votre niveau
3. Le niveau est sauvegardé avec tous ses paramètres

## Configuration

### Affichage
- Résolutions disponibles :
  - 800x600
  - 1280x720
  - 1920x1080
- Plein écran / Fenêtré

### Langue
- Français
- English
- Interface mise à jour en temps réel

## Conseils et Astuces

1. **Combat**
   - Utilisez le sprint pour éviter les monstres
   - Profitez de la période d'invincibilité
   - Gérez votre stamina

2. **Exploration**
   - Évitez les zones de poison (lave, poison,...)
   - Utilisez le terrain à votre avantage
   - Cherchez des routes alternatives

3. **Ressources**
   - Conservez les potions pour les moments critiques
   - Utilisez les items stratégiquement
   - Surveillez votre santé

4. **Éditeur**
   - Commencez par placer le point de départ
   - Équilibrez la difficulté
   - Testez régulièrement votre niveau

## Résolution des Problèmes

### Le jeu ne démarre pas
- Vérifiez votre version de Java
- Assurez-vous d'avoir les droits d'exécution
- Consultez les logs d'erreur

### Problèmes graphiques
- Essayez une résolution plus basse
- Mettez à jour vos pilotes graphiques
- Désactivez les effets visuels

### Erreurs de sauvegarde
- Vérifiez les permissions du dossier
- Libérez de l'espace disque
- Utilisez un autre emplacement de sauvegarde

### Performance
- Fermez les applications en arrière-plan
- Réduisez la résolution
- Désactivez les animations non essentielles

