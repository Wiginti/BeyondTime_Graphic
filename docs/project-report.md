# Rapport de Projet - BeyondTime

## Choix de Conception

### Architecture MVC

Nous avons choisi le pattern MVC pour :
- Séparer clairement la logique métier (Model) de l'interface utilisateur (View)
- Faciliter la maintenance et l'évolution du code
- Permettre le travail en parallèle sur différentes parties du projet
- Assurer une meilleure testabilité du code

### JavaFX

Le choix de JavaFX comme framework graphique s'est imposé pour :
- Sa compatibilité native avec Java
- Ses performances pour les jeux 2D
- Son système de scènes et de composants réutilisables
- Sa gestion efficace des événements
- Son support des animations fluides

### Système de Tuiles

Le système de carte basé sur des tuiles permet :
- Une création facile de niveaux
- Une gestion optimisée des collisions
- Une flexibilité dans la conception des niveaux
- Des effets variés (poison, ralentissement)
- Un placement stratégique des monstres

### Internationalisation

L'implémentation du support multilingue via ResourceBundle :
- Facilite l'ajout de nouvelles langues
- Centralise la gestion des textes
- Améliore la maintenabilité
- Permet une expérience utilisateur localisée

## Fonctionnalités Implémentées

### Système de Jeu
- Déplacement fluide avec gestion des collisions ✓
- Système de combat avec attaques au corps à corps ✓
- Gestion de la stamina et du sprint ✓
- Système d'inventaire à 5 slots ✓
- Effets de statut (poison, ralentissement) ✓
- Système de victoire et game over ✓

### Système de Monstres
- IA de poursuite du joueur ✓
- Système de dégâts avec affichage ✓
- Respawn dynamique ✓
- Points de spawn configurables ✓

### Interface Utilisateur
- HUD avec barre de vie et inventaire ✓
- Menu principal et pause ✓
- Écrans de victoire et défaite ✓
- Aide contextuelle ✓

### Éditeur de Niveaux
- Interface intuitive ✓
- Types de tuiles variés ✓
- Système d'annulation ✓
- Sauvegarde/chargement ✓
- Prévisualisation des effets ✓

## Difficultés Rencontrées

### 1. Gestion des Collisions
**Problème** : Détection imprécise des collisions avec les obstacles.
**Solution** : 
- Implémentation d'un système de hitbox avec vérification par cellule
- Optimisation des calculs de collision
- Gestion des cas limites

### 2. Performance de l'Éditeur
**Problème** : Ralentissements avec de grandes cartes.
**Solution** : 
- Optimisation du rendu
- Chargement asynchrone des assets
- Mise en cache des images
- Limitation des mises à jour inutiles

### 3. Système de Combat
**Problème** : Équilibrage et retour visuel.
**Solution** :
- Ajout d'effets visuels de dégâts
- Système d'invincibilité temporaire
- Équilibrage des statistiques

### 4. Gestion de la Caméra
**Problème** : Suivi fluide du personnage.
**Solution** :
- Implémentation d'une caméra centrée
- Gestion des bords de carte
- Optimisation des performances

## Gestion de Projet

### État d'Avancement Actuel
- Architecture de base : 100% ✓
- Interface utilisateur : 95% ✓
- Éditeur de niveaux : 90% ✓
- Système de jeu : 85% ✓
- Documentation : 80% ✓
- Tests et débogage : 0% 

### Prochaines Étapes
1. Finalisation des tests unitaires
2. Optimisation des performances
3. Ajout de nouveaux types de monstres
4. Amélioration des effets visuels
5. Enrichissement du contenu des niveaux

### Répartition des Tâches

#### Marco DUPRE
- Développement global du projet 
- Architecture MVC 
- Interface utilisateur
- Refactoring de code
- Éditeur de niveaux 
- Système de combat 
- Gestion des monstres 
- HUD et menus 
- Documentation 

#### Nicolas MOREAU
- Développement global du projet 
- Architecture MVC 
- Éditeur de niveaux 
- Inferface utilisateur
- Système de sauvegarde 
- Configuration Maven et script de compil 
- Documentation
- Rapport 
- Gestion de la traduction, transition entre les niveaux...
- Création map Egypte


#### Ruben COFFLARD
- Développement global du projet 
- Système d'inventaire 
- Gestion de la caméra 
- Interface utilisateur 
- Effets visuels 
- Tests d'intégration
- Gestion de la traduction
- Maps

#### Tarik TAHOUNE
- Déplacement du personnage en diagonale 

### Outils Utilisés

- **Git** : Gestion de version et collaboration
- **Maven** : Build et dépendances
- **JUnit** : Tests unitaires
- **Trello** : Suivi des tâches
- **Discord** : Communication d'équipe
- **JavaFX Scene Builder** : Design d'interface

### Méthodologie

Nous avons adopté une approche Agile avec :
- Sprints de 2 semaines
- Daily meetings
- Code reviews systématiques
- Tests continus
- Documentation régulière

## Perspectives d'Amélioration

1. **Gameplay**
   - Nouveaux types de monstres
   - Système de quêtes
   - Capacités spéciales
   - Boss de fin de niveau

2. **Technique**
   - Optimisation des performances
   - Support du multijoueur
   - Sauvegarde en ligne
   - Achievements

3. **Contenu**
   - Nouvelles époques
   - Plus d'items et d'armes
   - Cinématiques
   - Histoire approfondie

4. **Interface**
   - Animations améliorées
   - Effets sonores
   - Musique d'ambiance
   - Tutoriel interactif

## Conclusion

Le projet BeyondTime a permis de mettre en pratique les concepts de POO tout en créant un jeu fonctionnel et engageant. Les choix architecturaux ont facilité le développement collaboratif et l'évolution du projet. L'utilisation de patterns de conception appropriés et une bonne organisation du code ont permis de créer une base solide pour de futures améliorations.

### Points Forts
- Architecture modulaire et extensible
- Interface utilisateur intuitive
- Gameplay fluide et réactif
- Documentation complète
- Tests automatisés

### Axes d'Amélioration
- Couverture de tests à augmenter
- Optimisation des performances
- Plus de contenu de jeu
- Support multiplateforme
