# Rapport de Projet - BeyondTime

## Choix de Conception

### Architecture MVC

Nous avons choisi le pattern MVC pour :
- Séparer clairement la logique métier (Model) de l'interface utilisateur (View)
- Faciliter la maintenance et l'évolution du code
- Permettre le travail en parallèle sur différentes parties du projet

### JavaFX

Le choix de JavaFX comme framework graphique s'est imposé pour :
- Sa compatibilité native avec Java
- Ses performances pour les jeux 2D
- Son système de scènes et de composants réutilisables
- Sa gestion efficace des événements

### Système de Tuiles

Le système de carte basé sur des tuiles permet :
- Une création facile de niveaux
- Une gestion optimisée des collisions
- Une flexibilité dans la conception des niveaux

### Internationalisation

L'implémentation du support multilingue via ResourceBundle :
- Facilite l'ajout de nouvelles langues
- Centralise la gestion des textes
- Améliore la maintenabilité

## Difficultés Rencontrées

### 1. Gestion des Collisions
**Problème** : Détection imprécise des collisions avec les obstacles.
**Solution** : Implémentation d'un système de hitbox avec vérification par cellule.

### 2. Performance de l'Éditeur
**Problème** : Ralentissements avec de grandes cartes.
**Solution** : 
- Optimisation du rendu
- Chargement asynchrone des assets
- Mise en cache des images

### 3. Sauvegarde des Niveaux
**Problème** : Format de sauvegarde complexe avec assets.
**Solution** : Création d'un format personnalisé avec chemins relatifs.

### 4. Réutilisation des Nodes JavaFX
**Problème** : Erreurs lors du changement de scène.
**Solution** : Création de nouvelles instances pour chaque scène.

## Gestion de Projet

### Planning

#### Phase 1 : Conception (2 semaines)
- Architecture MVC
- Diagrammes UML
- Maquettes UI

#### Phase 2 : Core Development (4 semaines)
- Système de jeu base
- Moteur de collision
- Gestion des assets

#### Phase 3 : Features (3 semaines)
- Éditeur de niveaux
- Système de combat
- Internationalisation

#### Phase 4 : Polish (2 semaines)
- Tests et débogage
- Documentation
- Optimisations

### Répartition des Tâches

#### Marco DUPRE
- Architecture MVC
- Editeur de niveaux
- Refactoring du code 
- Correction de bug 
- Rapport 


#### Nicolas MOREAU
- Architecture MVC 
- Interface utilisateur
- Éditeur de niveaux
- Correction de bug 
- Documentation et rapport 

#### Ruben COFFLARD
- Création des boutons 
- Correction de bugs 

#### Tarik TAHOUNE
- Déplacement en diagonale du personnage

### Outils Utilisés

- **Git** : Gestion de version
- **Maven** : Build et dépendances
- **Trello** : Suivi des tâches
- **Discord** : Communication d'équipe

### Méthodologie

Nous avons adopté une approche Agile avec :
- Sprints de 2 semaines
- Daily meetings
- Code reviews systématiques
- Tests continus

## Perspectives d'Amélioration

1. **Performances**
   - Optimisation du rendu
   - Chargement dynamique des zones

2. **Gameplay**
   - Nouveaux types d'ennemis
   - Système de quêtes
   - Plus d'interactions

3. **Technique**
   - Tests automatisés
   - Packaging multiplateforme
   - Mode multijoueur

## Conclusion

Le projet a permis de mettre en pratique les concepts de POO tout en créant un jeu fonctionnel. Les choix architecturaux ont facilité le développement collaboratif et l'évolution du projet. 