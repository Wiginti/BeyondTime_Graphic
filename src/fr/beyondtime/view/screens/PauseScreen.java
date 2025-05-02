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

public class PauseScreen {
    private Stage stage;
    private Runnable onResumeClick;
    private Runnable onQuitClick;
    private Runnable onConfigClick;

    public PauseScreen(Stage parentStage, Runnable onResumeClick, Runnable onQuitClick, Runnable onConfigClick) {
        this.stage = new Stage();
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

        Button configButton = createButton("Configuration");
        configButton.setOnAction(e -> {
            if (onConfigClick != null) onConfigClick.run();
        });

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

    public void show() {
        stage.show();
    }
} 