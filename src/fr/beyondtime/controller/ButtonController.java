package fr.beyondtime.controller;

import javafx.stage.Stage;

import static fr.beyondtime.view.MenuView.*;

public class ButtonController {
    private Stage stage;

    public ButtonController(Stage stage) {
        this.stage = stage;
    }

    // Méthode appelée lors du clic sur le bouton
    public void quitAction() {
        showNiveauScene();
    }

    public void menuAction() {
        showMenuScene();
    }

    // Implémentez ici la logique pour changer de scène
    private void showNiveauScene() {
        stage.setScene(getSceneNiveau());
    }

    private void showMenuScene() {
        stage.setScene(getSceneMenu());
    }

    private void showEditorScene() {
        stage.setScene(getSceneEditor());
    }

    private void showClassiqueScene() {
        stage.setScene(getSceneClassique());
    }
}
