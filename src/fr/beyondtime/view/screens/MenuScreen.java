package fr.beyondtime.view.screens;

import fr.beyondtime.controller.editor.EditorController;
import fr.beyondtime.util.MapManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MenuScreen extends VBox {
    private Stage stage;

    public MenuScreen(Stage stage) {
        this.stage = stage;
        setupUI();
    }

    private void setupUI() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        Button btnNiveau1 = new Button("Niveau 1 - Préhistoire");
        Button btnNiveau2 = new Button("Niveau 2 - Égypte Antique");
        Button btnNiveau3 = new Button("Niveau 3 - 2nde Guerre Mondiale");
        Button btnEditor = new Button("Éditeur de niveaux");
        Button btnRetour = new Button("Retour");

        btnNiveau1.getStyleClass().add("classique-button");
        btnNiveau2.getStyleClass().add("classique-button");
        btnNiveau3.getStyleClass().add("classique-button");
        btnEditor.getStyleClass().add("classique-button");
        btnRetour.getStyleClass().add("classique-button");

        // Configuration des actions des boutons
        btnNiveau1.setOnAction(e -> {
            MapManager.selectAndLoadMap(stage, "Niveau 1 - Préhistoire");
        });

        btnNiveau2.setOnAction(e -> {
            MapManager.selectAndLoadMap(stage, "Niveau 2 - Égypte Antique");
        });

        btnNiveau3.setOnAction(e -> {
            MapManager.selectAndLoadMap(stage, "Niveau 3 - 2nde Guerre Mondiale");
        });

        btnEditor.setOnAction(e -> {
            EditorController editorController = new EditorController(stage);
        });

        btnRetour.setOnAction(e -> {
            stage.close();
        });

        getChildren().addAll(btnNiveau1, btnNiveau2, btnNiveau3, btnEditor, btnRetour);
    }
} 