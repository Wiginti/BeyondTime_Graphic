package fr.beyondtime.view;

import fr.beyondtime.controller.HeroController;
import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.entities.HeroView;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameView {

    private Hero hero;
    private HeroView heroView;
    private HeroController heroController;
    private Pane rootPane;
    private Scene scene;

    private static final int DEFAULT_CELL_SIZE = 50;

    public GameView(Stage stage) {
        GridPane defaultGrid = new GridPane();
        StackPane cell = new StackPane();
        cell.setPrefSize(DEFAULT_CELL_SIZE, DEFAULT_CELL_SIZE);
        Rectangle background = new Rectangle(DEFAULT_CELL_SIZE, DEFAULT_CELL_SIZE);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        cell.getProperties().put("tile", new Tile(true, 1.0));
        defaultGrid.add(cell, 0, 0);

        hero = new Hero();
        heroView = new HeroView();
        heroController = new HeroController(hero, heroView, defaultGrid, DEFAULT_CELL_SIZE);

        rootPane = new Pane();
        rootPane.getChildren().add(heroView);
        scene = new Scene(rootPane, 800, 600);
        scene.setOnKeyPressed(event -> heroController.handleKeyEvent(event));
        stage.setScene(scene);
        stage.show();
    }

    public GameView(Stage stage, GridPane mapGrid) {
        hero = new Hero();
        heroView = new HeroView();
        heroController = new HeroController(hero, heroView, mapGrid, DEFAULT_CELL_SIZE);

        rootPane = new Pane();
        rootPane.getChildren().add(mapGrid);
        heroView.setPosition(50, 50);
        rootPane.getChildren().add(heroView);
        scene = new Scene(rootPane, 800, 600);
        scene.setOnKeyPressed(event -> heroController.handleKeyEvent(event));
        stage.setScene(scene);
        stage.show();
    }
}