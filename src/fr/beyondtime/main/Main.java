package fr.beyondtime.main;

import fr.beyondtime.controller.Controller;
import fr.beyondtime.model.GameModel;
import fr.beyondtime.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		GameModel model = new GameModel();
		GameView view = new GameView();
		Scene scene = new Scene(view, 400, 400);	
		new Controller(model, view, scene);
		
		primaryStage.setTitle("BeyondTime");
        primaryStage.setScene(scene);
        primaryStage.show();
	}

    public static void main(String[] args) {
        launch(args);
    }
	
}
