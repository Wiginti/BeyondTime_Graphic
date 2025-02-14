package fr.beyondtime.main;

import fr.beyondtime.controller.Controller;
import fr.beyondtime.model.GameModel;
import fr.beyondtime.view.GameView;
import fr.beyondtime.view.editor.EditorView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		GameModel model = new GameModel();
		EditorView view = new EditorView();
		Scene scene = new Scene(view, 800, 700);	
		new Controller(model, view, scene);
		
		primaryStage.setTitle("BeyondTime");
        primaryStage.setScene(scene);
        primaryStage.show();
	}

    public static void main(String[] args) {
        launch(args);
    }
	
}
