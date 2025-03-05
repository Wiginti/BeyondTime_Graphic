package fr.beyondtime.controller;

import fr.beyondtime.model.GameModel;
import fr.beyondtime.view.editor.EditorView;
import javafx.scene.Scene;

public class Controller {
	
	private GameModel model;
	private EditorView view;
	private Scene scene;
	
	public Controller(GameModel model, EditorView view, Scene scene) {
		this.model = model;
		this.view = view;
		this.scene = scene;
	}
	
	public GameModel getModel() {
		return this.model;
	}
	
	public EditorView getView() {
		return this.view;
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
}
