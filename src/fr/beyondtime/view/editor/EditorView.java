package fr.beyondtime.view.editor;

import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class EditorView extends VBox {

    private StackPane contentPane;

    public EditorView() {
        setPrefHeight(600.0);
        setPrefWidth(900.0);

        // Création du MenuBar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem newGrillItem = new MenuItem("New Grill");
        fileMenu.getItems().add(newGrillItem);
        menuBar.getMenus().add(fileMenu);
        
        // Création du SplitPane et des panneaux
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.25);

        AnchorPane leftPane = new AnchorPane();
        // (Ajoutez d'autres éléments à gauche si nécessaire)

        ScrollPane scrollPane = new ScrollPane();
        // Permet de forcer le ScrollPane à redimensionner son contenu
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        contentPane = new StackPane();  // Utilisation de StackPane pour centrer la grille
        contentPane.setPrefHeight(545.0);
        contentPane.setPrefWidth(430.0);
        contentPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setContent(contentPane);

        splitPane.getItems().addAll(leftPane, scrollPane);

        getChildren().addAll(menuBar, splitPane);
        
        // Ajout de l'action pour "New Grill"
        newGrillItem.setOnAction(event -> {
            GridPane grid = showNewGrillDialog();
            if (grid != null) {
                contentPane.getChildren().clear();
                contentPane.getChildren().add(grid);
            }
        });
    }
    
    private GridPane showNewGrillDialog() {
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle grille");
        dialog.setHeaderText("Entrez les dimensions de la grille (ex: 25x25)");

        ButtonType createButtonType = new ButtonType("Créer", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField dimensionField = new TextField();
        dimensionField.setPromptText("25x25");

        grid.add(new Label("Dimensions:"), 0, 0);
        grid.add(dimensionField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String text = dimensionField.getText();
                if (text != null && text.matches("\\d+x\\d+")) {
                    String[] parts = text.split("x");
                    int rows = Integer.parseInt(parts[0]);
                    int columns = Integer.parseInt(parts[1]);
                    return new Pair<>(rows, columns);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Format invalide. Veuillez entrer par exemple : 25x25", ButtonType.OK);
                    alert.showAndWait();
                }
            }
            return null;
        });

        Optional<Pair<Integer, Integer>> result = dialog.showAndWait();
        if (result.isPresent()) {
            int rows = result.get().getKey();
            int columns = result.get().getValue();
            return createGrid(rows, columns);
        }
        return null;
    }

    private GridPane createGrid(int rows, int columns) {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(0);
        gridPane.setVgap(0);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Pane cell = new Pane();
                cell.setPrefSize(20, 20);
                cell.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid;");
                gridPane.add(cell, j, i);
            }
        }
        return gridPane;
    }
}
