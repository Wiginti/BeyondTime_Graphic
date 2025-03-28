package fr.beyondtime.main;

import fr.beyondtime.view.MenuView;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
    	
    	//Récupérer les dimensions de l'écran utilisateur
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Image icon = new Image(getClass().getResourceAsStream("/fr/beyondtime/resources/logo.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setTitle("BeyondTime");

        // Affichage de la scène du menu
        MenuView.showMenuScene(primaryStage);

        // Récupérer la scène déjà définie par MenuView
        Scene scene = primaryStage.getScene();

        // Ajout d'un écouteur pour détecter l'appui sur F11
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
