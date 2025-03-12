package fr.beyondtime.view;

import fr.beyondtime.controller.HeroController;
import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.entities.HeroView;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import static fr.beyondtime.view.MenuView.showNiveauScene;

public class GameView {

    private Hero hero;
    private HeroView heroView;
    private HeroController heroController;
    private Pane rootPane;
    private Scene scene;
    private HUDView hud;
    private Group cameraGroup; // Conteneur regroupant la carte et le héros

    private static final int DEFAULT_CELL_SIZE = 50;

    private Timeline poisonTimeline;

    private GridPane mapGrid;

    public GameView(Stage stage) {
        // Création d'une grille par défaut
        GridPane defaultGrid = new GridPane();
        StackPane cell = new StackPane();
        cell.setPrefSize(DEFAULT_CELL_SIZE, DEFAULT_CELL_SIZE);
        Rectangle background = new Rectangle(DEFAULT_CELL_SIZE, DEFAULT_CELL_SIZE);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        cell.getProperties().put("tile", new Tile(true, 1.0, 0));
        defaultGrid.add(cell, 0, 0);
        this.mapGrid = defaultGrid;

        hero = new Hero();
        heroView = new HeroView();
        heroController = new HeroController(hero, heroView, defaultGrid, DEFAULT_CELL_SIZE);

        hideGridDisplay(defaultGrid);

        rootPane = new Pane();

        // Création du groupe caméra qui contiendra la carte et le héros
        cameraGroup = new Group();
        cameraGroup.getChildren().add(mapGrid);
        cameraGroup.getChildren().add(heroView);
        rootPane.getChildren().add(cameraGroup);

        // Ajout du HUD (qui restera fixe)
        hud = new HUDView(5, 5);
        rootPane.getChildren().add(hud);

        // Ajout du bouton Quitter visible pendant le jeu
        addQuitButton(stage);

        scene = new Scene(rootPane, 800, 600);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
            heroController.handleKeyPress(event);
        });

        scene.setOnKeyReleased(event -> {
            heroController.handleKeyRelease(event);
        });

        stage.setScene(scene);
        stage.show();

        hud.updateHealth(hero.getHealth() / 20.0);

        startPoisonTimer();
        startCamera(); // Démarrage de la mise à jour de la caméra
    }

    public GameView(Stage stage, GridPane mapGrid) {
        this.mapGrid = mapGrid;

        hero = new Hero();
        heroView = new HeroView();
        heroController = new HeroController(hero, heroView, mapGrid, DEFAULT_CELL_SIZE);

        rootPane = new Pane();

        // Création du groupe caméra qui contiendra la carte et le héros
        cameraGroup = new Group();
        cameraGroup.getChildren().add(mapGrid);
        cameraGroup.getChildren().add(heroView);
        rootPane.getChildren().add(cameraGroup);

        hud = new HUDView(5, 5);
        rootPane.getChildren().add(hud);

        // Position initiale du héros
        heroView.setPosition(50, 50);

        // Ajout du bouton Quitter visible pendant le jeu
        addQuitButton(stage);

        scene = new Scene(rootPane, 800, 600);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
            heroController.handleKeyPress(event);
        });

        scene.setOnKeyReleased(event -> {
            heroController.handleKeyRelease(event);
        });

        stage.setScene(scene);
        stage.show();

        hud.updateHealth(hero.getHealth() / 20.0);

        startPoisonTimer();
        startCamera(); // Activation de la caméra qui suit le héros
    }

    /**
     * Méthode permettant d'ajouter un bouton "Quitter" qui, lorsqu'il est cliqué,
     * arrête les animations et renvoie vers le menu principal (MenuView).
     */
    private void addQuitButton(Stage stage) {
        Button btnQuit = new Button("Quitter");
        // Positionnement fixe en haut à droite de la scène (ajustez les valeurs si nécessaire)
        btnQuit.setLayoutX(720);
        btnQuit.setLayoutY(10);
        btnQuit.setOnAction(ActionEvent -> showNiveauScene(stage));
        rootPane.getChildren().add(btnQuit);
    }

    /**
     * AnimationTimer qui met à jour en continu la translation du groupe caméra pour centrer le héros dans la scène.
     */
    private void startCamera() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                double sceneWidth = scene.getWidth();
                double sceneHeight = scene.getHeight();
                // Calcul du centre du héros
                double heroWidth = heroView.getBoundsInLocal().getWidth();
                double heroHeight = heroView.getBoundsInLocal().getHeight();
                double heroX = heroView.getLayoutX();
                double heroY = heroView.getLayoutY();
                double heroCenterX = heroX + heroWidth / 2;
                double heroCenterY = heroY + heroHeight / 2;
                // Calcul des décalages pour centrer le héros dans la scène
                double offsetX = sceneWidth / 2 - heroCenterX;
                double offsetY = sceneHeight / 2 - heroCenterY;
                cameraGroup.setTranslateX(offsetX);
                cameraGroup.setTranslateY(offsetY);
            }
        }.start();
    }

    private void startPoisonTimer() {
        poisonTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            double heroX = heroView.getLayoutX();
            double heroY = heroView.getLayoutY();
            int col = (int) (heroX / DEFAULT_CELL_SIZE);
            int row = (int) (heroY / DEFAULT_CELL_SIZE);

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
                        hero.removeHealth(10);
                        hud.updateHealth(hero.getHealth() / 20.0);
                        System.out.println("Le héros subit " + tile.getDamage() + " dégâts de poison. Santé restante : " + hero.getHealth());
                    }
                }
            }
        }));
        poisonTimeline.setCycleCount(Timeline.INDEFINITE);
        poisonTimeline.play();
    }

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
