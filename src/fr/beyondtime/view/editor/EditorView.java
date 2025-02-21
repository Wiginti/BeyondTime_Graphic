package fr.beyondtime.view.editor;

import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.Arrays;

public class EditorView extends VBox {

    private GridPane mapGrid;
    private final int rows = 10;       // Nombre de lignes dans la grille
    private final int columns = 15;    // Nombre de colonnes dans la grille
    private final int cellSize = 50;   // Taille de chaque cellule en pixels

    // Liste des noms de fichiers .png (à adapter selon vos assets)
    private final String[] assetFiles = {
            "asset1.png",
            "asset2.png",
            "asset3.png"
            // Ajoutez ici tous les fichiers assets dont vous disposez
    };

    public EditorView() {
        // Initialisation de la taille de la VBox
        setPrefHeight(600.0);
        setPrefWidth(900.0);

        // Barre de menu
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

        // SplitPane pour séparer la liste des assets (gauche) et la grille d'édition (droite)
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);

        // Panneau de gauche : liste déroulante (ListView) des assets
        AnchorPane leftPane = new AnchorPane();
        ListView<String> assetListView = createAssetListView();
        assetListView.setPrefSize(200, 600);
        leftPane.getChildren().add(assetListView);

        // Panneau central : zone d'édition de la carte avec ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        AnchorPane contentPane = new AnchorPane();
        contentPane.setPrefHeight(545.0);
        contentPane.setPrefWidth(430.0);

        // Création de la grille représentant la carte
        mapGrid = createMapGrid();
        contentPane.getChildren().add(mapGrid);
        AnchorPane.setTopAnchor(mapGrid, 10.0);
        AnchorPane.setLeftAnchor(mapGrid, 10.0);

        scrollPane.setContent(contentPane);

        splitPane.getItems().addAll(leftPane, scrollPane);

        // Ajout de la barre de menu et du SplitPane dans la VBox
        getChildren().addAll(menuBar, splitPane);
        setPadding(new Insets(10));
    }

    /**
     * Crée et configure un ListView de String,
     * chaque String correspondant au nom d'un fichier .png.
     * On utilise un CellFactory pour afficher l'image au lieu du simple texte.
     */
    private ListView<String> createAssetListView() {
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(Arrays.asList(assetFiles));

        // CellFactory pour afficher l'image au lieu du texte
        listView.setCellFactory(lv -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            {
                // Taille de l'aperçu dans la liste
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
            }

            @Override
            protected void updateItem(String assetFile, boolean empty) {
                super.updateItem(assetFile, empty);
                if (empty || assetFile == null) {
                    setGraphic(null);
                } else {
                    InputStream is = getClass().getResourceAsStream("/fr/beyondtime/assets/" + assetFile);
                    if (is != null) {
                        Image image = new Image(is);
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Gestionnaire de drag & drop depuis la liste
        listView.setOnDragDetected(event -> {
            // Récupération de l'item sélectionné
            String selectedAsset = listView.getSelectionModel().getSelectedItem();
            if (selectedAsset != null) {
                Dragboard db = listView.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                // On charge l'image pour la mettre dans le clipboard
                InputStream is = getClass().getResourceAsStream("/fr/beyondtime/assets/" + selectedAsset);
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
     * Création de la grille (GridPane) pour représenter la map.
     * Chaque cellule est un StackPane avec un rectangle de fond et accepte le drop d'images.
     */
    private GridPane createMapGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setPadding(new Insets(10));

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(cellSize, cellSize);

                // Rectangle de fond
                Rectangle background = new Rectangle(cellSize, cellSize);
                background.setFill(Color.LIGHTGRAY);
                background.setStroke(Color.BLACK);
                cell.getChildren().add(background);

                // Exemple : au clic, on colorie en vert (optionnel)
                cell.setOnMouseClicked((MouseEvent event) -> {
                    background.setFill(Color.GREEN);
                });

                // Autorise le drag-over si on a une image
                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasImage()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                    event.consume();
                });

                // Drop : on ajoute l'image dans la cellule
                cell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasImage()) {
                        cell.getChildren().clear();
                        // On réinstalle le fond
                        Rectangle newBackground = new Rectangle(cellSize, cellSize);
                        newBackground.setFill(Color.LIGHTGRAY);
                        newBackground.setStroke(Color.BLACK);
                        cell.getChildren().add(newBackground);

                        // Ajout de l'image
                        Image droppedImage = db.getImage();
                        ImageView assetView = new ImageView(droppedImage);
                        assetView.setFitWidth(cellSize);
                        assetView.setFitHeight(cellSize);
                        cell.getChildren().add(assetView);
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
}




/*package fr.beyondtime.view.editor;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class EditorView extends VBox {

    public EditorView() {
        // Initialisation de la taille de la VBox
        setPrefHeight(600.0);
        setPrefWidth(900.0);

        // Création du MenuBar
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

        // Création du SplitPane
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.2505567928730512);

        // Panneau de gauche
        AnchorPane leftPane = new AnchorPane();
        TilePane tilePane = new TilePane();
        tilePane.setLayoutX(-26.0);
        tilePane.setPrefHeight(200.0);
        tilePane.setPrefWidth(200.0);
        leftPane.getChildren().add(tilePane);

        // Panneau central avec ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        AnchorPane contentPane = new AnchorPane();
        contentPane.setMinHeight(-1.0);
        contentPane.setMinWidth(-1.0);
        contentPane.setPrefHeight(545.0);
        contentPane.setPrefWidth(430.0);
        scrollPane.setContent(contentPane);

        splitPane.getItems().addAll(leftPane, scrollPane);

        // Ajouter tous les composants à la VBox
        getChildren().addAll(menuBar, splitPane);
    }
}*/