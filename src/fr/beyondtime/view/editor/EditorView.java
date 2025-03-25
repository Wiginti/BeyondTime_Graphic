package fr.beyondtime.view.editor;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EditorView extends VBox {
    private EditorController controller;
    
    private GridPane mapGrid;
    private ListView<AssetEntry> assetListView;
    private HBox toolsBox;
    
    public EditorView(EditorController controller) {
        this.controller = controller;
        this.controller.setView(this);
    }
    
    public void showConfigPane() {
        VBox configPane = new VBox(10);
        configPane.getStyleClass().add("vbox-config");
        configPane.setAlignment(Pos.CENTER);

        Label configLabel = new Label("Configurer la taille de la grille");
        configLabel.getStyleClass().add("config-label");

        Label rowsLabel = new Label("Nombre de lignes :");
        rowsLabel.getStyleClass().add("config-input-label");
        TextField rowsField = new TextField("25");
        rowsField.getStyleClass().add("config-text-field");

        Label columnsLabel = new Label("Nombre de colonnes :");
        columnsLabel.getStyleClass().add("config-input-label");
        TextField columnsField = new TextField("25");
        columnsField.getStyleClass().add("config-text-field");

        Button startButton = new Button("Créer la carte");
        startButton.getStyleClass().add("classique-button");
        startButton.setOnAction(e -> controller.handleCreateMap(
            Integer.parseInt(rowsField.getText()), 
            Integer.parseInt(columnsField.getText())
        ));

        Button modifyButton = new Button("Modifier une carte existante");
        modifyButton.getStyleClass().add("classique-button");
        modifyButton.setOnAction(e -> controller.handleModifyMap());

        Button returnButton = new Button("Retour");
        returnButton.getStyleClass().add("classique-buttonn");
        returnButton.setOnAction(e -> controller.handleReturn());

        configPane.getChildren().addAll(configLabel, rowsLabel, rowsField, 
            columnsLabel, columnsField, startButton, modifyButton, returnButton);
        getChildren().add(configPane);
    }
    
    public void buildEditorUI(GridPane grid, int rows, int columns) {
        getChildren().clear();
        
        controller.getModel().setMapGrid(grid);
        controller.getModel().setGridRows(rows);
        controller.getModel().setGridColumns(columns);
        this.mapGrid = grid;
        
        MenuBar menuBar = createMenuBar();
        SplitPane splitPane = createSplitPane();
        toolsBox = createToolsBox();
        
        getChildren().addAll(menuBar, splitPane, toolsBox);
        setPadding(new Insets(10));
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem closeItem = new MenuItem("Close");
        closeItem.setOnAction(e -> controller.handleReturn());
        fileMenu.getItems().add(closeItem);
        
        Menu editMenu = new Menu("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        editMenu.getItems().add(deleteItem);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        return menuBar;
    }
    
    private SplitPane createSplitPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);

        AnchorPane leftPane = new AnchorPane();
        assetListView = controller.createAssetListView();
        assetListView.setPrefSize(200, 600);
        leftPane.getChildren().add(assetListView);

        ScrollPane scrollPane = new ScrollPane();
        AnchorPane contentPane = new AnchorPane();
        contentPane.setPrefHeight(800.0);
        contentPane.setPrefWidth(600.0);
        contentPane.getChildren().add(mapGrid);
        
        AnchorPane.setTopAnchor(mapGrid, 10.0);
        AnchorPane.setLeftAnchor(mapGrid, 10.0);
        scrollPane.setContent(contentPane);
        splitPane.getItems().addAll(leftPane, scrollPane);
        
        return splitPane;
    }
    
    private HBox createToolsBox() {
        Button clearButton = new Button("Effacer la grille");
        clearButton.getStyleClass().add("classique-button");
        clearButton.setOnAction(e -> controller.handleClearGrid());

        Button eraserButton = new Button("Gomme");
        eraserButton.getStyleClass().add("classique-buttonn");
        eraserButton.setOnAction(e -> controller.toggleEraserMode());

        Button normalPropButton = new Button("Normal");
        normalPropButton.getStyleClass().add("classique-button");
        normalPropButton.setOnAction(e -> controller.setCurrentTileType(EditorModel.TileType.NORMAL));

        Button obstaclePropButton = new Button("Obstacle");
        obstaclePropButton.getStyleClass().add("classique-button");
        obstaclePropButton.setOnAction(e -> controller.setCurrentTileType(EditorModel.TileType.OBSTACLE));

        Button slowPropButton = new Button("Ralentissement");
        slowPropButton.getStyleClass().add("classique-button");
        slowPropButton.setOnAction(e -> controller.setCurrentTileType(EditorModel.TileType.SLOW));

        Button poisonPropButton = new Button("Poison");
        poisonPropButton.getStyleClass().add("classique-button");
        poisonPropButton.setOnAction(e -> controller.setCurrentTileType(EditorModel.TileType.POISON));

        Button clearPropButton = new Button("Mode Asset");
        clearPropButton.getStyleClass().add("classique-buttonn");
        clearPropButton.setOnAction(e -> controller.setCurrentTileType(null));

        Button saveButton = new Button("Sauvegarder");
        saveButton.getStyleClass().add("classique-button");
        saveButton.setOnAction(e -> controller.handleSaveMap());

        Button exitButton = new Button("Quitter");
        exitButton.getStyleClass().add("classique-buttonn");
        exitButton.setOnAction(e -> controller.handleReturn());

        HBox box = new HBox(10);
        box.getChildren().addAll(clearButton, eraserButton,
            normalPropButton, obstaclePropButton, slowPropButton, poisonPropButton, clearPropButton,
            saveButton, exitButton);
        box.setPadding(new Insets(10));
        
        return box;
    }
    
    public void updateAssetListView() {
        controller.updateAssetListView(assetListView);
    }
    
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static class AssetEntry {
        private File file;
        private boolean isBack;

        public AssetEntry(File file, boolean isBack) {
            this.file = file;
            this.isBack = isBack;
        }

        public File getFile() { return file; }
        public boolean isBack() { return isBack; }
        public String getName() { 
            return isBack ? "Retour aux dossiers supérieurs" : file.getName(); 
        }
    }
}