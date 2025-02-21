package fr.beyondtime.main;

import fr.beyondtime.view.MenuView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Menu de Jeu");
		MenuView.showMenuScene(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}