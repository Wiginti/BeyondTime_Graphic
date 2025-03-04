package fr.beyondtime.view;

import fr.beyondtime.controller.HeroController;
import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.entities.HeroView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameView {

    private Hero hero;
    private HeroView heroView;
    private HeroController heroController;
    private Pane rootPane;
    private Scene scene;
    private HUDView hud; // Affichage tête haute (barre de vie et inventaire)

    private static final int DEFAULT_CELL_SIZE = 50;

    // Timeline pour appliquer périodiquement les dégâts de poison
    private Timeline poisonTimeline;

    // Référence à la grille de jeu
    private GridPane mapGrid;

    // Constructeur par défaut (création d'une grille simple)
    public GameView(Stage stage) {
        // Création d'une grille par défaut avec une unique cellule
        GridPane defaultGrid = new GridPane();
        StackPane cell = new StackPane();
        cell.setPrefSize(DEFAULT_CELL_SIZE, DEFAULT_CELL_SIZE);
        Rectangle background = new Rectangle(DEFAULT_CELL_SIZE, DEFAULT_CELL_SIZE);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        cell.getProperties().put("tile", new Tile(true, 1.0, 0)); // Pas de poison par défaut
        defaultGrid.add(cell, 0, 0);

        this.mapGrid = defaultGrid; // On conserve la référence à la grille

        // Initialisation du héros et de sa vue
        hero = new Hero();
        heroView = new HeroView();
        heroController = new HeroController(hero, heroView, defaultGrid, DEFAULT_CELL_SIZE);

        // Création du conteneur principal de la scène
        rootPane = new Pane();
        // On ajoute d'abord la grille
        rootPane.getChildren().add(defaultGrid);
        // Création et ajout du HUD (ici 5 cœurs et 5 slots d'inventaire)
        hud = new HUDView(5, 5);
        rootPane.getChildren().add(hud);
        // Ensuite, on ajoute la vue du héros
        rootPane.getChildren().add(heroView);

        scene = new Scene(rootPane, 800, 600);
        scene.setOnKeyPressed(event -> heroController.handleKeyEvent(event));
        stage.setScene(scene);
        stage.show();

        // Initialisation de la barre de vie (santé maximale = 100 → 5 cœurs)
        hud.updateHealth(hero.getHealth() / 20.0);

        startPoisonTimer();
    }

    // Constructeur utilisant une grille déjà chargée (par exemple, depuis une map sauvegardée)
    public GameView(Stage stage, GridPane mapGrid) {
        this.mapGrid = mapGrid; // On conserve la grille chargée
        // Vous pouvez masquer la grille si nécessaire en modifiant l'affichage (voir hideGridDisplay)
        // hideGridDisplay(mapGrid);

        hero = new Hero();
        heroView = new HeroView();
        heroController = new HeroController(hero, heroView, mapGrid, DEFAULT_CELL_SIZE);

        rootPane = new Pane();
        rootPane.getChildren().add(mapGrid);
        hud = new HUDView(5, 5);
        rootPane.getChildren().add(hud);
        heroView.setPosition(50, 50);
        rootPane.getChildren().add(heroView);

        scene = new Scene(rootPane, 800, 600);
        scene.setOnKeyPressed(event -> heroController.handleKeyEvent(event));
        stage.setScene(scene);
        stage.show();

        hud.updateHealth(hero.getHealth() / 20.0);

        startPoisonTimer();
    }

    /**
     * Démarre une timeline qui toutes les 5 secondes vérifie
     * si le héros est sur une case de type poison et, le cas échéant,
     * lui inflige 10 points de dégâts (soit un demi-cœur).
     */
    private void startPoisonTimer() {
        poisonTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            // Détermine la position du héros en fonction de la grille
            double heroX = heroView.getLayoutX();
            double heroY = heroView.getLayoutY();
            int col = (int) (heroX / DEFAULT_CELL_SIZE);
            int row = (int) (heroY / DEFAULT_CELL_SIZE);

            // Recherche la cellule correspondante dans la grille
            StackPane currentCell = null;
            for (javafx.scene.Node node : mapGrid.getChildren()) {
                Integer nodeCol = GridPane.getColumnIndex(node);
                Integer nodeRow = GridPane.getRowIndex(node);
                if (nodeCol == null) nodeCol = 0;
                if (nodeRow == null) nodeRow = 0;
                if (nodeCol == col && nodeRow == row && node instanceof StackPane) {
                    currentCell = (StackPane) node;
                    break;
                }
            }

            if (currentCell != null) {
                Object tileObj = currentCell.getProperties().get("tile");
                if (tileObj instanceof Tile) {
                    Tile tile = (Tile) tileObj;
                    if (tile.getDamage() > 0) {
                        // Le héros subit des dégâts de poison (10 points = 0.5 cœur)
                        hero.removeHealth(10);
                        // Mise à jour du HUD : conversion de la santé en nombre de cœurs (si 100 = 5 cœurs)
                        hud.updateHealth(hero.getHealth() / 20.0);
                        System.out.println("Le héros subit " + tile.getDamage() + " dégâts de poison. Santé restante : " + hero.getHealth());
                        // Vous pouvez ajouter ici une vérification pour un Game Over si hero.getHealth() <= 0.
                    }
                }
            }
        }));
        poisonTimeline.setCycleCount(Timeline.INDEFINITE);
        poisonTimeline.play();
    }

    /**
     * Méthode utilitaire pour masquer les traits de la grille si besoin.
     */
    private void hideGridDisplay(GridPane grid) {
        grid.getChildren().forEach(node -> {
            if (node instanceof StackPane) {
                StackPane cell = (StackPane) node;
                cell.getChildren().forEach(child -> {
                    if (child instanceof Rectangle) {
                        ((Rectangle) child).setStroke(null);
                    }
                });
            }
        });
    }
}