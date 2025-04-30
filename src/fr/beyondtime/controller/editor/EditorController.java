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

/**
 * Controller for the map editor functionality.
 * Handles user interactions with the editor screen, manages the editor model,
 * and coordinates actions like map creation, modification, saving, and asset placement.
 */
public class EditorController {
    private EditorModel model;
    private EditorScreen view;
    private Stage stage;
    
    /**
     * Constructs an EditorController.
     * Initializes the model and view, sets up the initial configuration screen,
     * and attaches event handlers for the configuration options.
     *
     * @param stage The primary stage for the editor window.
     */
    public EditorController(Stage stage) {
        this.stage = stage;
        this.model = new EditorModel();
        this.view = new EditorScreen();
        this.view.setController(this);
        
        // Set the scene with the EditorScreen (showing config pane)
        Scene scene = new Scene(view);
        // Apply CSS if needed
        // scene.getStylesheets().add(getClass().getResource("/path/to/editor.css").toExternalForm());
        stage.setScene(scene);
        
        // Setup handlers ONLY for the initial config buttons
        setupInitialConfigEventHandlers();
    }
    
    /**
     * Sets up event handlers for the initial configuration buttons (Create, Modify, Return).
     */
    private void setupInitialConfigEventHandlers() {
        Button startButton = view.getStartButton();
        Button modifyButton = view.getModifyButton();
        Button returnButton = view.getReturnButton();

        startButton.setOnAction(e -> {
            int rows = view.getRowsValue();
            int columns = view.getColumnsValue();
            handleCreateMap(rows, columns);
        });

        modifyButton.setOnAction(e -> handleModifyMap());
        returnButton.setOnAction(e -> handleReturn());
    }
    
    /**
     * Sets up event handlers for the main editor UI components (toolbar buttons, asset list).
     * This is called after the grid is created or loaded.
     */
    private void setupEditorUIEventHandlers() {
        // Get buttons from the fully built editor UI
        Button clearButton = view.getClearButton();
        Button eraserButton = view.getEraserButton();
        Button normalPropButton = view.getNormalPropButton();
        Button obstaclePropButton = view.getObstaclePropButton();
        Button slowPropButton = view.getSlowPropButton();
        Button poisonPropButton = view.getPoisonPropButton();
        Button clearPropButton = view.getClearPropButton(); // Assuming this is the 'Mode Asset' button
        Button saveButton = view.getSaveButton();
        Button exitButton = view.getExitButton(); // Assuming this exits the editor

        // Assign actions to these buttons
        clearButton.setOnAction(e -> handleClearGrid());
        eraserButton.setOnAction(e -> toggleEraserMode());
        normalPropButton.setOnAction(e -> setCurrentTileType(TileType.NORMAL));
        obstaclePropButton.setOnAction(e -> setCurrentTileType(TileType.OBSTACLE));
        slowPropButton.setOnAction(e -> setCurrentTileType(TileType.SLOW));
        poisonPropButton.setOnAction(e -> setCurrentTileType(TileType.POISON));
        clearPropButton.setOnAction(e -> setCurrentTileType(null)); // Sets to Asset Mode
        saveButton.setOnAction(e -> handleSaveMap());
        exitButton.setOnAction(e -> handleReturn()); // Exit button likely returns to menu
        
        // Setup asset list view handlers
        ListView<EditorScreen.AssetEntry> assetListView = view.getAssetListView();
        setupAssetListViewHandlers(assetListView);
    }
    
    /**
     * Handles the creation of a new map grid based on user-specified dimensions.
     * Updates the model, creates the visual grid, rebuilds the editor UI,
     * and sets up event handlers for the editor tools.
     *
     * @param rows    The number of rows for the new grid.
     * @param columns The number of columns for the new grid.
     */
    public void handleCreateMap(int rows, int columns) {
        model.setGridRows(rows);
        model.setGridColumns(columns);
        GridPane grid = createMapGrid(rows, columns);
        model.setMapGrid(grid); // Store the grid in the model
        view.buildEditorUI(grid, rows, columns); // Rebuild view content
        setupEditorUIEventHandlers(); // Now setup handlers for the full editor UI
    }
    
    /**
     * Handles the process of modifying an existing map file.
     * Presents a dialog to choose a saved map, loads it using MapManager,
     * updates the model and view, and sets up editor event handlers.
     */
    public void handleModifyMap() {
        File saveDir = new File("src/fr/beyondtime/saved_maps"); // Use relative path
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
                            model.setGridRows(rows);
                            model.setGridColumns(columns);
                            model.setMapGrid(loadedGrid);
                            view.buildEditorUI(loadedGrid, rows, columns); // Rebuild view content
                            setupEditorUIEventHandlers(); // Setup handlers for the full editor UI
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    view.showAlert("Erreur", "Impossible de lire les dimensions de la carte.");
                    return;
                }
            }
        });
    }
    
    /**
     * Handles returning to the main menu screen.
     * Creates a new MenuScreen instance and scene to avoid node reuse issues.
     */
    public void handleReturn() {
        // Create a new MenuScreen instance
        MenuScreen menuScreen = new MenuScreen(stage);
        Scene menuScene = menuScreen.getMenuScene();
        stage.setScene(menuScene);
    }
    
    /**
     * Clears the content (assets and properties) of all cells in the current map grid.
     * Resets each cell to its default state.
     */
    public void handleClearGrid() {
        if (model.getMapGrid() == null) return;
        for (javafx.scene.Node node : model.getMapGrid().getChildren()) {
            if (node instanceof StackPane) {
                clearCell((StackPane) node);
            }
        }
    }
    
    /**
     * Toggles the eraser mode on or off in the model.
     * Disables asset/tile type selection when eraser is active.
     */
    public void toggleEraserMode() {
        model.setEraserMode(!model.isEraserMode());
        // TODO: Add visual feedback for eraser mode toggle if needed
    }
    
    /**
     * Sets the currently selected tile type for placement.
     * Disables eraser mode when a tile type is selected.
     *
     * @param type The TileType to set, or null to switch to asset placement mode.
     */
    public void setCurrentTileType(EditorModel.TileType type) {
        model.setCurrentTileType(type);
        if (type != null) {
            model.setEraserMode(false); // Disable eraser when selecting a tile type
        }
         // TODO: Add visual feedback for selected tile type if needed
    }
    
    /**
     * Handles saving the current map grid to a file.
     * Prompts the user to choose a level name or provide a custom name,
     * then uses MapManager to perform the save operation.
     */
    public void handleSaveMap() {
         if (model.getMapGrid() == null) {
             view.showAlert("Erreur", "Aucune carte à sauvegarder.");
             return;
         }
        List<String> choices = List.of(
            "Niveau 1 - Préhistoire",
            "Niveau 2 - Égypte Antique",
            "Niveau 3 - 2nde Guerre Mondiale",
            "Custom Map" // Option to save as a generic custom map
        );

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Sauvegarde de la Map");
        dialog.setHeaderText("Choisissez le niveau classique ou nommez votre carte");
        dialog.setContentText("Niveau/Nom :");

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
    
    /**
     * Sets up event handlers for the asset list view, including selection changes,
     * double-click navigation, and drag-and-drop functionality.
     *
     * @param listView The ListView displaying assets.
     */
    private void setupAssetListViewHandlers(ListView<EditorScreen.AssetEntry> listView) {
         // Listener for selection changes
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isBack() && newVal.getFile().isFile()) {
                 handleAssetSelection(newVal.getFile());
            }
        });

        // Listener for double-clicks (navigation)
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                EditorScreen.AssetEntry selected = listView.getSelectionModel().getSelectedItem();
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

         // Setup Drag and Drop for assets
         listView.setCellFactory(lv -> {
             ListCell<EditorScreen.AssetEntry> cell = createAssetListCell();
             
             // Drag detection
             cell.setOnDragDetected(event -> {
                 if (!cell.isEmpty()) {
                     EditorScreen.AssetEntry selected = cell.getItem();
                     if (selected != null && !selected.isBack() && selected.getFile().isFile()) {
                         Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                         ClipboardContent content = new ClipboardContent();
                         try {
                             Image img = model.loadImageFromAssetPath(selected.getFile().getAbsolutePath());
                             if(img != null) {
                                 content.putImage(img);
                                 // Store asset path for drop handling
                                 content.putString(model.getRelativeAssetPath(selected.getFile())); 
                                 db.setContent(content);
                             } else {
                                System.err.println("Failed to load image for drag: " + selected.getFile().getPath());
                             }
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                 }
                 event.consume();
             });
             return cell;
         });
         
         updateAssetListView(listView); // Initial population
    }
    
     /**
      * Handles the selection of an asset file from the list view.
      * Loads the image, updates the model with the selected asset and its path,
      * and switches the editor to asset placement mode.
      *
      * @param assetFile The selected asset File object.
      */
     private void handleAssetSelection(File assetFile) {
         try {
            Image img = model.loadImageFromAssetPath(assetFile.getAbsolutePath());
             if (img != null) {
                 model.setSelectedAssetImage(img);
                 model.setSelectedAssetPath(model.getRelativeAssetPath(assetFile));
                 model.setCurrentTileType(null); // Switch to asset placement mode
                 model.setEraserMode(false); // Disable eraser
             } else {
                 System.err.println("Failed to load selected asset image: " + assetFile.getPath());
             }
         } catch (Exception e) {
             e.printStackTrace();
             view.showAlert("Erreur Asset", "Impossible de charger l'image pour: " + assetFile.getName());
         }
     }

    /**
     * Updates the content of the asset list view based on the model's current directory.
     * Clears the existing items and repopulates with directories and .png files,
     * including a "Go Back" option if applicable.
     *
     * @param listView The ListView to update.
     */
    public void updateAssetListView(ListView<EditorScreen.AssetEntry> listView) {
        listView.getItems().clear();
        File currentDir = model.getCurrentDirectory();
        
        // Add 'Go Back' item if not in root
        if (!currentDir.equals(model.getRootAssets())) {
            listView.getItems().add(new EditorScreen.AssetEntry(currentDir.getParentFile(), true));
        }

        File[] files = currentDir.listFiles();
        if (files != null) {
            Arrays.sort(files, (f1, f2) -> {
                 if (f1.isDirectory() && !f2.isDirectory()) return -1;
                 if (!f1.isDirectory() && f2.isDirectory()) return 1;
                 return f1.getName().compareToIgnoreCase(f2.getName());
            });
            
            for (File f : files) {
                if (f.isDirectory()) {
                     listView.getItems().add(new EditorScreen.AssetEntry(f, false));
                } else if (f.isFile() && f.getName().toLowerCase().endsWith(".png")) {
                     listView.getItems().add(new EditorScreen.AssetEntry(f, false));
                }
            }
        }
    }

    /**
     * Creates a custom ListCell for displaying asset entries (files/directories).
     * Shows icons for image files and text names for directories and the "Go Back" item.
     *
     * @return A configured ListCell for the asset ListView.
     */
    private ListCell<EditorScreen.AssetEntry> createAssetListCell() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(EditorScreen.AssetEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setText(item.getName()); // Set text first (name or 'Go Back')
                    if (item.isBack()) {
                        setGraphic(null); // No icon for 'Go Back'
                    } else if (item.getFile().isDirectory()) {
                         // Optionally, set a folder icon here
                         setGraphic(null); 
                    } else {
                         // It's a file (image)
                         try {
                             Image img = model.loadImageFromAssetPath(item.getFile().getAbsolutePath());
                             if (img != null) {
                                 imageView.setImage(img);
                                 setGraphic(imageView);
                                 setText(null); // Show image instead of text
                             } else {
                                 // Failed to load image, show name
                                 setGraphic(null); 
                             }
                         } catch (Exception e) {
                             setGraphic(null); 
                             System.err.println("Error loading list cell image: " + e.getMessage());
                         }
                    }
                }
            }
        };
    }

    /**
     * Creates the main GridPane for the map editor.
     * Initializes the grid with empty cells based on the specified dimensions.
     *
     * @param rows    Number of rows for the grid.
     * @param columns Number of columns for the grid.
     * @return The initialized GridPane.
     */
    private GridPane createMapGrid(int rows, int columns) {
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(Insets.EMPTY); // Use Insets.EMPTY

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                StackPane cell = createGridCell();
                grid.add(cell, col, row);
            }
        }
        return grid;
    }

    /**
     * Creates a single cell (StackPane) for the map grid.
     * Initializes the cell with a default background, size, and default Tile properties.
     * Attaches necessary event handlers (click, drag over, drag dropped).
     *
     * @return The configured StackPane representing a grid cell.
     */
    private StackPane createGridCell() {
        StackPane cell = new StackPane();
        cell.setPrefSize(model.getCellSize(), model.getCellSize());

        Rectangle background = new Rectangle(model.getCellSize(), model.getCellSize());
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);

        // Initialize with a default passable tile
        cell.getProperties().put("tile", new Tile(true, 1.0, 0));

        // Event Handlers for the cell
        cell.setOnMouseClicked(event -> handleCellClick(cell));
        cell.setOnDragOver(event -> handleDragOver(event, cell));
        cell.setOnDragDropped(event -> handleDragDropped(event, cell));

        return cell;
    }

    /**
     * Handles mouse click events on a grid cell.
     * Applies the currently selected tile property, places the selected asset,
     * or erases the cell content based on the current editor mode.
     *
     * @param cell The StackPane cell that was clicked.
     */
    private void handleCellClick(StackPane cell) {
        if (model.getCurrentTileType() != null) {
            // Apply selected tile property
            switch (model.getCurrentTileType()) {
                case NORMAL: model.setCellAsNormal(cell); break;
                case OBSTACLE: model.setCellAsObstacle(cell); break;
                case SLOW: model.setCellAsSlowZone(cell); break;
                case POISON: model.setCellAsPoison(cell); break;
            }
        } else if (model.isEraserMode()) {
             // Erase content (asset and reset tile property)
            clearCell(cell);
        } else if (model.getSelectedAssetImage() != null) {
            // Place selected asset
            placeAssetOnCell(cell);
        }
    }

    /**
     * Handles drag over events on a grid cell.
     * Accepts the transfer if the dragboard contains an image or the expected asset path string.
     * Provides visual feedback that a drop is possible.
     *
     * @param event The DragEvent.
     * @param cell  The StackPane cell being dragged over.
     */
    private void handleDragOver(DragEvent event, StackPane cell) {
        // Accept drop only if it has an image or string (our asset path)
        if (event.getGestureSource() != cell && (event.getDragboard().hasImage() || event.getDragboard().hasString())) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    /**
     * Handles drag dropped events on a grid cell.
     * Retrieves the asset path from the dragboard, loads the corresponding image,
     * and places the asset onto the cell.
     *
     * @param event The DragEvent containing drop data.
     * @param cell  The StackPane cell where the item was dropped.
     */
    private void handleDragDropped(DragEvent event, StackPane cell) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) { // Prioritize the path string we put
            String relativeAssetPath = db.getString();
            try {
                Image img = model.loadImageFromAssetPath(model.getRootAssets().getPath() + File.separator + relativeAssetPath.replace("/", File.separator));
                 if(img != null) {
                    placeAssetOnCell(cell, img, relativeAssetPath);
                    success = true;
                 } else {
                     System.err.println("Failed to load dropped image from path: " + relativeAssetPath);
                 }
            } catch (Exception e) {
                 e.printStackTrace();
            }
        } else if (db.hasImage()) {
             // Fallback if only image was transferred (less reliable)
             // We don't have the path here, so we can't easily save it.
             // Consider disallowing drops without the path string.
             System.out.println("Dropped image without path data.");
             // placeAssetOnCell(cell, db.getImage(), ""); // Cannot save path
             // success = true; 
        }
        event.setDropCompleted(success);
        event.consume();
    }
    
     /**
      * Helper method to place the currently selected asset (from the ListView) onto a cell.
      * Used typically by the handleCellClick method.
      *
      * @param cell The target StackPane cell.
      */
     private void placeAssetOnCell(StackPane cell) {
         // Overload for placing the currently selected asset (e.g., on click)
         if (model.getSelectedAssetImage() != null && model.getSelectedAssetPath() != null) {
             placeAssetOnCell(cell, model.getSelectedAssetImage(), model.getSelectedAssetPath());
         }
     }

    /**
     * Places a specified asset image onto a grid cell.
     * Clears previous content, adds the default background, then adds the asset ImageView.
     * Stores the relative asset path in the cell's UserData for saving purposes.
     * Optionally resets the underlying tile properties.
     *
     * @param cell              The target StackPane cell.
     * @param assetImage        The Image to place.
     * @param relativeAssetPath The relative path of the asset (for saving), or null if unknown.
     */
    private void placeAssetOnCell(StackPane cell, Image assetImage, String relativeAssetPath) {
        // Clear previous content first (background might be needed)
        cell.getChildren().clear(); 
        Rectangle newBackground = new Rectangle(model.getCellSize(), model.getCellSize());
        newBackground.setFill(Color.LIGHTGRAY); // Or keep original tile color?
        newBackground.setStroke(Color.BLACK);
        cell.getChildren().add(newBackground);
        
        ImageView assetView = new ImageView(assetImage);
        assetView.setFitWidth(model.getCellSize());
        assetView.setFitHeight(model.getCellSize());
        assetView.setPreserveRatio(true);
        cell.getChildren().add(assetView);
        
        // Store the *relative* path for saving
        cell.setUserData(relativeAssetPath); 
        
         // Reset tile property to default passable when placing asset?
         // Or keep the underlying property?
         // cell.getProperties().put("tile", new Tile(true, 1.0, 0)); 
    }

    /**
     * Clears the content of a specific grid cell.
     * Removes any placed asset image and resets the background and tile properties
     * to their default state (passable, normal speed, no damage, light gray background).
     *
     * @param cell The StackPane cell to clear.
     */
    private void clearCell(StackPane cell) {
        cell.getChildren().clear();
        Rectangle background = new Rectangle(model.getCellSize(), model.getCellSize());
        background.setFill(Color.LIGHTGRAY); // Reset to default background
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
        cell.setUserData(null); // Remove asset path
        // Reset tile property to default
        cell.getProperties().put("tile", new Tile(true, 1.0, 0)); 
    }
}