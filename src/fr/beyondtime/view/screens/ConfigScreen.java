package fr.beyondtime.view.screens;

import fr.beyondtime.model.config.GameConfig;
import fr.beyondtime.util.TranslationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfigScreen extends VBox {
    private Stage stage;
    private GameConfig config;
    private Scene previousScene;
    private TranslationManager translator;

    private Label titleLabel;
    private Label resolutionLabel;
    private Label languageLabel;
    private Button saveButton;
    private Button cancelButton;

    public ConfigScreen(Stage stage, Scene previousScene) {
        this.stage = stage;
        this.previousScene = previousScene;
        this.config = GameConfig.getInstance();
        this.translator = TranslationManager.getInstance();
        setupUI();
        setupTranslations();
    }

    private void setupUI() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(20));
        getStyleClass().add("config-screen");

        titleLabel = new Label();
        titleLabel.getStyleClass().add("config-title");

        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(10);
        settingsGrid.setAlignment(Pos.CENTER);

        // Resolution settings
        resolutionLabel = new Label();
        ComboBox<GameConfig.Resolution> resolutionComboBox = new ComboBox<>();
        resolutionComboBox.getItems().addAll(GameConfig.AVAILABLE_RESOLUTIONS);
        resolutionComboBox.setValue(config.getCurrentResolution());
        resolutionComboBox.setOnAction(e -> {
            GameConfig.Resolution selectedRes = resolutionComboBox.getValue();
            config.setCurrentResolution(selectedRes);
            config.applySafeResolutionToStage(stage);
        });

        // Language settings
        languageLabel = new Label();
        ComboBox<GameConfig.Language> languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll(GameConfig.AVAILABLE_LANGUAGES);
        languageComboBox.setValue(config.getCurrentLanguage());
        languageComboBox.setOnAction(e -> {
            GameConfig.Language selectedLang = languageComboBox.getValue();
            config.setCurrentLanguage(selectedLang);
            translator.setLocale(selectedLang.getLocale());
            config.saveConfig();
            
            // Forcer un rafraîchissement complet de l'interface
            MenuScreen refreshedMenu = new MenuScreen(stage);
            Scene newScene = refreshedMenu.getMenuScene();
            newScene.getStylesheets().add(getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm());

            // Recréer l'écran de configuration avec la nouvelle langue
            ConfigScreen refreshedConfig = new ConfigScreen(stage, newScene);
            Scene refreshedConfigScene = new Scene(refreshedConfig);
            refreshedConfigScene.getStylesheets().add(getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm());
            
            // Appliquer la nouvelle scène
            stage.setScene(refreshedConfigScene);
        });

        settingsGrid.add(resolutionLabel, 0, 0);
        settingsGrid.add(resolutionComboBox, 1, 0);
        settingsGrid.add(languageLabel, 0, 1);
        settingsGrid.add(languageComboBox, 1, 1);

        saveButton = new Button();
        saveButton.getStyleClass().add("classique-button");
        saveButton.setOnAction(e -> {
            config.saveConfig();

            // Recharge le MenuScreen avec la bonne résolution et style
            MenuScreen refreshedMenu = new MenuScreen(stage);
            Scene newScene = refreshedMenu.getMenuScene();

            GameConfig.Resolution res = config.getCurrentResolution();
            if (res.getWidth() <= 800 && res.getHeight() <= 600) {
                newScene.getRoot().getStyleClass().add("small-ui");
            } else {
                newScene.getRoot().getStyleClass().add("normal-ui");
            }

            stage.setScene(newScene);
        });

        cancelButton = new Button();
        cancelButton.getStyleClass().add("classique-button");
        cancelButton.setOnAction(e -> stage.setScene(previousScene));

        getChildren().addAll(titleLabel, settingsGrid, saveButton, cancelButton);
    }

    private void setupTranslations() {
        updateTranslations();
        translator.currentLocaleProperty().addListener((obs, oldLocale, newLocale) -> {
            updateTranslations();
        });
    }

    private void updateTranslations() {
        titleLabel.setText(translator.get("config.title"));
        resolutionLabel.setText(translator.get("config.resolution"));
        languageLabel.setText(translator.get("config.language"));
        saveButton.setText(translator.get("config.save"));
        cancelButton.setText(translator.get("config.back"));
    }
}
