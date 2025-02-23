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
    private Button niveau1Btn;
    private Button niveau2Btn;
    private Button niveau3Btn;

    public MenuView() {
        this(false, false);
    }

    public MenuView(boolean isNiveau, boolean isClassique) {
        super();
        this.getStyleClass().add("vbox-gameview");

        if (!isNiveau) {
            jouerButton = new Button("Jouer");
            scoresButton = new Button("Scores");
            quitterButton = new Button("Quitter");
            getChildren().addAll(jouerButton, scoresButton, quitterButton);
        } else if (!isClassique) {
            classiqueBtn = new Button("Niveau Classique");
            personnaliseBtn = new Button("Niveau Personnalisé");
            retourBtn = new Button("Retour");
            classiqueBtn.getStyleClass().add("classique-button");
            personnaliseBtn.getStyleClass().add("personnalise-button");
            getChildren().addAll(classiqueBtn, personnaliseBtn, retourBtn);
        } else {
            niveau1Btn = new Button("Niv. 1 - Préhistoire");
            niveau2Btn = new Button("Niv. 2 - Égypte Antique");
            niveau3Btn = new Button("Niv. 3 - 2nde Guerre Mondiale");
            retourBtn = new Button("Retour");
            niveau1Btn.getStyleClass().add("level-button");
            niveau2Btn.getStyleClass().add("level-button");
            niveau3Btn.getStyleClass().add("level-button");
            getChildren().addAll(niveau1Btn, niveau2Btn, niveau3Btn, retourBtn);
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

    public Button getNiveau1Btn() {
        return niveau1Btn;
    }

    public Button getNiveau2Btn() {
        return niveau2Btn;
    }

    public Button getNiveau3Btn() {
        return niveau3Btn;
    }

    // Affiche le menu principal
    public static void showMenuScene(Stage stage) {
        MenuView menuPrincipal = new MenuView(false, false);
        Scene sceneMenu = new Scene(menuPrincipal);
        sceneMenu.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );

        menuPrincipal.getJouerButton().setOnAction(event -> showNiveauScene(stage));
        menuPrincipal.getScoresButton().setOnAction(event -> {
            System.out.println("Affichage des scores à faire plus tard");
        });
        menuPrincipal.getQuitterButton().setOnAction(event -> stage.close());

        stage.setScene(sceneMenu);
        stage.show();
    }

    // Affiche le choix du niveau
    public static void showNiveauScene(Stage stage) {
        MenuView choixNiveau = new MenuView(true, false);
        Scene sceneNiveau = new Scene(choixNiveau);
        sceneNiveau.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );

        choixNiveau.getRetourBtn().setOnAction(event -> showMenuScene(stage));
        choixNiveau.getPersonnaliseBtn().setOnAction(event -> showEditorScene(stage));
        choixNiveau.getClassiqueBtn().setOnAction(event -> showClassiqueScene(stage));

        stage.setScene(sceneNiveau);
        stage.show();
    }

    // Affiche la scène du mode classique
    public static void showClassiqueScene(Stage stage) {
        MenuView classiqueNiveau = new MenuView(true, true);
        Scene sceneClassique = new Scene(classiqueNiveau);
        sceneClassique.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );

        classiqueNiveau.getRetourBtn().setOnAction(event -> showNiveauScene(stage));
        classiqueNiveau.getNiveau1Btn().setOnAction(event -> {
            System.out.println("Lancement du Niveau 1 - Préhistoire");
        });
        classiqueNiveau.getNiveau2Btn().setOnAction(event -> {
            System.out.println("Lancement du Niveau 2 - Égypte Antique");
        });
        classiqueNiveau.getNiveau3Btn().setOnAction(event -> {
            System.out.println("Lancement du Niveau 3 - 2nd Guerre Mondiale");
        });

        stage.setScene(sceneClassique);
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
        stage.show();
    }
}