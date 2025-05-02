package fr.beyondtime.view.screens;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class VictoryScreen {
    private Stage stage;
    private int monstersKilled;
    private Runnable onMenuClick;
    private Runnable onNextLevelClick;

    public VictoryScreen(Stage parentStage, int monstersKilled, Runnable onMenuClick, Runnable onNextLevelClick) {
        this.monstersKilled = monstersKilled;
        this.onMenuClick = onMenuClick;
        this.onNextLevelClick = onNextLevelClick;

        // Création de la fenêtre
        stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);

        // Création des éléments de l'interface
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: black;");

        Text victoryText = new Text("Niveau terminé !");
        victoryText.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Text statsText = new Text("Monstres tués : " + monstersKilled);
        statsText.setStyle("-fx-font-size: 16;");

        Button menuButton = new Button("Retour au menu");
        menuButton.setOnAction(e -> {
            stage.close();
            onMenuClick.run();
        });

        Button nextLevelButton = new Button("Niveau suivant");
        nextLevelButton.setOnAction(e -> {
            stage.close();
            onNextLevelClick.run();
        });

        layout.getChildren().addAll(victoryText, statsText, menuButton, nextLevelButton);

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }
} 