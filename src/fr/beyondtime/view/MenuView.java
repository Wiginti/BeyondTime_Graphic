package fr.beyondtime.view;

import fr.beyondtime.view.editor.EditorView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

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

    // Affiche le menu principal
    public static void showMenuScene(Stage stage) {
        MenuView menuPrincipal = new MenuView(false);
        Scene sceneMenu = new Scene(menuPrincipal);
        sceneMenu.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );

        menuPrincipal.getJouerButton().setOnAction(event -> showNiveauScene(stage));
        menuPrincipal.getScoresButton().setOnAction(event -> {
            System.out.println("affichage score à faire plus tard");
        });
        menuPrincipal.getQuitterButton().setOnAction(event -> stage.close());

        stage.setScene(sceneMenu);
        stage.setFullScreen(true);
        stage.show();
    }

    // Affiche le choix du niveau
    public static void showNiveauScene(Stage stage) {
        MenuView choixNiveau = new MenuView(true);
        Scene sceneNiveau = new Scene(choixNiveau);
        sceneNiveau.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );

        choixNiveau.getRetourBtn().setOnAction(event -> showMenuScene(stage));
        choixNiveau.getPersonnaliseBtn().setOnAction(event -> showEditorScene(stage));
        // Vous pouvez également définir une action pour le bouton classique

        stage.setScene(sceneNiveau);
        stage.setFullScreen(true);
        stage.show();
    }

    // Affiche l'interface de l'éditeur
    public static void showEditorScene(Stage stage) {
        EditorView editorView = new EditorView();
        Scene sceneEditor = new Scene(editorView);
        sceneEditor.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );

        stage.setScene(sceneEditor);
        stage.setFullScreen(true);
        stage.show();
    }
}