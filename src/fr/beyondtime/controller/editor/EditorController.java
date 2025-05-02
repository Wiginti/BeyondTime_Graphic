package fr.beyondtime.controller.editor;

import fr.beyondtime.model.editor.EditorModel;
import fr.beyondtime.model.editor.EditorModel.TileType;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.util.MapManager;
import fr.beyondtime.util.TranslationManager;
import fr.beyondtime.view.screens.EditorScreen;
import fr.beyondtime.view.screens.MenuScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.*;
import java.util.*;

/**
 * Contrôleur gérant l'éditeur de niveaux.
 * Permet de créer ou modifier des cartes de jeu.
 */
public class EditorController {

    /** Modèle gérant les données de l'éditeur de cartes. */
    private final EditorModel model;
    /** Vue de l'éditeur pour l'affichage du contenu. */
    private final EditorScreen view;
    /** Fenêtre principale de l'application. */
    private final Stage stage;
    /** Dossier actuel des ressources (assets) de l'éditeur. */
    private File currentAssetFolder = new File("assets");
    /** Fichier de ressource actuellement sélectionné (image ou autre). */
    private File selectedAsset = null;
    /** Fichier de la carte actuellement éditée (null si aucune). */
    private File currentMapFile = null;  // Pour suivre le fichier de la carte en cours d'édition
    /** Gestionnaire de traduction pour l'internationalisation. */
    private final TranslationManager translator;

    public EditorController(Stage stage) {
        this.stage = stage;
        this.model = new EditorModel();
        this.view = new EditorScreen();
        this.translator = TranslationManager.getInstance();

        // Création de la fenêtre de choix
        Stage choiceStage = new Stage();
        choiceStage.initModality(Modality.APPLICATION_MODAL);
        choiceStage.initOwner(stage);
        choiceStage.setTitle(translator.get("menu.editor"));

        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #1a2a3d, #2a3a4d);
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 2;
            """);

        Text titleText = new Text(translator.get("menu.editor"));
        titleText.setStyle("""
            -fx-fill: #e0e0e0;
            -fx-font-size: 28;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, #000000, 2, 0.3, 0, 1);
            """);

        VBox buttonsBox = new VBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(20, 0, 20, 0));
        buttonsBox.setStyle("-fx-background-color: rgba(26, 42, 61, 0.6); -fx-background-radius: 5;");
        buttonsBox.setMinWidth(400);

        Button newMapButton = createEditorButton(translator.get("editor.new"));
        Button modifyMapButton = createEditorButton(translator.get("editor.modify"));
        Button backButton = createEditorButton(translator.get("editor.back"));

        newMapButton.setOnAction(e -> {
            choiceStage.close();
            showNewMapDialog();
        });

        modifyMapButton.setOnAction(e -> {
            choiceStage.close();
            showModifyMapDialog();
        });

        backButton.setOnAction(e -> {
            choiceStage.close();
            stage.setScene(new MenuScreen(stage).getMenuScene());
        });

        buttonsBox.getChildren().addAll(newMapButton, modifyMapButton, backButton);
        layout.getChildren().addAll(titleText, buttonsBox);

        Scene scene = new Scene(layout, 500, 600);
        scene.setFill(null);
        choiceStage.setScene(scene);
        choiceStage.show();

        // Setup de l'éditeur
        this.view.setController(this);
        setupInitialConfigEventHandlers();
    }

    /** Affiche une fenêtre pour créer une nouvelle carte. */
    private void showNewMapDialog() {
        stage.setTitle(translator.get("editor.new.title"));
        this.stage.setScene(new Scene(view));
    }

    /** Affiche une fenêtre pour sélectionner et modifier une carte existante. */
    private void showModifyMapDialog() {
        File saveDir = new File("saved_map");
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            view.showAlert("Modification", "Dossier de sauvegarde introuvable.");
            return;
        }

        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".map"));
        if (files == null || files.length == 0) {
            view.showAlert("Modification", "Aucune carte existante à modifier.");
            return;
        }

        // Création de la fenêtre de sélection de map
        Stage mapSelectStage = new Stage();
        mapSelectStage.initModality(Modality.APPLICATION_MODAL);
        mapSelectStage.initOwner(stage);
        mapSelectStage.setTitle(translator.get("editor.modify"));

        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #1a2a3d, #2a3a4d);
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 2;
            """);

        Text titleText = new Text(translator.get("editor.modify.select"));
        titleText.setStyle("""
            -fx-fill: #e0e0e0;
            -fx-font-size: 28;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, #000000, 2, 0.3, 0, 1);
            """);

        VBox mapsBox = new VBox(20);
        mapsBox.setAlignment(Pos.CENTER);
        mapsBox.setPadding(new Insets(20, 0, 20, 0));
        mapsBox.setStyle("-fx-background-color: rgba(26, 42, 61, 0.6); -fx-background-radius: 5;");
        mapsBox.setMinWidth(400);

        for (File file : files) {
            String mapName = file.getName();
            Button mapButton = createEditorButton(mapName);
            final File selectedMap = file;
            
            mapButton.setOnAction(e -> {
                currentMapFile = selectedMap;
                GridPane loadedGrid = MapManager.loadMapFromFile(selectedMap);
                if (loadedGrid != null) {
                    mapSelectStage.close();
                    
                    model.setMapGrid(loadedGrid);
                    model.setGridRows((int) loadedGrid.getRowCount());
                    model.setGridColumns((int) loadedGrid.getColumnCount());
                    
                    // Ajouter les overlays pour les effets spéciaux
                    loadExistingGrid(loadedGrid);
                    
                    view.buildEditorUI(loadedGrid, model.getGridRows(), model.getGridColumns());
                    setupEditorUIEventHandlers();
                    loadAssetList();
                    
                    // Afficher l'éditeur
                    stage.setScene(new Scene(view));
                }
            });
            
            mapsBox.getChildren().add(mapButton);
        }

        Button closeButton = new Button(translator.get("menu.close"));
        closeButton.setStyle("""
            -fx-background-color: #2a3a4d;
            -fx-text-fill: #e0e0e0;
            -fx-font-size: 16;
            -fx-padding: 10 20;
            -fx-background-radius: 5;
            -fx-border-radius: 5;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 1;
            -fx-cursor: hand;
            -fx-min-width: 120;
            """);

        final String baseCloseStyle = closeButton.getStyle();
        final String hoverCloseStyle = baseCloseStyle + "-fx-background-color: #3a4a5d;";

        closeButton.setOnMouseEntered(e -> closeButton.setStyle(hoverCloseStyle));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(baseCloseStyle));
        closeButton.setOnAction(e -> mapSelectStage.close());

        layout.getChildren().addAll(titleText, mapsBox, closeButton);

        Scene scene = new Scene(layout, 500, 600);
        scene.setFill(null);
        mapSelectStage.setScene(scene);
        mapSelectStage.show();
    }

    /** Crée et configure un bouton pour l'éditeur. */
    private Button createEditorButton(String text) {
        final Button button = new Button(text);
        
        final String baseStyle = """
            -fx-background-color: #2a3a4d;
            -fx-text-fill: #e0e0e0;
            -fx-font-size: 20;
            -fx-padding: 15 30;
            -fx-min-width: 350;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-width: 2;
            -fx-border-color: #4a5a6d;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, #000000, 8, 0.4, 0, 0);
            """;

        button.setStyle(baseStyle);

        final String hoverStyle = baseStyle + """
            -fx-scale-x: 1.03;
            -fx-scale-y: 1.03;
            -fx-effect: dropshadow(gaussian, #000000, 15, 0.6, 0, 0);
            -fx-background-color: #3a4a5d;
            """;

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    /** Configure les gestionnaires d'événements initiaux avant création de la carte. */
    private void setupInitialConfigEventHandlers() {
        view.getStartButton().setOnAction(e -> handleCreateMap(view.getRowsValue(), view.getColumnsValue()));
        view.getModifyButton().setOnAction(e -> handleModifyMap());
        view.getReturnButton().setOnAction(e -> handleReturn());
    }

    /** Crée une nouvelle carte avec les dimensions spécifiées. */
    public void handleCreateMap(int rows, int columns) {
        model.setGridRows(rows);
        model.setGridColumns(columns);
        GridPane grid = createMapGrid(rows, columns);
        model.setMapGrid(grid);
        view.buildEditorUI(grid, rows, columns);
        setupEditorUIEventHandlers();
        loadAssetList();
    }

    /** Retourne au menu principal depuis l'éditeur. */
    public void handleReturn() {
        MenuScreen menuScreen = new MenuScreen(stage);
        stage.setScene(menuScreen.getMenuScene());
    }

    /** Configure les gestionnaires d'événements de l'interface de l'éditeur. */
    private void setupEditorUIEventHandlers() {
        view.getClearButton().setOnAction(e -> handleClearGrid());
        view.getEraserButton().setOnAction(e -> toggleEraserMode());
        view.getNormalPropButton().setOnAction(e -> setCurrentTileType(TileType.NORMAL));
        view.getObstaclePropButton().setOnAction(e -> setCurrentTileType(TileType.OBSTACLE));
        view.getSlowPropButton().setOnAction(e -> setCurrentTileType(TileType.SLOW));
        view.getPoisonPropButton().setOnAction(e -> setCurrentTileType(TileType.POISON));
        view.getSpawnerPropButton().setOnAction(e -> setCurrentTileType(TileType.SPAWNER));
        view.getExitPropButton().setOnAction(e -> setCurrentTileType(TileType.EXIT));
        view.getStartPropButton().setOnAction(e -> setCurrentTileType(TileType.START));
        view.getSaveButton().setOnAction(e -> handleSaveMap());
        view.getExitButton().setOnAction(e -> handleReturn());
        
        // Gestionnaire pour le bouton d'annulation
        view.getUndoButton().setOnAction(e -> handleUndo());
    }

    /** Active ou désactive le mode 'gomme' (effaceur) dans l'éditeur. */
    public void toggleEraserMode() {
        model.setEraserMode(!model.isEraserMode());
    }

    /** Définit le type de tuile actif pour la peinture dans l'éditeur. */
    public void setCurrentTileType(TileType type) {
        model.setCurrentTileType(type);
        if (type != null) model.setEraserMode(false);
    }

    /** Efface complètement la grille de l'éditeur (toutes les cellules). */
    private void handleClearGrid() {
        // Sauvegarder l'état de toutes les cellules avant de les effacer
        for (Node node : model.getMapGrid().getChildren()) {
            if (node instanceof StackPane cell) {
                model.saveStateBeforeEdit(cell);
                clearCell(cell);
            }
        }
        // Activer le bouton d'annulation car nous avons une action à annuler
        view.getUndoButton().setDisable(false);
    }
    
    /** Efface une cellule spécifique (retire toutes ses couches de contenu). */
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

    /** Gère le clic sur une cellule : peinture ou effacement selon le mode. */
    private void handleCellClick(StackPane cell) {
        // Sauvegarder l'état avant modification
        model.saveStateBeforeEdit(cell);
        
        if (model.isEraserMode()) {
            clearCell(cell);
        } else {
            updateCell(cell);
        }
        
        // Activer le bouton d'annulation car nous avons une action à annuler
        view.getUndoButton().setDisable(false);
    }

    /** Annule la dernière action de peinture. */
    private void handleUndo() {
        if (model.undoLastAction()) {
            // Désactiver le bouton si nous n'avons plus d'actions à annuler
            view.getUndoButton().setDisable(model.getActionHistorySize() == 0);
        }
    }

    /** Met à jour l'affichage d'une cellule en fonction du modèle. */
    private void updateCell(StackPane cell) {
        if (model.getCurrentTileType() == null) {
            clearCell(cell);
            return;
        }

        // Supprimer l'ancien point de départ si on en place un nouveau
        if (model.getCurrentTileType() == TileType.START) {
            for (Node node : model.getMapGrid().getChildren()) {
                if (node instanceof StackPane existingCell) {
                    Object tileObj = existingCell.getProperties().get("tile");
                    if (tileObj instanceof Tile && ((Tile) tileObj).isStart()) {
                        clearCell(existingCell);
                    }
                }
            }
        }
        // Effacer la cellule pour la remettre à l'état de base
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
        	// Appliquer l'overlay coloré selon le type de tuile
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
                case EXIT -> {
                    model.setCellAsExit(cell);
                    cell.getProperties().put("isExit", true);
                }
                case START -> {
                    model.setCellAsStart(cell);
                    cell.getProperties().put("isStart", true);
                }
            }
        }
    }

    /** Retourne la couleur de surbrillance pour un type de tuile donné. */
    private Color getOverlayColor(TileType type) {
        return switch (type) {
            case NORMAL -> Color.TRANSPARENT;
            case OBSTACLE -> Color.BLACK;
            case SLOW -> Color.BLUE;
            case POISON -> Color.PURPLE;
            case SPAWNER -> Color.RED;
            case EXIT -> Color.GREEN;
            case START -> Color.YELLOW;
        };
    }

    /** Crée une grille de carte visuelle avec les dimensions données. */
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

    /** Enregistre la carte actuelle dans un fichier. */
    public void handleSaveMap() {
        if (model.getMapGrid() == null) {
            view.showAlert("Erreur", "Aucune carte à sauvegarder.");
            return;
        }

        if (currentMapFile != null) {
            // Si on modifie une carte existante, on la met à jour
            MapManager.saveMap(model.getMapGrid(), model.getGridRows(), model.getGridColumns(), currentMapFile);
            view.showAlert("Succès", "La carte a été mise à jour avec succès.");
        } else {
            // Nouvelle carte
            List<String> choices = List.of("Préhistoire", "Égypte Antique", "2nde Guerre Mondiale", "Custom Map");
            ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Sauvegarde de la Map");
            dialog.setHeaderText("Choisissez le niveau classique ou nommez votre carte");
            dialog.setContentText("Niveau/Nom :");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(chosenLevel -> {
                currentMapFile = MapManager.saveMap(model.getMapGrid(), model.getGridRows(), model.getGridColumns(), chosenLevel);
                if (currentMapFile != null) {
                    view.showAlert("Succès", "Nouvelle carte sauvegardée avec succès.");
                }
            });
        }
    }

    /** Lance la procédure de modification d'une carte existante. */
    public void handleModifyMap() {
        File saveDir = new File("saved_map");
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            view.showAlert("Modification", "Dossier de sauvegarde introuvable.");
            return;
        }

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
                currentMapFile = file;  // Sauvegarder la référence au fichier en cours d'édition
                GridPane loadedGrid = MapManager.loadMapFromFile(file);
                if (loadedGrid != null) {
                    model.setMapGrid(loadedGrid);
                    model.setGridRows((int) loadedGrid.getRowCount());
                    model.setGridColumns((int) loadedGrid.getColumnCount());
                    
                    // Ajouter les overlays pour les effets spéciaux
                    loadExistingGrid(loadedGrid);
                    
                    view.buildEditorUI(loadedGrid, model.getGridRows(), model.getGridColumns());
                    setupEditorUIEventHandlers();
                    loadAssetList();
                }
            }
        });
    }
    
    /** Charge et affiche la liste des ressources disponibles pour l'éditeur. */
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

    /** Vérifie si le fichier spécifié est une image. */
    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    public void loadExistingGrid(GridPane grid) {
        // Parcourir toutes les cellules et ajouter les overlays appropriés
        for (Node node : grid.getChildren()) {
            if (node instanceof StackPane cell) {
                Object tileObj = cell.getProperties().get("tile");
                if (tileObj instanceof Tile tile) {
                    // Ajouter les overlays en fonction des propriétés de la tuile
                    if (!tile.isPassable()) {
                        addOverlay(cell, Color.BLACK);
                    } else if (tile.getSlowdownFactor() < 1.0) {
                        addOverlay(cell, Color.BLUE);
                    } else if (tile.getDamage() > 0) {
                        addOverlay(cell, Color.PURPLE);
                    } else if (tile.isExit()) {
                        addOverlay(cell, Color.GREEN);
                    } else if (tile.isStart()) {
                        addOverlay(cell, Color.YELLOW);
                    }
                }
                
                // Vérifier les spawners
                if (cell.getProperties().get("isSpawner") != null) {
                    addOverlay(cell, Color.RED);
                }
                
                // Ajouter le gestionnaire d'événements pour l'édition
                cell.setOnMouseClicked(event -> handleCellClick(cell));
            }
        }
    }

    /** Ajoute une couche colorée semi-transparente sur une cellule (overlay). */
    private void addOverlay(StackPane cell, Color color) {
        Rectangle overlay = new Rectangle(model.getCellSize(), model.getCellSize());
        overlay.setFill(color);
        overlay.setOpacity(0.4);
        cell.getChildren().add(overlay);
    }
}
