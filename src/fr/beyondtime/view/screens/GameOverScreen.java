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

public class GameOverScreen {
    private Stage stage;
    private int monstersKilled;
    private Runnable onMenuClick;

    public GameOverScreen(Stage parentStage, int monstersKilled, Runnable onMenuClick) {
        this.stage = new Stage();
        this.monstersKilled = monstersKilled;
        this.onMenuClick = onMenuClick;

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Game Over");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: red; -fx-border-width: 2;");

        // Titre
        Text titleText = new Text("Game Over");
        titleText.setStyle("-fx-fill: red; -fx-font-size: 36; -fx-font-weight: bold;");
        titleText.setTextAlignment(TextAlignment.CENTER);

        // Score
        Text scoreText = new Text("Monstres tués : " + monstersKilled);
        scoreText.setStyle("-fx-fill: white; -fx-font-size: 24;");
        scoreText.setTextAlignment(TextAlignment.CENTER);

        // Bouton retour au menu
        Button menuButton = createButton("Retour au menu");
        menuButton.setOnAction(e -> {
            stage.close();
            if (onMenuClick != null) onMenuClick.run();
        });

        layout.getChildren().addAll(
            titleText,
            scoreText,
            menuButton
        );

        Scene scene = new Scene(layout, 400, 300);
        scene.setFill(null);
        stage.setScene(scene);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #4a4a4a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18;" +
            "-fx-min-width: 200;" +
            "-fx-padding: 10;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #6a6a6a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18;" +
            "-fx-min-width: 200;" +
            "-fx-padding: 10;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #4a4a4a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18;" +
            "-fx-min-width: 200;" +
            "-fx-padding: 10;"
        ));
        return button;
    }

    public void show() {
        stage.show();
    }
} 