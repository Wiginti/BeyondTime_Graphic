package fr.beyondtime.view.screens;

import fr.beyondtime.controller.HeroController;
import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.entities.Item;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.model.map.GameMap;
import fr.beyondtime.model.game.GameState;
import fr.beyondtime.view.entities.HeroView;
import fr.beyondtime.view.components.HUDView;
import fr.beyondtime.util.ImageLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.image.Image;

public class GameScreen {

    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;
    private static final int CELL_SIZE = 50;
    private static final String CSS_PATH = "/fr/beyondtime/resources/style.css";
    private static final int MAX_HEARTS_DISPLAY = 10;
    private static final int DEFAULT_INVENTORY_SLOTS = 8;

    private Stage primaryStage;
    private GameState gameState;
    private HUDView hudView;
    private HeroController heroController;
    private HeroView heroView;
    private Group cameraGroup;
    private GridPane mapGrid;

    public GameScreen(Stage stage, GameState gameState) {
        this.primaryStage = stage;
        this.gameState = gameState;
        initializeScreen(stage);
    }

    private void initializeScreen(Stage stage) {
        stage.setTitle("BeyondTime Game");

        cameraGroup = new Group();
        System.out.println("GameScreen: Getting map grid from GameState...");
        mapGrid = gameState.getMap().getMapGrid();
        if (mapGrid == null) {
            System.err.println("FATAL ERROR: GameState provided a null map grid!");
            mapGrid = new GridPane();
        }
        cameraGroup.getChildren().add(mapGrid);
        
        heroView = new HeroView();
        cameraGroup.getChildren().add(heroView);

        int inventorySlots = DEFAULT_INVENTORY_SLOTS;
        hudView = new HUDView(MAX_HEARTS_DISPLAY, inventorySlots);

        StackPane rootStack = new StackPane();
        rootStack.getChildren().add(cameraGroup);
        rootStack.getChildren().add(hudView);
        hudView.setMouseTransparent(true);

        Scene scene = new Scene(rootStack, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(javafx.scene.paint.Color.LIGHTGRAY);

        try {
            URL cssUrl = getClass().getResource(CSS_PATH);
            if (cssUrl == null) {
                System.err.println("ERROR in GameScreen: CSS file not found at classpath: " + CSS_PATH);
            } else {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("GameScreen CSS loaded successfully from: " + cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("ERROR loading CSS file in GameScreen: " + CSS_PATH);
            e.printStackTrace();
        }

        Hero hero = gameState.getHero();
        if (hero == null) {
             System.err.println("FATAL ERROR: GameState provided a null hero!");
             return; 
        }
        heroController = new HeroController(hero, heroView, mapGrid, CELL_SIZE);
        heroController.setOnUpdate(this::update);
        
        scene.setOnKeyPressed(event -> {
            heroController.handleKeyPress(event);
            if (event.getCode() == KeyCode.ESCAPE) {
                // Retourner au menu principal
                MenuScreen menuScreen = new MenuScreen(stage);
                stage.setScene(menuScreen.getMenuScene());
            }
        });
        scene.setOnKeyReleased(heroController::handleKeyRelease);

        stage.setScene(scene);
        stage.show();

        updateCamera();
        updateHUD();
    }

    private void update() {
        updateHUD();
        updateCamera();
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

    private void updateCamera() {
        if (heroController == null) return;

        double heroX = heroController.getWorldX();
        double heroY = heroController.getWorldY();

        // Centrer la caméra sur le héros
        // Utiliser les constantes de HeroController pour la taille du héros
        double cameraX = heroX - SCENE_WIDTH / 2 + HeroController.HERO_WIDTH / 2;
        double cameraY = heroY - SCENE_HEIGHT / 2 + HeroController.HERO_HEIGHT / 2;

        // Limites de la carte en pixels
        double mapWidth = mapGrid.getColumnCount() * CELL_SIZE;
        double mapHeight = mapGrid.getRowCount() * CELL_SIZE;

        // Empêcher la caméra de sortir des limites de la carte
        cameraX = Math.max(0, Math.min(cameraX, mapWidth - SCENE_WIDTH));
        cameraY = Math.max(0, Math.min(cameraY, mapHeight - SCENE_HEIGHT));

        // Appliquer le décalage à la caméra
        // Le groupe contient la map et le héros, on le déplace
        cameraGroup.setTranslateX(-cameraX);
        cameraGroup.setTranslateY(-cameraY);

        // Logging pour débogage
        // System.out.println(String.format("Hero World: (%.1f, %.1f), Camera Offset: (%.1f, %.1f)", 
        //                                heroX, heroY, -cameraX, -cameraY));
    }
} 