package fr.beyondtime.view.screens;

import fr.beyondtime.controller.HeroController;
import fr.beyondtime.controller.MonsterController;
import fr.beyondtime.controller.MonsterSpawnerController;
import fr.beyondtime.controller.game.GameController;
import fr.beyondtime.model.config.GameConfig;
import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.entities.Item;
import fr.beyondtime.model.game.GameState;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.util.ImageLoader;
import fr.beyondtime.util.TranslationManager;
import fr.beyondtime.view.components.HUDView;
import fr.beyondtime.view.entities.HeroView;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameScreen {
    private static final int CELL_SIZE = 50;
    private static final int MAX_HEARTS_DISPLAY = 10;
    private static final int DEFAULT_INVENTORY_SLOTS = 5;
    private static final String CSS_PATH = "/fr/beyondtime/resources/style.css";

    @SuppressWarnings("unused")
	private Stage primaryStage;
    private GameState gameState;
    private HUDView hudView;
    private HeroController heroController;
    private HeroView heroView;
    private Group cameraGroup;
    private GridPane mapGrid;
    private GameController gameController;

    public GameScreen(Stage stage, GameState gameState) {
        this.primaryStage = stage;
        this.gameState = gameState;
        
        initializeScreen(stage);
        
        // Créer le GameController après l'initialisation de l'écran
        this.gameController = new GameController(stage, gameState);
        
        // Lier le GameController au HeroController
        if (heroController != null) {
            heroController.setGameController(gameController);
        }
        
        this.gameController.startGame();
        System.out.println("GameScreen: Game loop started");
    }

    private void initializeScreen(Stage stage) {
        int SCENE_WIDTH = GameConfig.getInstance().getCurrentResolution().getWidth();
        int SCENE_HEIGHT = GameConfig.getInstance().getCurrentResolution().getHeight();

        stage.setTitle("BeyondTime Game");
        stage.setWidth(SCENE_WIDTH);
        stage.setHeight(SCENE_HEIGHT);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - SCENE_WIDTH) / 2);
        stage.setY(screenBounds.getMinY() + (screenBounds.getHeight() - SCENE_HEIGHT) / 2);

        cameraGroup = new Group();
        mapGrid = gameState.getMap().getMapGrid();
        if (mapGrid == null) mapGrid = new GridPane();
        cameraGroup.getChildren().add(mapGrid);

        heroView = new HeroView();
        cameraGroup.getChildren().add(heroView);

        // Set initial hero position from the model
        if (gameState.getHero() != null) {
            heroView.setPosition(gameState.getHero().getX(), gameState.getHero().getY());
        }

        hudView = new HUDView(MAX_HEARTS_DISPLAY, DEFAULT_INVENTORY_SLOTS);
        hudView.setMouseTransparent(true);

        Pane gameLayer = new Pane(cameraGroup);
        gameLayer.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);
        cameraGroup.setManaged(false);

        StackPane root = new StackPane(gameLayer, hudView);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(Color.LIGHTGRAY);

        try {
            URL cssUrl = getClass().getResource(CSS_PATH);
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS load failed: " + e.getMessage());
        }

        Hero hero = gameState.getHero();
        if (hero == null) return;

        heroController = new HeroController(hero, heroView, mapGrid, CELL_SIZE, hudView);
        
        MonsterSpawnerController spawnerController = new MonsterSpawnerController();
        @SuppressWarnings("unused")
		List<Node> monsterViews = spawnerController.spawnMonsters(mapGrid, cameraGroup, heroController, CELL_SIZE);
        
        List<MonsterController> monsterControllers = spawnerController.getActiveControllers();
        heroController.setMonsters(monsterControllers);

        scene.setOnKeyPressed(event -> {
            heroController.handleKeyPress(event);
            if (event.getCode() == KeyCode.ESCAPE) {
                // Créer et afficher la fenêtre de pause
                showPauseScreen(stage, scene);
            }
        });
        
        scene.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                heroController.attackNearbyMonsters();
            }
        });

        
        scene.setOnKeyReleased(heroController::handleKeyRelease);

        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                double heroX = hero.getX();
                double heroY = hero.getY();

                cameraGroup.setTranslateX(SCENE_WIDTH / 2.0 - heroX);
                cameraGroup.setTranslateY(SCENE_HEIGHT / 2.0 - heroY);
                
                for (MonsterController mc : monsterControllers) {
                    mc.update();
                }
            }
        }.start();

        updateHUD();
    }

    private void updateHUD() {
        if (hudView == null || gameState == null || gameState.getHero() == null) return;

        double healthProportion = (double) gameState.getHealth() / (double) Hero.DEFAULT_HEALTH;
        double healthValueForHUD = healthProportion * MAX_HEARTS_DISPLAY;
        hudView.updateHealth(healthValueForHUD);

        List<Item> items = gameState.getHero().getBag().getItems();
        List<Image> itemImages = new ArrayList<>();

        for (Item item : items) {
            Image itemImage = ImageLoader.loadImage(item.getImagePath());
            if (itemImage != null) {
                itemImages.add(itemImage);
            }
        }

        hudView.updateInventory(itemImages);
    }

    private void showPauseScreen(Stage stage, Scene gameScene) {
        // Pause the game
        gameController.pauseGame();

        final PauseScreen[] pauseScreenRef = new PauseScreen[1];
        pauseScreenRef[0] = new PauseScreen(
            stage,
            gameState,
            () -> {
                gameController.resumeGame();
            },
            () -> {
                gameController.stopGame();
                stage.setScene(new MenuScreen(stage).getMenuScene());
            },
            null  // La configuration est maintenant gérée directement dans PauseScreen
        );
        pauseScreenRef[0].show();
    }

    private void updateCell(StackPane cell) {
        // Supprimer tous les enfants de la cellule sauf l'asset
        List<Node> toKeep = new ArrayList<>();
        for (Node child : cell.getChildren()) {
            if (child instanceof ImageView) {
                toKeep.add(child);
            }
        }
        cell.getChildren().clear();
        cell.getChildren().addAll(toKeep);
        
        // Ajouter l'image de fond si elle n'existe pas déjà
        if (cell.getChildren().isEmpty()) {
            String imagePath = (String) cell.getUserData();
            if (imagePath != null) {
                try {
                    Image image = new Image(getClass().getResourceAsStream("/fr/beyondtime/resources/" + imagePath));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    cell.getChildren().add(imageView);
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image : " + imagePath);
                }
            }
        }
    }
}
