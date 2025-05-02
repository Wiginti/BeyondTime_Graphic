package fr.beyondtime.view.screens;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.text.TextAlignment;
import fr.beyondtime.model.config.GameConfig;
import fr.beyondtime.model.game.GameState;
import fr.beyondtime.util.SaveGameManager;
import javafx.scene.control.TextInputDialog;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class PauseScreen {
    private Stage stage;
    @SuppressWarnings("unused")
	private Runnable onResumeClick;
    @SuppressWarnings("unused")
	private Runnable onQuitClick;
    @SuppressWarnings("unused")
	private Runnable onConfigClick;
    private GameState gameState;

    public PauseScreen(Stage parentStage, GameState gameState, Runnable onResumeClick, Runnable onQuitClick, Runnable onConfigClick) {
        this.stage = new Stage();
        this.gameState = gameState;
        this.onResumeClick = onResumeClick;
        this.onQuitClick = onQuitClick;
        this.onConfigClick = onConfigClick;

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Pause");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-border-color: white; -fx-border-width: 2;");

        // Titre
        Text titleText = new Text("Pause");
        titleText.setStyle("-fx-fill: white; -fx-font-size: 24;");

        // Boutons
        Button resumeButton = createButton("Reprendre la partie");
        resumeButton.setOnAction(e -> {
            stage.close();
            if (onResumeClick != null) onResumeClick.run();
        });

        Button saveButton = createButton("Sauvegarder la partie");
        saveButton.setOnAction(e -> showSaveDialog());

        Button loadButton = createButton("Charger une partie");
        loadButton.setOnAction(e -> showLoadDialog());

        Button configButton = createButton("Configuration");
        configButton.setOnAction(e -> showConfigWindow());

        Button helpButton = createButton("Aide");
        helpButton.setOnAction(e -> showHelpWindow());

        Button quitButton = createButton("Quitter la partie");
        quitButton.setOnAction(e -> {
            stage.close();
            if (onQuitClick != null) onQuitClick.run();
        });

        layout.getChildren().addAll(
            titleText,
            resumeButton,
            saveButton,
            loadButton,
            configButton,
            helpButton,
            quitButton
        );

        Scene scene = new Scene(layout, 300, 400);
        scene.setFill(null);
        stage.setScene(scene);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #4a4a4a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14;" +
            "-fx-min-width: 200;" +
            "-fx-padding: 10;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #6a6a6a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14;" +
            "-fx-min-width: 200;" +
            "-fx-padding: 10;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #4a4a4a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14;" +
            "-fx-min-width: 200;" +
            "-fx-padding: 10;"
        ));
        return button;
    }

    private void showHelpWindow() {
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.initOwner(stage);
        helpStage.setTitle("Aide - Contrôles");

        VBox helpLayout = new VBox(10);
        helpLayout.setAlignment(Pos.CENTER);
        helpLayout.setPadding(new Insets(20));
        helpLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: white; -fx-border-width: 2;");

        Text helpTitle = new Text("Contrôles du jeu");
        helpTitle.setStyle("-fx-fill: white; -fx-font-size: 20;");
        helpTitle.setTextAlignment(TextAlignment.CENTER);

        Text controls = new Text(
            "Déplacements:\n" +
            "↑ ou Z : Haut\n" +
            "↓ ou S : Bas\n" +
            "← ou Q : Gauche\n" +
            "→ ou D : Droite\n\n" +
            "Actions:\n" +
            "1-5 : Sélectionner un objet\n" +
            "F : Utiliser l'objet sélectionné\n" +
            "Clic gauche : Attaquer\n" +
            "Échap : Menu pause"
        );
        controls.setStyle("-fx-fill: white; -fx-font-size: 14;");
        controls.setTextAlignment(TextAlignment.LEFT);

        Button closeButton = createButton("Fermer");
        closeButton.setOnAction(e -> helpStage.close());

        helpLayout.getChildren().addAll(helpTitle, controls, closeButton);

        Scene helpScene = new Scene(helpLayout, 300, 400);
        helpScene.setFill(null);
        helpStage.setScene(helpScene);
        helpStage.show();
    }

    private void showConfigWindow() {
        Stage configStage = new Stage();
        configStage.initModality(Modality.APPLICATION_MODAL);
        configStage.initOwner(stage);
        configStage.setTitle("Configuration");

        VBox configLayout = new VBox(10);
        configLayout.setAlignment(Pos.CENTER);
        configLayout.setPadding(new Insets(20));
        configLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: white; -fx-border-width: 2;");

        Text configTitle = new Text("Configuration du jeu");
        configTitle.setStyle("-fx-fill: white; -fx-font-size: 20;");
        configTitle.setTextAlignment(TextAlignment.CENTER);

        // Créer les contrôles de configuration
        VBox settingsBox = new VBox(15);
        settingsBox.setAlignment(Pos.CENTER);

        // Volume de la musique
        HBox musicBox = new HBox(10);
        musicBox.setAlignment(Pos.CENTER);
        Text musicLabel = new Text("Volume de la musique:");
        musicLabel.setStyle("-fx-fill: white;");
        javafx.scene.control.Slider musicSlider = new javafx.scene.control.Slider(0, 100, 50);
        musicBox.getChildren().addAll(musicLabel, musicSlider);

        // Volume des effets sonores
        HBox sfxBox = new HBox(10);
        sfxBox.setAlignment(Pos.CENTER);
        Text sfxLabel = new Text("Volume des effets:");
        sfxLabel.setStyle("-fx-fill: white;");
        javafx.scene.control.Slider sfxSlider = new javafx.scene.control.Slider(0, 100, 50);
        sfxBox.getChildren().addAll(sfxLabel, sfxSlider);

        // Résolution
        HBox resolutionBox = new HBox(10);
        resolutionBox.setAlignment(Pos.CENTER);
        Text resolutionLabel = new Text("Résolution:");
        resolutionLabel.setStyle("-fx-fill: white;");
        javafx.scene.control.ComboBox<String> resolutionCombo = new javafx.scene.control.ComboBox<>();
        resolutionCombo.getItems().addAll("1920x1080", "1600x900", "1280x720");
        
        // Définir la résolution actuelle
        String currentResolution = GameConfig.getInstance().getCurrentResolution().getWidth() + "x" + 
                                 GameConfig.getInstance().getCurrentResolution().getHeight();
        resolutionCombo.setValue(currentResolution);
        
        resolutionBox.getChildren().addAll(resolutionLabel, resolutionCombo);

        settingsBox.getChildren().addAll(musicBox, sfxBox, resolutionBox);

        // Boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveButton = createButton("Appliquer");
        saveButton.setOnAction(e -> {
            // Appliquer les changements de résolution
            String selectedResolution = resolutionCombo.getValue();
            String[] dimensions = selectedResolution.split("x");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);
            
            // Créer un nouvel objet Resolution et l'appliquer
            GameConfig.Resolution newResolution = new GameConfig.Resolution(width, height);
            GameConfig.getInstance().setCurrentResolution(newResolution);
            
            // Mettre à jour la taille de la fenêtre principale
            Stage mainStage = (Stage) stage.getOwner();
            GameConfig.getInstance().applySafeResolutionToStage(mainStage);
            
            // Fermer la fenêtre de configuration
            configStage.close();
        });
        
        Button cancelButton = createButton("Annuler");
        cancelButton.setOnAction(e -> configStage.close());
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        configLayout.getChildren().addAll(configTitle, settingsBox, buttonBox);

        Scene configScene = new Scene(configLayout, 400, 500);
        configScene.setFill(null);
        configStage.setScene(configScene);
        configStage.show();
    }

    private void showSaveDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sauvegarder la partie");
        dialog.setHeaderText("Entrez un nom pour votre sauvegarde");
        dialog.setContentText("Nom de la sauvegarde:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(saveName -> {
            Path savePath = SaveGameManager.saveGame(gameState, saveName);
            if (savePath != null) {
                showInfoDialog("Sauvegarde réussie", "La partie a été sauvegardée avec succès.");
            } else {
                showErrorDialog("Erreur de sauvegarde", "Une erreur est survenue lors de la sauvegarde.");
            }
        });
    }

    private void showLoadDialog() {
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
                Button saveButton = createButton(save.getFileName().toString());
                saveButton.setOnAction(e -> {
                    GameState loadedState = SaveGameManager.loadGame(save);
                    if (loadedState != null) {
                        // Update the current game state
                        this.gameState = loadedState;
                        loadStage.close();
                        stage.close();
                        // Refresh the game screen with the loaded state
                        new GameScreen((Stage) stage.getOwner(), loadedState);
                    } else {
                        showErrorDialog("Erreur de chargement", "Impossible de charger la sauvegarde sélectionnée.");
                    }
                });
                savesList.getChildren().add(saveButton);
            }
        }

        Button closeButton = createButton("Fermer");
        closeButton.setOnAction(e -> loadStage.close());

        loadLayout.getChildren().addAll(loadTitle, savesList, closeButton);

        Scene loadScene = new Scene(loadLayout, 400, 500);
        loadScene.setFill(null);
        loadStage.setScene(loadScene);
        loadStage.show();
    }

    private void showInfoDialog(String title, String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.setTitle(title);

        VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: white; -fx-border-width: 2;");

        Text messageText = new Text(message);
        messageText.setStyle("-fx-fill: white;");

        Button okButton = createButton("OK");
        okButton.setOnAction(e -> dialogStage.close());

        dialogLayout.getChildren().addAll(messageText, okButton);

        Scene dialogScene = new Scene(dialogLayout, 300, 150);
        dialogScene.setFill(null);
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    private void showErrorDialog(String title, String message) {
        Stage errorStage = new Stage();
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.initOwner(stage);
        errorStage.setTitle(title);

        VBox errorLayout = new VBox(10);
        errorLayout.setAlignment(Pos.CENTER);
        errorLayout.setPadding(new Insets(20));
        errorLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: white; -fx-border-width: 2;");

        Text errorText = new Text(message);
        errorText.setStyle("-fx-fill: white;");

        Button okButton = createButton("OK");
        okButton.setOnAction(e -> errorStage.close());

        errorLayout.getChildren().addAll(errorText, okButton);

        Scene errorScene = new Scene(errorLayout, 300, 150);
        errorScene.setFill(null);
        errorStage.setScene(errorScene);
        errorStage.show();
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
} 