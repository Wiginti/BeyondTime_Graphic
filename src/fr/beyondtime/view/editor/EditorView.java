package fr.beyondtime.view.editor;

import javafx.geometry.Insets;
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

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;

public class EditorView extends VBox {

    private GridPane mapGrid;
    private final int cellSize = 50;   // Taille de chaque cellule en pixels

    // Pour la navigation dans les assets
    private File rootAssets;
    private File currentDirectory;

    // Image actuellement sélectionnée dans la ListView (pour placement par clic)
    private Image selectedAssetImage = null;
    // Mode gomme (effacement)
    private boolean eraserMode = false;

    public EditorView() {
        // Applique la classe "root" pour le background défini dans le CSS
        getStyleClass().add("root");

        // Initialisation du dossier racine des assets
        try {
            rootAssets = new File(getClass().getResource("/fr/beyondtime/assets").toURI());
            currentDirectory = rootAssets;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Création d'un formulaire de configuration intégré
        VBox configPane = new VBox(10);
        configPane.getStyleClass().add("vbox-config");

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

        configPane.getChildren().addAll(configLabel, rowsLabel, rowsField, columnsLabel, columnsField, startButton);
        getChildren().add(configPane);

        startButton.setOnAction(e -> {
            try {
                int rows = Integer.parseInt(rowsField.getText());
                int columns = Integer.parseInt(columnsField.getText());
                // Une fois la configuration validée, on retire le formulaire et on construit l'éditeur complet
                getChildren().remove(configPane);
                buildEditorUI(rows, columns);
            } catch (NumberFormatException ex) {
                System.out.println("Veuillez entrer des nombres valides.");
            }
        });
    }

    /**
     * Construit l'interface de l'éditeur (menu, navigation des assets, grille et boutons outils)
     * avec la taille choisie.
     */
    private void buildEditorUI(int rows, int columns) {
        // Création de la barre de menu
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

        // Création du SplitPane qui séparera la navigation (gauche) et la zone d'édition (droite)
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);

        // Volet gauche : ListView pour naviguer dans les dossiers/assets
        AnchorPane leftPane = new AnchorPane();
        ListView<AssetEntry> assetListView = createAssetListView();
        assetListView.setPrefSize(200, 600);
        leftPane.getChildren().add(assetListView);

        // Volet central : zone d'édition de la carte dans un ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        AnchorPane contentPane = new AnchorPane();
        contentPane.setPrefHeight(800.0);
        contentPane.setPrefWidth(600.0);

        // Création de la grille selon la taille choisie
        mapGrid = createMapGrid(rows, columns);
        contentPane.getChildren().add(mapGrid);
        AnchorPane.setTopAnchor(mapGrid, 10.0);
        AnchorPane.setLeftAnchor(mapGrid, 10.0);
        scrollPane.setContent(contentPane);

        splitPane.getItems().addAll(leftPane, scrollPane);

        // Bouton pour effacer la grille
        Button clearButton = new Button("Effacer la grille");
        clearButton.getStyleClass().add("classique-button");
        clearButton.setOnAction(e -> clearGrid());

        // Bouton Gomme pour activer/désactiver le mode effacement
        Button eraserButton = new Button("Gomme");
        eraserButton.getStyleClass().add("classique-buttonn");
        eraserButton.setOnAction(e -> {
            eraserMode = !eraserMode;
        });

        // Ajout des boutons outils dans un conteneur horizontal
        HBox toolsBox = new HBox(10);
        toolsBox.getChildren().addAll(clearButton, eraserButton);
        toolsBox.setPadding(new Insets(10));

        // Ajout du menu, du SplitPane et des outils à l'éditeur
        getChildren().addAll(menuBar, splitPane, toolsBox);
        setPadding(new Insets(10));
    }

    /**
     * Crée et configure un ListView pour la navigation dans le dossier assets.
     */
    private ListView<AssetEntry> createAssetListView() {
        ListView<AssetEntry> listView = new ListView<>();
        updateAssetListView(listView);

        // Enregistre l'image sélectionnée lors du clic sur un asset (fichier PNG)
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isBack() && newVal.getFile().isFile()) {
                InputStream is = getClass().getResourceAsStream("/fr/beyondtime/assets"
                        + newVal.getFile().getAbsolutePath().split("assets")[1]);
                if (is != null) {
                    selectedAssetImage = new Image(is);
                }
            }
        });

        // Double-clic pour naviguer dans les dossiers
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

        // CellFactory pour afficher le nom du dossier ou l'aperçu d'une image
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

        // (Optionnel) Gestion du drag & drop reste disponible
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

    /**
     * Met à jour le ListView pour afficher le contenu du dossier courant.
     */
    private void updateAssetListView(ListView<AssetEntry> listView) {
        listView.getItems().clear();
        if (!currentDirectory.equals(rootAssets)) {
            listView.getItems().add(new AssetEntry(currentDirectory.getParentFile(), true));
        }
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            // Ajoute d'abord les sous-dossiers
            Arrays.stream(files)
                    .filter(File::isDirectory)
                    .forEach(f -> listView.getItems().add(new AssetEntry(f, false)));
            // Puis les fichiers PNG
            Arrays.stream(files)
                    .filter(f -> f.isFile() && f.getName().toLowerCase().endsWith(".png"))
                    .forEach(f -> listView.getItems().add(new AssetEntry(f, false)));
        }
    }

    /**
     * Crée la grille (GridPane) pour représenter la carte.
     * Chaque cellule est un StackPane qui accepte le clic pour placer l'asset sélectionné
     * ou pour effacer la cellule en mode gomme.
     */
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

                // Placement par clic
                cell.setOnMouseClicked(event -> {
                    if (eraserMode) {
                        clearCell(cell);
                    } else {
                        placeAssetOnCell(cell);
                    }
                });

                // (Optionnel) Drag & drop
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

    /**
     * Place l'asset actuellement sélectionné dans la cellule donnée.
     */
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
        }
    }

    /**
     * Efface le contenu de la cellule (remet à l'état initial).
     */
    private void clearCell(StackPane cell) {
        cell.getChildren().clear();
        Rectangle background = new Rectangle(cellSize, cellSize);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
    }

    /**
     * Efface tous les assets placés sur la grille en réinitialisant chaque cellule.
     */
    private void clearGrid() {
        for (javafx.scene.Node node : mapGrid.getChildren()) {
            if (node instanceof StackPane) {
                clearCell((StackPane) node);
            }
        }
    }

    /**
     * Classe interne représentant une entrée dans la ListView d'assets.
     * Si isBack est true, cela représente l'option "Retour aux dossiers supérieurs".
     */
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