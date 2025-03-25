package fr.beyondtime.controller;

import javafx.stage.Stage;

import static fr.beyondtime.view.MenuView.getSceneNiveau;

public class ButtonController {
    private Stage stage;

    public ButtonController(Stage stage) {
        this.stage = stage;
    }

    // Méthode appelée lors du clic sur le bouton
    public void quitAction() {
        showNiveauScene();
    }

    // Implémentez ici la logique pour changer de scène
    private void showNiveauScene() {
        System.out.println("Changement de scène vers NiveauScene");
        stage.setScene(getSceneNiveau());
    }
}
