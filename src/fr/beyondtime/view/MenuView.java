package fr.beyondtime.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MenuView extends VBox {

    private Button jouerButton;
    private Button scoresButton;
    private Button quitterButton;
    private Button classiqueBtn;
    private Button personnaliseBtn;
    private Button retourBtn;

    public MenuView() {
        this(false);
    }

    public MenuView(boolean isNiveau) {
        super();
        this.getStyleClass().add("vbox-gameview");

        if (!isNiveau) {
            jouerButton = new Button("Jouer");
            scoresButton = new Button("Scores");
            quitterButton = new Button("Quitter");
            getChildren().addAll(jouerButton, scoresButton, quitterButton);
        } else {
            classiqueBtn = new Button("Niveau Classique");
            personnaliseBtn = new Button("Niveau Personnalisé");
            retourBtn = new Button("Retour");
            classiqueBtn.getStyleClass().add("classique-button");
            personnaliseBtn.getStyleClass().add("personnalise-button");
            getChildren().addAll(classiqueBtn, personnaliseBtn, retourBtn);
        }

        setAlignment(Pos.CENTER);
    }

    public Button getJouerButton() {
        return jouerButton;
    }

    public Button getScoresButton() {
        return scoresButton;
    }

    public Button getQuitterButton() {
        return quitterButton;
    }

    public Button getClassiqueBtn() {
        return classiqueBtn;
    }

    public Button getPersonnaliseBtn() {
        return personnaliseBtn;
    }

    public Button getRetourBtn() {
        return retourBtn;
    }
}