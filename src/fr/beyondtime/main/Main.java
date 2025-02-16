package fr.beyondtime.main;

import fr.beyondtime.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private void showMenuScene(Stage primaryStage) {
		GameView menuPrincipal = new GameView(false);
		Scene sceneMenu = new Scene(menuPrincipal);
		sceneMenu.getStylesheets().add(
				getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm()
		);
		menuPrincipal.getJouerButton().setOnAction(event -> showNiveauScene(primaryStage));
		menuPrincipal.getScoresButton().setOnAction(event -> {
			System.out.println("affichage score a faire plus tard");
		});
		menuPrincipal.getQuitterButton().setOnAction(event -> {
			primaryStage.close();
		});
		primaryStage.setScene(sceneMenu);
		primaryStage.setFullScreen(true);
		primaryStage.show();
	}

	private void showNiveauScene(Stage primaryStage) {
		GameView choixNiveau = new GameView(true);
		Scene sceneNiveau = new Scene(choixNiveau);
		sceneNiveau.getStylesheets().add(
				getClass().getResource("/fr/beyondtime/resources/style.css").toExternalForm()
		);
		choixNiveau.getRetourBtn().setOnAction(event -> showMenuScene(primaryStage));
		primaryStage.setScene(sceneNiveau);
		primaryStage.setFullScreen(true);
		primaryStage.show();
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Menu de Jeu");
		showMenuScene(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}