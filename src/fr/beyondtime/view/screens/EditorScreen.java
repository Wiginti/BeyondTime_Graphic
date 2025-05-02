package fr.beyondtime.view.screens;

import fr.beyondtime.controller.editor.EditorController;
import fr.beyondtime.util.TranslationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.io.File;

public class EditorScreen extends VBox {
    @SuppressWarnings("unused")
    private EditorController controller;
    private GridPane mapGrid;
    private ListView<AssetEntry> assetListView;
    private HBox toolsBox;
    private TranslationManager translator;

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
    private Button undoButton;

    private Spinner<Integer> rowsSpinner;
    private Spinner<Integer> columnsSpinner;

    public EditorScreen() {
        this.translator = TranslationManager.getInstance();
        setupInitialUI();
    }

    public void setController(EditorController controller) {
        this.controller = controller;
    }

    private void setupInitialUI() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));
        setStyle("""
            -fx-background-color: linear-gradient(to bottom, #1a2a3d, #2a3a4d);
            """);

        Text titleText = new Text(translator.get("editor.title"));
        titleText.setStyle("""
            -fx-fill: #e0e0e0;
            -fx-font-size: 28;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, #000000, 2, 0.3, 0, 1);
            """);

        VBox configBox = new VBox(20);
        configBox.setAlignment(Pos.CENTER);
        configBox.setPadding(new Insets(20));
        configBox.setStyle("""
            -fx-background-color: rgba(26, 42, 61, 0.6);
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 2;
            -fx-min-width: 400;
            """);

        // Configuration des lignes
        HBox rowsBox = new HBox(15);
        rowsBox.setAlignment(Pos.CENTER);
        Text rowsLabel = new Text(translator.get("editor.rows"));
        rowsLabel.setStyle("-fx-fill: #e0e0e0; -fx-font-size: 18;");
        rowsSpinner = new Spinner<>(5, 50, 10);
        styleSpinner(rowsSpinner);
        rowsBox.getChildren().addAll(rowsLabel, rowsSpinner);

        // Configuration des colonnes
        HBox colsBox = new HBox(15);
        colsBox.setAlignment(Pos.CENTER);
        Text colsLabel = new Text(translator.get("editor.columns"));
        colsLabel.setStyle("-fx-fill: #e0e0e0; -fx-font-size: 18;");
        columnsSpinner = new Spinner<>(5, 50, 10);
        styleSpinner(columnsSpinner);
        colsBox.getChildren().addAll(colsLabel, columnsSpinner);

        // Boutons
        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);

        startButton = createStyledButton(translator.get("editor.new"));
        modifyButton = createStyledButton(translator.get("editor.modify.select"));
        returnButton = createStyledButton(translator.get("editor.back"));

        buttonsBox.getChildren().addAll(startButton, returnButton);
        configBox.getChildren().addAll(rowsBox, colsBox, buttonsBox);
        getChildren().addAll(titleText, configBox);
    }

    private void styleSpinner(Spinner<Integer> spinner) {
        spinner.setEditable(true);
        spinner.setPrefWidth(100);
        spinner.getStyleClass().add("spinner-styled");
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("editor-button");
        return button;
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
        toolsBox.setPadding(new Insets(10));
        toolsBox.setAlignment(Pos.CENTER_LEFT);

        undoButton = new Button("Annuler");
        undoButton.setTooltip(new Tooltip("Annuler la dernière action"));
        undoButton.setDisable(true);

        clearButton = new Button("Tout effacer");
        eraserButton = new Button("Gomme");
        normalPropButton = createPropertyButton("Normal", "normal-button");
        obstaclePropButton = createPropertyButton("Obstacle", "obstacle-button");
        slowPropButton = createPropertyButton("Slow", "slow-button");
        poisonPropButton = createPropertyButton("Poison", "poison-button");
        spawnerPropButton = createPropertyButton("Spawner", "spawner-button");
        itemPropButton = new Button("Item");
        clearPropButton = createPropertyButton("Clear", "clear-button");
        saveButton = new Button("Sauvegarder");
        exitButton = new Button("Retour au menu");
        exitPropButton = createPropertyButton("Sortie", "exit-button");
        startPropButton = new Button("Départ");

        itemTypeComboBox = new ComboBox<>();
        setupItemTypeComboBox();
        itemTypeComboBox.setVisible(false);

        toolsBox.getChildren().addAll(
            undoButton,
            clearButton,
            eraserButton,
            normalPropButton,
            obstaclePropButton,
            slowPropButton,
            poisonPropButton,
            spawnerPropButton,
            itemPropButton,
            clearPropButton,
            itemTypeComboBox,
            saveButton,
            exitButton,
            exitPropButton,
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

    public int getRowsValue() {
        return rowsSpinner.getValue();
    }

    public int getColumnsValue() {
        return columnsSpinner.getValue();
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getModifyButton() { return modifyButton; }
    public Button getReturnButton() {
        return returnButton;
    }
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
    public Button getUndoButton() { return undoButton; }

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
