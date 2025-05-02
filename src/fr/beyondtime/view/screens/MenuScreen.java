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

    private Button btnNiveaux;
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
        setSpacing(20);
        setPadding(new Insets(20));
        setMaxWidth(400);
        getStyleClass().add("vbox-menu");

        VBox buttonsBox = new VBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        btnNiveaux = createMenuButton();
        btnLoadGame = createMenuButton();
        btnEditor = createMenuButton();
        btnConfig = createMenuButton();
        btnRetour = createMenuButton();

        btnNiveaux.getStyleClass().add("classique-button");
        btnLoadGame.getStyleClass().add("classique-button");
        btnEditor.getStyleClass().add("classique-button");
        btnConfig.getStyleClass().add("classique-button");
        btnRetour.getStyleClass().add("classique-button");

        btnNiveaux.setOnAction(e -> showLevelsDialog());
        btnLoadGame.setOnAction(e -> showLoadGameDialog());
        btnEditor.setOnAction(e -> new EditorController(stage));
        btnConfig.setOnAction(e -> {
            ConfigScreen configScreen = new ConfigScreen(stage, menuScene);
            Scene configScene = new Scene(configScreen);
            configScene.getStylesheets().add(getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm());
            stage.setScene(configScene);
        });
        btnRetour.setOnAction(e -> stage.close());

        buttonsBox.getChildren().addAll(btnNiveaux, btnLoadGame, btnEditor, btnConfig, btnRetour);
        getChildren().add(buttonsBox);
    }

    private void showLevelsDialog() {
        final Stage levelStage = new Stage();
        levelStage.initModality(Modality.APPLICATION_MODAL);
        levelStage.initOwner(stage);
        levelStage.setTitle(translator.get("menu.levels"));

        VBox levelLayout = new VBox(30);
        levelLayout.setAlignment(Pos.CENTER);
        levelLayout.setPadding(new Insets(40));
        levelLayout.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #1a2a3d, #2a3a4d);
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 2;
            """);

        Text levelTitle = new Text(translator.get("menu.select.level"));
        levelTitle.setStyle("""
            -fx-fill: #e0e0e0;
            -fx-font-size: 28;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, #000000, 2, 0.3, 0, 1);
            """);

        VBox levelsBox = new VBox(20);
        levelsBox.setAlignment(Pos.CENTER);
        levelsBox.setPadding(new Insets(20, 0, 20, 0));
        levelsBox.setStyle("-fx-background-color: rgba(26, 42, 61, 0.6); -fx-background-radius: 5;");

        final String LEVEL1_NAME = "Préhistoire";
        final String LEVEL2_NAME = "Égypte Antique";
        final String LEVEL3_NAME = "2nde Guerre Mondiale";

        Button btnNiveau1 = createLevelButton(translator.get("menu.level1"), "prehistoric-theme");
        Button btnNiveau2 = createLevelButton(translator.get("menu.level2"), "egypt-theme");
        Button btnNiveau3 = createLevelButton(translator.get("menu.level3"), "ww2-theme");

        btnNiveau1.setOnAction(e -> {
            levelStage.close();
            MapManager.selectAndLoadMap(stage, LEVEL1_NAME);
        });
        btnNiveau2.setOnAction(e -> {
            levelStage.close();
            MapManager.selectAndLoadMap(stage, LEVEL2_NAME);
        });
        btnNiveau3.setOnAction(e -> {
            levelStage.close();
            MapManager.selectAndLoadMap(stage, LEVEL3_NAME);
        });

        levelsBox.getChildren().addAll(btnNiveau1, btnNiveau2, btnNiveau3);

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
        closeButton.setOnAction(e -> levelStage.close());

        levelLayout.getChildren().addAll(levelTitle, levelsBox, closeButton);

        Scene scene = new Scene(levelLayout);
        levelStage.setScene(scene);
        levelStage.showAndWait();
    }

    private Button createLevelButton(String text, String theme) {
        final Button button = new Button(text);
        
        // Style de base
        final String baseStyle = """
            -fx-background-color: #2a3a4d;
            -fx-text-fill: #e0e0e0;
            -fx-font-size: 20;
            -fx-padding: 20 40;
            -fx-min-width: 300;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-width: 2;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, #000000, 10, 0.5, 0, 0);
            """;

        // Ajouter des styles spécifiques selon le thème
        final String themeStyle = switch (theme) {
            case "prehistoric-theme" -> baseStyle + """
                -fx-border-color: #8B4513;
                -fx-background-image: url('/fr/beyondtime/resources/menu/prehistoric.png');
                -fx-background-size: cover;
                """;
            case "egypt-theme" -> baseStyle + """
                -fx-border-color: #FFD700;
                -fx-background-image: url('/fr/beyondtime/resources/menu/egypt.png');
                -fx-background-size: cover;
                """;
            case "ww2-theme" -> baseStyle + """
                -fx-border-color: #696969;
                -fx-background-image: url('/fr/beyondtime/resources/menu/ww2.png');
                -fx-background-size: cover;
                """;
            default -> baseStyle;
        };

        button.setStyle(themeStyle);

        final String hoverStyle = themeStyle + """
            -fx-scale-x: 1.05;
            -fx-scale-y: 1.05;
            -fx-effect: dropshadow(gaussian, #000000, 20, 0.7, 0, 0);
            """;

        // Effets de survol
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(themeStyle));

        return button;
    }

    private Button createMenuButton() {
        Button button = new Button();
        button.setPrefWidth(250);
        button.setMinHeight(40);
        return button;
    }

    private void showLoadGameDialog() {
        final Stage loadStage = new Stage();
        loadStage.initModality(Modality.APPLICATION_MODAL);
        loadStage.initOwner(stage);
        loadStage.setTitle(translator.get("menu.load"));

        VBox loadLayout = new VBox(30);
        loadLayout.setAlignment(Pos.CENTER);
        loadLayout.setPadding(new Insets(40));
        loadLayout.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #1a2a3d, #2a3a4d);
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 2;
            """);

        Text loadTitle = new Text(translator.get("menu.available.saves"));
        loadTitle.setStyle("""
            -fx-fill: #e0e0e0;
            -fx-font-size: 28;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, #000000, 2, 0.3, 0, 1);
            """);

        VBox savesList = new VBox(20);
        savesList.setAlignment(Pos.CENTER);
        savesList.setPadding(new Insets(20, 0, 20, 0));
        savesList.setStyle("-fx-background-color: rgba(26, 42, 61, 0.6); -fx-background-radius: 5;");

        List<Path> saves = SaveGameManager.listSaves();
        if (saves.isEmpty()) {
            Text noSaves = new Text(translator.get("menu.no.saves"));
            noSaves.setStyle("""
                -fx-fill: #e0e0e0;
                -fx-font-size: 18;
                -fx-font-style: italic;
                """);
            savesList.getChildren().add(noSaves);
        } else {
            for (Path save : saves) {
                final String saveName = save.getFileName().toString();
                Button saveButton = createSaveButton(saveName);
                final Path currentSave = save;
                
                saveButton.setOnAction(e -> {
                    GameState loadedState = SaveGameManager.loadGame(currentSave);
                    if (loadedState != null) {
                        loadStage.close();
                        new GameScreen(stage, loadedState);
                    } else {
                        showErrorDialog(loadStage, translator.get("error.loading.title"), 
                                               translator.get("error.loading.message"));
                    }
                });
                savesList.getChildren().add(saveButton);
            }
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
        closeButton.setOnAction(e -> loadStage.close());

        loadLayout.getChildren().addAll(loadTitle, savesList, closeButton);

        Scene loadScene = new Scene(loadLayout, 500, 600);
        loadScene.setFill(null);
        loadStage.setScene(loadScene);
        loadStage.show();
    }

    private Button createSaveButton(String text) {
        final Button button = new Button(text);
        
        final String baseStyle = """
            -fx-background-color: #2a3a4d;
            -fx-text-fill: #e0e0e0;
            -fx-font-size: 20;
            -fx-padding: 15 30;
            -fx-min-width: 300;
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
        btnNiveaux.setText(translator.get("menu.levels"));
        btnLoadGame.setText(translator.get("menu.load"));
        btnEditor.setText(translator.get("menu.editor"));
        btnConfig.setText(translator.get("menu.config"));
        btnRetour.setText(translator.get("menu.quit"));
    }

    public Scene getMenuScene() {
        return menuScene;
    }
}