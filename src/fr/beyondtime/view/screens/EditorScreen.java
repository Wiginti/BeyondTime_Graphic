package fr.beyondtime.view.screens;

import fr.beyondtime.controller.editor.EditorController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.File;

public class EditorScreen extends VBox {
    @SuppressWarnings("unused")
	private EditorController controller;
    private GridPane mapGrid;
    private ListView<AssetEntry> assetListView;
    private HBox toolsBox;

    private TextField rowsField;
    private TextField columnsField;
    private Button startButton;
    private Button modifyButton;
    private Button returnButton;

    private Button clearButton;
    private Button eraserButton;
    private Button normalPropButton;
    private Button obstaclePropButton;
    private Button slowPropButton;
    private Button poisonPropButton;
    private Button spawnerPropButton;
    private Button itemPropButton;
    private Button clearPropButton;
    private Button saveButton;
    private Button exitButton;
    private Button startPropButton;
    private ComboBox<String> itemTypeComboBox;
    private Button exitPropButton;

    public EditorScreen() {
        showConfigPane();
    }

    public void setController(EditorController controller) {
        this.controller = controller;
    }

    public void showConfigPane() {
        VBox configPane = new VBox(10);
        configPane.setAlignment(Pos.CENTER);

        Label configLabel = new Label("Configurer la taille de la grille");
        Label rowsLabel = new Label("Nombre de lignes :");
        rowsField = new TextField("25");
        Label columnsLabel = new Label("Nombre de colonnes :");
        columnsField = new TextField("25");

        startButton = new Button("Créer la carte");
        modifyButton = new Button("Modifier une carte existante");
        returnButton = new Button("Retour");

        configPane.getChildren().addAll(configLabel, rowsLabel, rowsField,
            columnsLabel, columnsField, startButton, modifyButton, returnButton);
        getChildren().add(configPane);
    }

    public void buildEditorUI(GridPane grid, int rows, int columns) {
        getChildren().clear();

        this.mapGrid = grid;

        MenuBar menuBar = createMenuBar();
        SplitPane splitPane = createSplitPane();
        toolsBox = createToolsBox();

        getChildren().addAll(menuBar, splitPane, toolsBox);
        setPadding(new Insets(10));
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Fichier");
        MenuItem closeItem = new MenuItem("Fermer");
        fileMenu.getItems().add(closeItem);
        menuBar.getMenus().addAll(fileMenu);
        return menuBar;
    }

    private SplitPane createSplitPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);

        AnchorPane leftPane = new AnchorPane();
        assetListView = new ListView<>();
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
        HBox toolsBox = new HBox(10);
        toolsBox.setAlignment(Pos.CENTER);
        toolsBox.setPadding(new Insets(10));

        clearButton = new Button("Effacer");
        eraserButton = new Button("Gomme");
        normalPropButton = createPropertyButton("Normal", "normal-button");
        obstaclePropButton = createPropertyButton("Obstacle", "obstacle-button");
        slowPropButton = createPropertyButton("Slow", "slow-button");
        poisonPropButton = createPropertyButton("Poison", "poison-button");
        spawnerPropButton = createPropertyButton("Spawner", "spawner-button");
        itemPropButton = new Button("Item");
        clearPropButton = createPropertyButton("Clear", "clear-button");
        saveButton = new Button("Sauvegarder");
        exitButton = new Button("Quitter");
        exitPropButton = createPropertyButton("Sortie", "exit-button");
        startPropButton = new Button("Départ");

        itemTypeComboBox = new ComboBox<>();
        setupItemTypeComboBox();
        itemTypeComboBox.setVisible(false);

        toolsBox.getChildren().addAll(
            clearButton, eraserButton, normalPropButton,
            obstaclePropButton, slowPropButton, poisonPropButton,
            spawnerPropButton, itemPropButton, clearPropButton,
            itemTypeComboBox, saveButton, exitButton, exitPropButton,
            startPropButton
        );

        return toolsBox;
    }

    private Button createPropertyButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private void setupItemTypeComboBox() {
        itemTypeComboBox.getItems().addAll("Potion", "Épée");
        itemTypeComboBox.setValue("Potion");
    }

    public int getRowsValue() { return Integer.parseInt(rowsField.getText()); }
    public int getColumnsValue() { return Integer.parseInt(columnsField.getText()); }

    public Button getStartButton() { return startButton; }
    public Button getModifyButton() { return modifyButton; }
    public Button getReturnButton() { return returnButton; }
    public Button getClearButton() { return clearButton; }
    public Button getEraserButton() { return eraserButton; }
    public Button getNormalPropButton() { return normalPropButton; }
    public Button getObstaclePropButton() { return obstaclePropButton; }
    public Button getSlowPropButton() { return slowPropButton; }
    public Button getPoisonPropButton() { return poisonPropButton; }
    public Button getClearPropButton() { return clearPropButton; }
    public Button getSaveButton() { return saveButton; }
    public Button getExitButton() { return exitButton; }
    public Button getSpawnerPropButton() { return spawnerPropButton; }
    public Button getItemPropButton() { return itemPropButton; }
    public ComboBox<String> getItemTypeComboBox() { return itemTypeComboBox; }
    public Button getExitPropButton() { return exitPropButton; }
    public Button getStartPropButton() { return startPropButton; }

    public GridPane getMapGrid() { return mapGrid; }
    public ListView<AssetEntry> getAssetListView() { return assetListView; }

    public void updateAssetListView(ListView<AssetEntry> listView) {
        this.assetListView.getItems().clear();
        this.assetListView.getItems().addAll(listView.getItems());
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class AssetEntry {
        private final File file;
        private final boolean isBack;

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
