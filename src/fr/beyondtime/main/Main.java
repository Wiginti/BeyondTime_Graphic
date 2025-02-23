package fr.beyondtime.main;

import fr.beyondtime.view.MenuView;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		// Récupération de la zone visible de l'écran
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setX(screenBounds.getMinX());
		primaryStage.setY(screenBounds.getMinY());
		primaryStage.setWidth(screenBounds.getWidth());
		primaryStage.setHeight(screenBounds.getHeight());
		primaryStage.setTitle("Menu de Jeu");

		// Affichage du menu principal
		MenuView.showMenuScene(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}