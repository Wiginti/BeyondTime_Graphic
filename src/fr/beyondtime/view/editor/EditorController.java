package fr.beyondtime.view.editor;

import fr.beyondtime.model.map.Tile;
import fr.beyondtime.util.MapManager;
import fr.beyondtime.view.MenuView;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class EditorController {
    private EditorModel model;
    private EditorView view;
    
    public EditorController() {
        this.model = new EditorModel();
    }
    
    public void setView(EditorView view) {
        this.view = view;
    }
    
    public EditorModel getModel() {
        return model;
    }
    
    public void handleCreateMap(int rows, int columns) {
        model.setGridRows(rows);
        model.setGridColumns(columns);
        GridPane grid = createMapGrid(rows, columns);
        view.buildEditorUI(grid, rows, columns);
    }
    
    public void handleModifyMap() {
        File saveDir = new File("saved_maps");
        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".map"));
        if (files == null || files.length == 0) {
            view.showAlert("Modification", "Aucune carte existante à modifier.");
            return;
        }
        
        List<String> choices = new ArrayList<>();
        for (File f : files) {
            choices.add(f.getName());
        }
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Choix de la carte à modifier");
        dialog.setHeaderText("Sélectionnez une carte personnalisée");
        dialog.setContentText("Carte : ");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedFileName -> {
            File selectedFile = Arrays.stream(files)
                .filter(f -> f.getName().equals(selectedFileName))
                .findFirst()
                .orElse(null);
                
            if (selectedFile != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    String firstLine = reader.readLine();
                    if (firstLine != null) {
                        String[] dims = firstLine.split(",");
                        int rows = Integer.parseInt(dims[0].trim());
                        int columns = Integer.parseInt(dims[1].trim());
                        GridPane loadedGrid = MapManager.loadMapFromFile(selectedFile);
                        if (loadedGrid != null) {
                            view.buildEditorUI(loadedGrid, rows, columns);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    public void handleReturn() {
        Stage stage = (Stage) view.getScene().getWindow();
        MenuView.showNiveauScene(stage);
    }
    
    public void handleClearGrid() {
        for (javafx.scene.Node node : model.getMapGrid().getChildren()) {
            if (node instanceof StackPane) {
                clearCell((StackPane) node);
            }
        }
    }
    
    public void toggleEraserMode() {
        model.setEraserMode(!model.isEraserMode());
    }
    
    public void setCurrentTileType(EditorModel.TileType type) {
        model.setCurrentTileType(type);
    }
    
    public void handleSaveMap() {
        List<String> choices = List.of(
            "Niveau 1 - Préhistoire", 
            "Niveau 2 - Égypte Antique", 
            "Niveau 3 - 2nde Guerre Mondiale"
        );
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Sauvegarde de la Map");
        dialog.setHeaderText("Choisissez le niveau classique de sauvegarde");
        dialog.setContentText("Niveau :");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(chosenLevel -> {
            MapManager.saveMap(
                model.getMapGrid(), 
                model.getGridRows(), 
                model.getGridColumns(), 
                chosenLevel
            );
        });
    }
    
    public ListView<EditorView.AssetEntry> createAssetListView() {
        ListView<EditorView.AssetEntry> listView = new ListView<>();
        updateAssetListView(listView);
        
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isBack() && newVal.getFile().isFile()) {
                try {
                    InputStream is = getClass().getResourceAsStream("/fr/beyondtime/assets" 
                        + newVal.getFile().getAbsolutePath().split("assets")[1]);
                    if (is != null) {
                        model.setSelectedAssetImage(new Image(is));
                        model.setSelectedAssetPath("/fr/beyondtime/assets" 
                            + newVal.getFile().getAbsolutePath().split("assets")[1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                EditorView.AssetEntry selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    if (selected.isBack()) {
                        model.setCurrentDirectory(model.getCurrentDirectory().getParentFile());
                        updateAssetListView(listView);
                    } else if (selected.getFile().isDirectory()) {
                        model.setCurrentDirectory(selected.getFile());
                        updateAssetListView(listView);
                    }
                }
            }
        });
        
        listView.setCellFactory(lv -> new ListCell<>() {  // Notez les <> vides pour l'inférence de type
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
            }
            
            @Override
            protected void updateItem(EditorView.AssetEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if (item.isBack()) {
                        setText(item.getName());
                        setGraphic(null);
                    } else if (item.getFile().isDirectory()) {
                        setText(item.getFile().getName());
                        setGraphic(null);
                    } else {
                        try {
                            InputStream is = getClass().getResourceAsStream("/fr/beyondtime/assets" 
                                + item.getFile().getAbsolutePath().split("assets")[1]);
                            if (is != null) {
                                Image image = new Image(is);
                                imageView.setImage(image);
                                setGraphic(imageView);
                                setText(null);
                            } else {
                                setGraphic(null);
                                setText(item.getFile().getName());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        
        listView.setOnDragDetected(event -> {
            EditorView.AssetEntry selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.isBack() && selected.getFile().isFile()) {
                Dragboard db = listView.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                try {
                    InputStream is = getClass().getResourceAsStream("/fr/beyondtime/assets" 
                        + selected.getFile().getAbsolutePath().split("assets")[1]);
                    if (is != null) {
                        content.putImage(new Image(is));
                        db.setContent(content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            event.consume();
        });
        
        return listView;
    }
    
    public void updateAssetListView(ListView<EditorView.AssetEntry> listView) {
        listView.getItems().clear();
        if (!model.getCurrentDirectory().equals(model.getRootAssets())) {
            listView.getItems().add(new EditorView.AssetEntry(
                model.getCurrentDirectory().getParentFile(), true));
        }
        
        File[] files = model.getCurrentDirectory().listFiles();
        if (files != null) {
            Arrays.stream(files)
                .filter(File::isDirectory)
                .forEach(f -> listView.getItems().add(new EditorView.AssetEntry(f, false)));
            Arrays.stream(files)
                .filter(f -> f.isFile() && f.getName().toLowerCase().endsWith(".png"))
                .forEach(f -> listView.getItems().add(new EditorView.AssetEntry(f, false)));
        }
    }
    
    private GridPane createMapGrid(int rows, int columns) {
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(0));
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                StackPane cell = createGridCell();
                grid.add(cell, col, row);
            }
        }
        return grid;
    }
    
    private StackPane createGridCell() {
        StackPane cell = new StackPane();
        cell.setPrefSize(model.getCellSize(), model.getCellSize());
        
        Rectangle background = new Rectangle(model.getCellSize(), model.getCellSize());
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        
        cell.getProperties().put("tile", new Tile(true, 1.0, 0));
        
        cell.setOnMouseClicked(event -> handleCellClick(cell));
        cell.setOnDragOver(event -> handleDragOver(event, cell));
        cell.setOnDragDropped(event -> handleDragDropped(event, cell));
        
        return cell;
    }
    
    private void handleCellClick(StackPane cell) {
        if (model.getCurrentTileType() != null) {
            switch (model.getCurrentTileType()) {
                case NORMAL: model.setCellAsNormal(cell); break;
                case OBSTACLE: model.setCellAsObstacle(cell); break;
                case SLOW: model.setCellAsSlowZone(cell); break;
                case POISON: model.setCellAsPoison(cell); break;
            }
        } else {
            if (model.isEraserMode()) {
                clearCell(cell);
            } else {
                placeAssetOnCell(cell);
            }
        }
    }
    
    private void handleDragOver(DragEvent event, StackPane cell) {
        if (event.getGestureSource() != cell && event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }
    
    private void handleDragDropped(DragEvent event, StackPane cell) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasImage()) {
            placeAssetOnCell(cell);
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }
    
    private void placeAssetOnCell(StackPane cell) {
        if (model.getSelectedAssetImage() != null) {
            cell.getChildren().clear();
            Rectangle newBackground = new Rectangle(model.getCellSize(), model.getCellSize());
            newBackground.setFill(Color.LIGHTGRAY);
            newBackground.setStroke(Color.BLACK);
            cell.getChildren().add(newBackground);
            
            ImageView assetView = new ImageView(model.getSelectedAssetImage());
            assetView.setFitWidth(model.getCellSize());
            assetView.setFitHeight(model.getCellSize());
            cell.getChildren().add(assetView);
            cell.setUserData(model.getSelectedAssetPath());
        }
    }
    
    private void clearCell(StackPane cell) {
        cell.getChildren().clear();
        Rectangle background = new Rectangle(model.getCellSize(), model.getCellSize());
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        cell.setUserData(null);
    }
}