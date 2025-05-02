package fr.beyondtime.view.screens;

import fr.beyondtime.controller.editor.EditorController;
import fr.beyondtime.model.config.GameConfig;
import fr.beyondtime.model.game.GameState;
import fr.beyondtime.util.MapManager;
import fr.beyondtime.util.SaveGameManager;
import fr.beyondtime.util.TranslationManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.nio.file.Path;
import java.util.List;

public class MenuScreen extends VBox {
    private Stage stage;
    private Scene menuScene;
    private TranslationManager translator;

    private Button btnNiveau1;
    private Button btnNiveau2;
    private Button btnNiveau3;
    private Button btnLoadGame;
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
        int w = GameConfig.getInstance().getCurrentResolution().getWidth();
        int h = GameConfig.getInstance().getCurrentResolution().getHeight();
        if (w <= 800 && h <= 600) {
            getStyleClass().add("small-ui");
        }
    }

    private void setupUI() {
        setAlignment(Pos.CENTER);
        setSpacing(40);
        getStyleClass().add("vbox-menu");

        btnNiveau1 = new Button();
        btnNiveau2 = new Button();
        btnNiveau3 = new Button();
        btnLoadGame = new Button();
        btnEditor = new Button();
        btnConfig = new Button();
        btnRetour = new Button();

        btnNiveau1.getStyleClass().add("classique-button");
        btnNiveau2.getStyleClass().add("classique-button");
        btnNiveau3.getStyleClass().add("classique-button");
        btnLoadGame.getStyleClass().add("classique-button");
        btnEditor.getStyleClass().add("classique-button");
        btnConfig.getStyleClass().add("classique-button");
        btnRetour.getStyleClass().add("classique-button");

        btnNiveau1.setOnAction(e -> MapManager.selectAndLoadMap(stage, translator.get("menu.level1")));
        btnNiveau2.setOnAction(e -> MapManager.selectAndLoadMap(stage, translator.get("menu.level2")));
        btnNiveau3.setOnAction(e -> MapManager.selectAndLoadMap(stage, translator.get("menu.level3")));
        btnLoadGame.setOnAction(e -> showLoadGameDialog());
        btnEditor.setOnAction(e -> new EditorController(stage));

        btnConfig.setOnAction(e -> {
            ConfigScreen configScreen = new ConfigScreen(stage, menuScene);
            Scene configScene = new Scene(configScreen);
            configScene.getStylesheets().add(getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm());
            stage.setScene(configScene);
        });

        btnRetour.setOnAction(e -> stage.close());

        getChildren().addAll(
            btnNiveau1,
            btnNiveau2,
            btnNiveau3,
            btnLoadGame,
            btnEditor,
            btnConfig,
            btnRetour
        );
    }

    private void showLoadGameDialog() {
        Stage loadStage = new Stage();
        loadStage.initModality(Modality.APPLICATION_MODAL);
        loadStage.initOwner(stage);
        loadStage.setTitle("Charger une partie");

        VBox loadLayout = new VBox(10);
        loadLayout.setAlignment(Pos.CENTER);
        loadLayout.setPadding(new Insets(20));
        loadLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: white; -fx-border-width: 2;");

        Text loadTitle = new Text("Sauvegardes disponibles");
        loadTitle.setStyle("-fx-fill: white; -fx-font-size: 20;");

        VBox savesList = new VBox(5);
        savesList.setAlignment(Pos.CENTER);

        List<Path> saves = SaveGameManager.listSaves();
        if (saves.isEmpty()) {
            Text noSaves = new Text("Aucune sauvegarde disponible");
            noSaves.setStyle("-fx-fill: white;");
            savesList.getChildren().add(noSaves);
        } else {
            for (Path save : saves) {
                Button saveButton = new Button(save.getFileName().toString());
                saveButton.getStyleClass().add("classique-button");
                saveButton.setOnAction(e -> {
                    GameState loadedState = SaveGameManager.loadGame(save);
                    if (loadedState != null) {
                        loadStage.close();
                        new GameScreen(stage, loadedState);
                    } else {
                        showErrorDialog(loadStage, "Erreur de chargement", "Impossible de charger la sauvegarde sélectionnée.");
                    }
                });
                savesList.getChildren().add(saveButton);
            }
        }

        Button closeButton = new Button("Fermer");
        closeButton.getStyleClass().add("classique-button");
        closeButton.setOnAction(e -> loadStage.close());

        loadLayout.getChildren().addAll(loadTitle, savesList, closeButton);

        Scene loadScene = new Scene(loadLayout, 400, 500);
        loadScene.getStylesheets().add(getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm());
        loadStage.setScene(loadScene);
        loadStage.show();
    }

    private void showErrorDialog(Stage owner, String title, String message) {
        Stage errorStage = new Stage();
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.initOwner(owner);
        errorStage.setTitle(title);

        VBox errorLayout = new VBox(10);
        errorLayout.setAlignment(Pos.CENTER);
        errorLayout.setPadding(new Insets(20));
        errorLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: white; -fx-border-width: 2;");

        Text errorText = new Text(message);
        errorText.setStyle("-fx-fill: white;");

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("classique-button");
        okButton.setOnAction(e -> errorStage.close());

        errorLayout.getChildren().addAll(errorText, okButton);

        Scene errorScene = new Scene(errorLayout, 300, 150);
        errorScene.getStylesheets().add(getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm());
        errorStage.setScene(errorScene);
        errorStage.show();
    }

    private void setupTranslations() {
        updateTranslations();
        translator.currentLocaleProperty().addListener((obs, oldLocale, newLocale) -> updateTranslations());
    }

    private void updateTranslations() {
        btnNiveau1.setText(translator.get("menu.level1"));
        btnNiveau2.setText(translator.get("menu.level2"));
        btnNiveau3.setText(translator.get("menu.level3"));
        btnLoadGame.setText(translator.get("menu.load"));
        btnEditor.setText(translator.get("menu.editor"));
        btnConfig.setText(translator.get("menu.config"));
        btnRetour.setText(translator.get("menu.quit"));
    }

    public Scene getMenuScene() {
        return menuScene;
    }
}