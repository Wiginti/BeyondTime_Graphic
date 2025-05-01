package fr.beyondtime.controller.editor;

import fr.beyondtime.model.editor.EditorModel;
import fr.beyondtime.model.editor.EditorModel.TileType;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.util.MapManager;
import fr.beyondtime.view.screens.EditorScreen;
import fr.beyondtime.view.screens.MenuScreen;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class EditorController {

    private final EditorModel model;
    private final EditorScreen view;
    private final Stage stage;

    public EditorController(Stage stage) {
        this.stage = stage;
        this.model = new EditorModel();
        this.view = new EditorScreen();
        this.view.setController(this);
        this.stage.setScene(new Scene(view));
        setupInitialConfigEventHandlers();
    }

    private void setupInitialConfigEventHandlers() {
        view.getStartButton().setOnAction(e -> handleCreateMap(view.getRowsValue(), view.getColumnsValue()));
        view.getModifyButton().setOnAction(e -> handleModifyMap());
        view.getReturnButton().setOnAction(e -> handleReturn());
    }

    public void handleCreateMap(int rows, int columns) {
        model.setGridRows(rows);
        model.setGridColumns(columns);
        GridPane grid = createMapGrid(rows, columns);
        model.setMapGrid(grid);
        view.buildEditorUI(grid, rows, columns);
        setupEditorUIEventHandlers();
    }

    public void handleReturn() {
        MenuScreen menuScreen = new MenuScreen(stage);
        stage.setScene(menuScreen.getMenuScene());
    }

    private void setupEditorUIEventHandlers() {
        view.getClearButton().setOnAction(e -> handleClearGrid());
        view.getEraserButton().setOnAction(e -> toggleEraserMode());
        view.getNormalPropButton().setOnAction(e -> setCurrentTileType(TileType.NORMAL));
        view.getObstaclePropButton().setOnAction(e -> setCurrentTileType(TileType.OBSTACLE));
        view.getSlowPropButton().setOnAction(e -> setCurrentTileType(TileType.SLOW));
        view.getPoisonPropButton().setOnAction(e -> setCurrentTileType(TileType.POISON));
        view.getSpawnerPropButton().setOnAction(e -> setCurrentTileType(TileType.SPAWNER));
        view.getClearPropButton().setOnAction(e -> setCurrentTileType(null));
        view.getSaveButton().setOnAction(e -> handleSaveMap());
        view.getExitButton().setOnAction(e -> handleReturn());
    }

    public void toggleEraserMode() {
        model.setEraserMode(!model.isEraserMode());
    }

    public void setCurrentTileType(TileType type) {
        model.setCurrentTileType(type);
        if (type != null) model.setEraserMode(false);
    }

    private void handleClearGrid() {
        for (var node : model.getMapGrid().getChildren()) {
            if (node instanceof StackPane cell) {
                clearCell(cell);
            }
        }
    }

    private void clearCell(StackPane cell) {
        cell.getChildren().clear();
        Rectangle background = new Rectangle(model.getCellSize(), model.getCellSize());
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        cell.getProperties().put("tile", new Tile(true, 1.0, 0));
        cell.getProperties().remove("isSpawner");
        model.unmarkSpawner(cell);
    }

    private void handleCellClick(StackPane cell) {
        if (model.getCurrentTileType() != null) {
            switch (model.getCurrentTileType()) {
                case NORMAL -> model.setCellAsNormal(cell);
                case OBSTACLE -> model.setCellAsObstacle(cell);
                case SLOW -> model.setCellAsSlowZone(cell);
                case POISON -> model.setCellAsPoison(cell);
                case SPAWNER -> {
                    model.setCellAsSpawner(cell);
                    cell.getProperties().put("isSpawner", true);
                }
            }
        } else if (model.isEraserMode()) {
            clearCell(cell);
        }
    }

    private GridPane createMapGrid(int rows, int cols) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setHgap(0);
        grid.setVgap(0);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(model.getCellSize(), model.getCellSize());
                Rectangle background = new Rectangle(model.getCellSize(), model.getCellSize());
                background.setFill(Color.LIGHTGRAY);
                background.setStroke(Color.BLACK);
                cell.getChildren().add(background);
                cell.getProperties().put("tile", new Tile(true, 1.0, 0));
                cell.setOnMouseClicked(event -> handleCellClick(cell));
                grid.add(cell, col, row);
            }
        }
        return grid;
    }

    @SuppressWarnings("unused")
    private StackPane getCellAt(int row, int col) {
        for (var node : model.getMapGrid().getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col && node instanceof StackPane) {
                return (StackPane) node;
            }
        }
        return null;
    }

    public void handleSaveMap() {
        if (model.getMapGrid() == null) {
            view.showAlert("Erreur", "Aucune carte à sauvegarder.");
            return;
        }

        List<String> choices = List.of("Préhistoire", "Égypte Antique", "2nde Guerre Mondiale", "Custom Map");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Sauvegarde de la Map");
        dialog.setHeaderText("Choisissez le niveau classique ou nommez votre carte");
        dialog.setContentText("Niveau/Nom :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(chosenLevel -> {
            MapManager.saveMap(model.getMapGrid(), model.getGridRows(), model.getGridColumns(), chosenLevel);
        });
    }

    public void handleModifyMap() {
        File saveDir = new File("saved_map");
        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".map"));
        if (files == null || files.length == 0) {
            view.showAlert("Modification", "Aucune carte existante à modifier.");
            return;
        }

        List<String> choices = Arrays.stream(files).map(File::getName).toList();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Chargement de Map");
        dialog.setHeaderText("Sélectionnez une carte");
        dialog.setContentText("Carte :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedFileName -> {
            File file = Arrays.stream(files)
                    .filter(f -> f.getName().equals(selectedFileName))
                    .findFirst()
                    .orElse(null);
            if (file != null) {
                GridPane loadedGrid = MapManager.loadMapFromFile(file);
                if (loadedGrid != null) {
                    model.setMapGrid(loadedGrid);
                    model.setGridRows((int) loadedGrid.getRowCount());
                    model.setGridColumns((int) loadedGrid.getColumnCount());
                    view.buildEditorUI(loadedGrid, model.getGridRows(), model.getGridColumns());
                    setupEditorUIEventHandlers();
                }
            }
        });
    }
}
