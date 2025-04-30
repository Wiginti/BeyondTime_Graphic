package fr.beyondtime.view.screens;

import fr.beyondtime.controller.editor.EditorController;
import fr.beyondtime.util.MapManager;
import fr.beyondtime.util.TranslationManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MenuScreen extends VBox {
    private Stage stage;
    private Scene menuScene;
    private TranslationManager translator;
    
    // UI elements that need translation
    private Button btnNiveau1;
    private Button btnNiveau2;
    private Button btnNiveau3;
    private Button btnEditor;
    private Button btnConfig;
    private Button btnRetour;

    public MenuScreen(Stage stage) {
        this.stage = stage;
        this.translator = TranslationManager.getInstance();
        setupUI();
        setupTranslations();
        this.menuScene = new Scene(this);
        this.menuScene.getStylesheets().add(getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm());
    }

    private void setupUI() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        btnNiveau1 = new Button();
        btnNiveau2 = new Button();
        btnNiveau3 = new Button();
        btnEditor = new Button();
        btnConfig = new Button();
        btnRetour = new Button();

        btnNiveau1.getStyleClass().add("classique-button");
        btnNiveau2.getStyleClass().add("classique-button");
        btnNiveau3.getStyleClass().add("classique-button");
        btnEditor.getStyleClass().add("classique-button");
        btnConfig.getStyleClass().add("classique-button");
        btnRetour.getStyleClass().add("classique-button");

        // Configuration des actions des boutons
        btnNiveau1.setOnAction(e -> {
            MapManager.selectAndLoadMap(stage, translator.get("menu.level1"));
        });

        btnNiveau2.setOnAction(e -> {
            MapManager.selectAndLoadMap(stage, translator.get("menu.level2"));
        });

        btnNiveau3.setOnAction(e -> {
            MapManager.selectAndLoadMap(stage, translator.get("menu.level3"));
        });

        btnEditor.setOnAction(e -> {
            EditorController editorController = new EditorController(stage);
        });

        btnConfig.setOnAction(e -> {
            ConfigScreen configScreen = new ConfigScreen(stage, menuScene);
            Scene configScene = new Scene(configScreen);
            configScene.getStylesheets().add(getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm());
            stage.setScene(configScene);
        });

        btnRetour.setOnAction(e -> {
            stage.close();
        });

        getChildren().addAll(
            btnNiveau1, 
            btnNiveau2, 
            btnNiveau3, 
            btnEditor, 
            btnConfig, 
            btnRetour
        );
    }

    private void setupTranslations() {
        // Initial translation
        updateTranslations();
        
        // Listen for locale changes
        translator.currentLocaleProperty().addListener((obs, oldLocale, newLocale) -> {
            updateTranslations();
        });
    }

    private void updateTranslations() {
        btnNiveau1.setText(translator.get("menu.level1"));
        btnNiveau2.setText(translator.get("menu.level2"));
        btnNiveau3.setText(translator.get("menu.level3"));
        btnEditor.setText(translator.get("menu.editor"));
        btnConfig.setText(translator.get("menu.config"));
        btnRetour.setText(translator.get("menu.quit"));
    }

    public Scene getMenuScene() {
        return menuScene;
    }
} 