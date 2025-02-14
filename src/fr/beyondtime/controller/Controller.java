package fr.beyondtime.controller;

import fr.beyondtime.model.GameModel;
import fr.beyondtime.view.GameView;
import javafx.scene.Scene;

public class Controller {
	
	private GameModel model;
	private GameView view;
	private Scene scene;
	
	public Controller(GameModel model, GameView view, Scene scene) {
		this.model = model;
		this.view = view;
		this.scene = scene;
	}
  //test
}
