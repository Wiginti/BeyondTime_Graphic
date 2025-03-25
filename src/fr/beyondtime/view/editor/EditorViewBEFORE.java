package fr.beyondtime.view.editor;

import fr.beyondtime.model.map.Tile;
import fr.beyondtime.util.MapManager;
import fr.beyondtime.view.MenuView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EditorView extends VBox {

    private GridPane mapGrid;
    private final int cellSize = 50;

    private File rootAssets;
    private File currentDirectory;

    private Image selectedAssetImage = null;
    private String selectedAssetPath = null;
    private boolean eraserMode = false;

    // Ajout du type POISON à l'énumération
    private enum TileType { NORMAL, OBSTACLE, SLOW, POISON }
    private TileType currentTileType = null;

    private int gridRows;
    private int gridColumns;

    // Constructeur pour créer une nouvelle carte (configuration de la grille)
    public EditorView() {
        try {
            rootAssets = new File(getClass().getResource("/fr/beyondtime/assets").toURI());
            currentDirectory = rootAssets;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

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

        // Bouton de modification d'une carte existante
        Button modifyButton = new Button("Modifier une carte existante");
        modifyButton.getStyleClass().add("classique-button");

        Button returnButton = new Button("Retour");
        returnButton.getStyleClass().add("classique-buttonn");

        configPane.getChildren().addAll(configLabel, rowsLabel, rowsField, columnsLabel, columnsField, startButton, modifyButton, returnButton);
        getChildren().add(configPane);

        returnButton.setOnAction(e -> {
            Stage stage = (Stage)this.getScene().getWindow();
            MenuView.showNiveauScene(stage);
        });

        startButton.setOnAction(e -> {
            try {
                int rows = Integer.parseInt(rowsField.getText());
                int columns = Integer.parseInt(columnsField.getText());
                gridRows = rows;
                gridColumns = columns;
                getChildren().remove(configPane);
                // Supprimer le CSS pour obtenir l'affichage de modification
                if (getScene() != null) {
                    getScene().getStylesheets().clear();
                }
                mapGrid = createMapGrid(rows, columns);
                buildEditorUI();
            } catch (NumberFormatException ex) {
                System.out.println("Veuillez entrer des nombres valides.");
            }
        });

        modifyButton.setOnAction(e -> {
            File saveDir = new File("saved_maps");
            File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".map"));
            if (files == null || files.length == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Modification");
                alert.setHeaderText(null);
                alert.setContentText("Aucune carte existante à modifier.");
                alert.showAndWait();
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
            if (result.isPresent()) {
                String selectedFileName = result.get();
                File selectedFile = null;
                for (File f : files) {
                    if (f.getName().equals(selectedFileName)) {
                        selectedFile = f;
                        break;
                    }
                }
                if (selectedFile != null) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                        String firstLine = reader.readLine();
                        if (firstLine != null) {
                            String[] dims = firstLine.split(",");
                            int rows = Integer.parseInt(dims[0].trim());
                            int columns = Integer.parseInt(dims[1].trim());
                            gridRows = rows;
                            gridColumns = columns;
                            GridPane loadedGrid = MapManager.loadMapFromFile(selectedFile);
                            if (loadedGrid != null) {
                                EditorView newEditor = new EditorView(loadedGrid, rows, columns);
                                Stage stage = (Stage)this.getScene().getWindow();
                                stage.setScene(new Scene(newEditor, 800, 600));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    // Constructeur pour éditer une carte déjà existante
    public EditorView(GridPane preloadedGrid, int rows, int columns) {
        try {
            rootAssets = new File(getClass().getResource("/fr/beyondtime/assets").toURI());
            currentDirectory = rootAssets;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.mapGrid = preloadedGrid;
        this.gridRows = rows;
        this.gridColumns = columns;
        buildEditorUI();
    }

    // Construction de l'interface de l'éditeur (menu, zone d'édition et outils)
    private void buildEditorUI() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem closeItem = new MenuItem("Close");
        fileMenu.getItems().add(closeItem);
        Menu editMenu = new Menu("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        editMenu.getItems().add(deleteItem);
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        helpMenu.getItems().add(aboutItem);
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);

        AnchorPane leftPane = new AnchorPane();
        ListView<AssetEntry> assetListView = createAssetListView();
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

        Button clearButton = new Button("Effacer la grille");
        clearButton.getStyleClass().add("classique-button");
        clearButton.setOnAction(e -> clearGrid());

        Button eraserButton = new Button("Gomme");
        eraserButton.getStyleClass().add("classique-buttonn");
        eraserButton.setOnAction(e -> eraserMode = !eraserMode);

        // Boutons pour sélectionner une propriété à appliquer sur les cases
        Button normalPropButton = new Button("Normal");
        normalPropButton.getStyleClass().add("classique-button");
        normalPropButton.setOnAction(e -> currentTileType = TileType.NORMAL);

        Button obstaclePropButton = new Button("Obstacle");
        obstaclePropButton.getStyleClass().add("classique-button");
        obstaclePropButton.setOnAction(e -> currentTileType = TileType.OBSTACLE);

        Button slowPropButton = new Button("Ralentissement");
        slowPropButton.getStyleClass().add("classique-button");
        slowPropButton.setOnAction(e -> currentTileType = TileType.SLOW);

        // Nouveau bouton pour le mode Poison
        Button poisonPropButton = new Button("Poison");
        poisonPropButton.getStyleClass().add("classique-button");
        poisonPropButton.setOnAction(e -> currentTileType = TileType.POISON);

        // Bouton pour revenir en mode asset (désactiver le mode propriété)
        Button clearPropButton = new Button("Mode Asset");
        clearPropButton.getStyleClass().add("classique-buttonn");
        clearPropButton.setOnAction(e -> currentTileType = null);

        Button saveButton = new Button("Sauvegarder");
        saveButton.getStyleClass().add("classique-button");
        saveButton.setOnAction(e -> {
            List<String> choices = List.of("Niveau 1 - Préhistoire", "Niveau 2 - Égypte Antique", "Niveau 3 - 2nde Guerre Mondiale");
            ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Sauvegarde de la Map");
            dialog.setHeaderText("Choisissez le niveau classique de sauvegarde");
            dialog.setContentText("Niveau :");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(chosenLevel -> {
                MapManager.saveMap(mapGrid, gridRows, gridColumns, chosenLevel);
            });
        });

        Button exitButton = new Button("Quitter");
        exitButton.getStyleClass().add("classique-buttonn");
        exitButton.setOnAction(e -> {
            Stage stage = (Stage)this.getScene().getWindow();
            MenuView.showNiveauScene(stage);
        });

        HBox toolsBox = new HBox(10);
        toolsBox.getChildren().addAll(clearButton, eraserButton,
                normalPropButton, obstaclePropButton, slowPropButton, poisonPropButton, clearPropButton,
                saveButton, exitButton);
        toolsBox.setPadding(new Insets(10));

        getChildren().addAll(menuBar, splitPane, toolsBox);
        setPadding(new Insets(10));
    }

    private ListView<AssetEntry> createAssetListView() {
        ListView<AssetEntry> listView = new ListView<>();
        updateAssetListView(listView);

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isBack() && newVal.getFile().isFile()) {
                InputStream is = getClass().getResourceAsStream("/fr/beyondtime/assets"
                        + newVal.getFile().getAbsolutePath().split("assets")[1]);
                if (is != null) {
                    selectedAssetImage = new Image(is);
                    selectedAssetPath = "/fr/beyondtime/assets" + newVal.getFile().getAbsolutePath().split("assets")[1];
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                AssetEntry selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    if (selected.isBack()) {
                        currentDirectory = currentDirectory.getParentFile();
                        updateAssetListView(listView);
                    } else if (selected.getFile().isDirectory()) {
                        currentDirectory = selected.getFile();
                        updateAssetListView(listView);
                    }
                }
            }
        });

        listView.setCellFactory(lv -> new ListCell<AssetEntry>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
            }
            @Override
            protected void updateItem(AssetEntry item, boolean empty) {
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
                    }
                }
            }
        });

        listView.setOnDragDetected(event -> {
            AssetEntry selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.isBack() && selected.getFile().isFile()) {
                Dragboard db = listView.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                InputStream is = getClass().getResourceAsStream("/fr/beyondtime/assets"
                        + selected.getFile().getAbsolutePath().split("assets")[1]);
                if (is != null) {
                    Image image = new Image(is);
                    content.putImage(image);
                    db.setContent(content);
                }
            }
            event.consume();
        });

        return listView;
    }

    private void updateAssetListView(ListView<AssetEntry> listView) {
        listView.getItems().clear();
        if (!currentDirectory.equals(rootAssets)) {
            listView.getItems().add(new AssetEntry(currentDirectory.getParentFile(), true));
        }
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            Arrays.stream(files)
                    .filter(File::isDirectory)
                    .forEach(f -> listView.getItems().add(new AssetEntry(f, false)));
            Arrays.stream(files)
                    .filter(f -> f.isFile() && f.getName().toLowerCase().endsWith(".png"))
                    .forEach(f -> listView.getItems().add(new AssetEntry(f, false)));
        }
    }

    private GridPane createMapGrid(int rows, int columns) {
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(0));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(cellSize, cellSize);
                Rectangle background = new Rectangle(cellSize, cellSize);
                background.setFill(Color.LIGHTGRAY);
                background.setStroke(Color.BLACK);
                cell.getChildren().add(background);
                // Par défaut, la cellule est en mode Normal (damage = 0)
                cell.getProperties().put("tile", new Tile(true, 1.0, 0));
                cell.setOnMouseClicked(event -> {
                    if (currentTileType != null) {
                        switch (currentTileType) {
                            case NORMAL:
                                setCellAsNormal(cell);
                                break;
                            case OBSTACLE:
                                setCellAsObstacle(cell);
                                break;
                            case SLOW:
                                setCellAsSlowZone(cell);
                                break;
                            case POISON:
                                setCellAsPoison(cell);
                                break;
                        }
                    } else {
                        if (eraserMode) {
                            clearCell(cell);
                        } else {
                            placeAssetOnCell(cell);
                        }
                    }
                });
                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasImage()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                    event.consume();
                });
                cell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasImage()) {
                        placeAssetOnCell(cell);
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });
                grid.add(cell, col, row);
            }
        }
        return grid;
    }

    private void setCellAsNormal(StackPane cell) {
        cell.getProperties().put("tile", new Tile(true, 1.0, 0));
        updateCellBackground(cell, Color.LIGHTGRAY);
    }

    private void setCellAsObstacle(StackPane cell) {
        cell.getProperties().put("tile", new Tile(false, 0, 0));
        updateCellBackground(cell, Color.RED);
    }

    private void setCellAsSlowZone(StackPane cell) {
        cell.getProperties().put("tile", new Tile(true, 0.5, 0));
        updateCellBackground(cell, Color.ORANGE);
    }

    // Nouvelle méthode pour définir une cellule comme toxique/poison
    private void setCellAsPoison(StackPane cell) {
        // Ici, la cellule reste franchissable avec un ralentissement normal (1.0)
        // mais inflige 10 points de dégâts lorsque le joueur la traverse
        cell.getProperties().put("tile", new Tile(true, 1.0, 10));
        updateCellBackground(cell, Color.PURPLE);
    }

    private void updateCellBackground(StackPane cell, Color color) {
        for (javafx.scene.Node node : cell.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setFill(color);
            }
        }
    }

    private void placeAssetOnCell(StackPane cell) {
        if (selectedAssetImage != null) {
            cell.getChildren().clear();
            Rectangle newBackground = new Rectangle(cellSize, cellSize);
            newBackground.setFill(Color.LIGHTGRAY);
            newBackground.setStroke(Color.BLACK);
            cell.getChildren().add(newBackground);
            ImageView assetView = new ImageView(selectedAssetImage);
            assetView.setFitWidth(cellSize);
            assetView.setFitHeight(cellSize);
            cell.getChildren().add(assetView);
            cell.setUserData(selectedAssetPath);
        }
    }

    private void clearCell(StackPane cell) {
        cell.getChildren().clear();
        Rectangle background = new Rectangle(cellSize, cellSize);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        cell.setUserData(null);
    }

    private void clearGrid() {
        for (javafx.scene.Node node : mapGrid.getChildren()) {
            if (node instanceof StackPane) {
                clearCell((StackPane) node);
            }
        }
    }

    private static class AssetEntry {
        private File file;
        private boolean isBack;

        public AssetEntry(File file, boolean isBack) {
            this.file = file;
            this.isBack = isBack;
        }

        public File getFile() {
            return file;
        }

        public boolean isBack() {
            return isBack;
        }

        public String getName() {
            return isBack ? "Retour aux dossiers supérieurs" : file.getName();
        }
    }
}