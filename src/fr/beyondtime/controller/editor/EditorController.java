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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private File currentAssetFolder = new File("assets");
    private File selectedAsset = null;

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
        loadAssetList();
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
        view.getExitPropButton().setOnAction(e -> setCurrentTileType(TileType.SORTIE));
        view.getClearPropButton().setOnAction(e -> {
            setCurrentTileType(null);
            view.getItemTypeComboBox().setVisible(false);
        });
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
        cell.setUserData(null);
        model.unmarkSpawner(cell);
    }

    private void handleCellClick(StackPane cell) {
        if (model.isEraserMode()) {
            clearCell(cell);
            return;
        }

        clearCell(cell);

        // Asset d'abord (pour tous les types de tuiles)
        if (selectedAsset != null) {
            Image img = new Image(selectedAsset.toURI().toString(), model.getCellSize(), model.getCellSize(), true, true);
            ImageView iv = new ImageView(img);
            iv.setFitWidth(model.getCellSize());
            iv.setFitHeight(model.getCellSize());
            cell.getChildren().add(iv);
            String relativePath = model.getRelativeAssetPath(selectedAsset);
            cell.setUserData(relativePath);
        }

        // Puis overlay transparent
        if (model.getCurrentTileType() != null) {
            Rectangle overlay = new Rectangle(model.getCellSize(), model.getCellSize());
            overlay.setFill(getOverlayColor(model.getCurrentTileType()));
            overlay.setOpacity(0.4);
            cell.getChildren().add(overlay);

            switch (model.getCurrentTileType()) {
                case NORMAL -> model.setCellAsNormal(cell);
                case OBSTACLE -> model.setCellAsObstacle(cell);
                case SLOW -> model.setCellAsSlowZone(cell);
                case POISON -> model.setCellAsPoison(cell);
                case SPAWNER -> {
                    model.setCellAsSpawner(cell);
                    cell.getProperties().put("isSpawner", true);
                }
                case SORTIE -> {
                    model.setCellAsExit(cell);
                    cell.getProperties().put("isExit", true);
                }
            }
        }
    }

    private Color getOverlayColor(TileType type) {
        return switch (type) {
            case NORMAL -> Color.TRANSPARENT;
            case OBSTACLE -> Color.BLACK;
            case SLOW -> Color.BLUE;
            case POISON -> Color.PURPLE;
            case SPAWNER -> Color.RED;
            case SORTIE -> Color.GREEN;
        };
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
        loadAssetList();
    }

    public void loadAssetList() {
        if (!currentAssetFolder.exists() || !currentAssetFolder.isDirectory()) {
            view.showAlert("Erreur", "Le dossier des assets n'existe pas : " + currentAssetFolder.getAbsolutePath());
            return;
        }

        File[] files = currentAssetFolder.listFiles();
        if (files == null) return;

        List<EditorScreen.AssetEntry> entries = new ArrayList<>();

        File rootFolder = new File("assets");
        if (!currentAssetFolder.equals(rootFolder)) {
            entries.add(new EditorScreen.AssetEntry(currentAssetFolder.getParentFile(), true));
        }

        for (File file : files) {
            if (file.isDirectory() || file.isFile()) {
                entries.add(new EditorScreen.AssetEntry(file, false));
            }
        }

        ListView<EditorScreen.AssetEntry> listView = view.getAssetListView();
        listView.getItems().setAll(entries);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(EditorScreen.AssetEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item.isBack()) {
                        setText("🔙 " + item.getName());
                        setGraphic(null);
                    } else if (item.getFile().isDirectory()) {
                        setText("📁 " + item.getName());
                        setGraphic(null);
                    } else if (isImageFile(item.getFile())) {
                        Image image = new Image(item.getFile().toURI().toString(), 50, 50, true, true);
                        ImageView imageView = new ImageView(image);
                        setText(null);
                        setGraphic(imageView);
                    } else {
                        setText(item.getName());
                        setGraphic(null);
                    }
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            EditorScreen.AssetEntry selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.isBack()) {
                    currentAssetFolder = selected.getFile();
                    loadAssetList();
                } else if (selected.getFile().isDirectory()) {
                    currentAssetFolder = selected.getFile();
                    loadAssetList();
                } else if (isImageFile(selected.getFile())) {
                    selectedAsset = selected.getFile();
                }
            }
        });
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }
}
