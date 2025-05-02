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
    private String currentLevel;

    private static final String LEVEL_PREHISTOIRE = "Préhistoire";
    private static final String LEVEL_EGYPTE = "Égypte Antique";
    private static final String LEVEL_WWII = "2nde Guerre Mondiale";

    public VictoryScreen(Stage parentStage, String currentLevel, int monstersKilled, Runnable onMenuClick, Runnable onNextLevelClick) {
        this.currentLevel = currentLevel;
        this.monstersKilled = monstersKilled;
        this.onMenuClick = onMenuClick;
        this.onNextLevelClick = onNextLevelClick;

        // Création de la fenêtre
        stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);

        // Création des éléments de l'interface
        VBox layout = new VBox(20); // Augmenté l'espacement
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #1a2a3d, #2a3a4d);
            -fx-padding: 30;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 2;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
        """);

        Text victoryText;
        if (LEVEL_WWII.equals(currentLevel)) {
            // Message spécial pour la fin du jeu
            victoryText = new Text("FÉLICITATIONS !\nVous avez terminé Beyond Time !");
        } else {
            victoryText = new Text("Niveau terminé !");
        }
        victoryText.setStyle("""
            -fx-font-size: 28;
            -fx-font-weight: bold;
            -fx-fill: #e0e0e0;
            -fx-text-alignment: center;
        """);

        Text statsText = new Text("Monstres vaincus : " + monstersKilled);
        statsText.setStyle("""
            -fx-font-size: 18;
            -fx-fill: #e0e0e0;
        """);

        Button menuButton = createStyledButton("Retour au menu");
        menuButton.setOnAction(e -> {
            stage.close();
            onMenuClick.run();
        });

        layout.getChildren().addAll(victoryText, statsText, menuButton);
        
        // Ajouter le bouton "Niveau suivant" seulement s'il y a un niveau suivant
        if (getNextLevel() != null) {
            Button nextLevelButton = createStyledButton("Niveau suivant");
            nextLevelButton.setOnAction(e -> {
                stage.close();
                onNextLevelClick.run();
            });
            layout.getChildren().add(nextLevelButton);
        }

        // Ajuster la taille de la fenêtre selon le niveau
        int width = LEVEL_WWII.equals(currentLevel) ? 400 : 300;
        int height = LEVEL_WWII.equals(currentLevel) ? 300 : 250;
        Scene scene = new Scene(layout, width, height);
        stage.setScene(scene);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
            -fx-background-color: #2a3a4d;
            -fx-text-fill: #e0e0e0;
            -fx-font-size: 16;
            -fx-padding: 10 20;
            -fx-background-radius: 5;
            -fx-border-radius: 5;
            -fx-border-color: #4a5a6d;
            -fx-border-width: 1;
            -fx-cursor: hand;
            -fx-min-width: 150;
        """);

        button.setOnMouseEntered(e -> 
            button.setStyle(button.getStyle() + "-fx-background-color: #3a4a5d;")
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(button.getStyle().replace("-fx-background-color: #3a4a5d;", "-fx-background-color: #2a3a4d;"))
        );

        return button;
    }

    public String getNextLevel() {
        return switch (currentLevel) {
            case LEVEL_PREHISTOIRE -> LEVEL_EGYPTE;
            case LEVEL_EGYPTE -> LEVEL_WWII;
            case LEVEL_WWII -> null; // Pas de niveau suivant après la 2nde Guerre Mondiale
            default -> null;
        };
    }

    public void show() {
        stage.show();
    }
} 