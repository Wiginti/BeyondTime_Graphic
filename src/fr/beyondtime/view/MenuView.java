package fr.beyondtime.view;

import fr.beyondtime.util.LevelNames;
import fr.beyondtime.util.MapManager;
import fr.beyondtime.util.StyleLoader;
import fr.beyondtime.view.editor.EditorView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuView {

	// Afficher le Menu Principal
    public static void showMenuScene(Stage stage) {
        VBox menuPrincipal = createMainMenu(stage);
        Scene sceneMenu = new Scene(menuPrincipal, 400, 300);
        applyStyle(sceneMenu);
        stage.setScene(sceneMenu);
        stage.show();
    }
    
    // Afficher le Menu des niveaux 
    public static void showNiveauScene(Stage stage) {
        VBox choixNiveau = createNiveauMenu(stage);
        Scene sceneNiveau = new Scene(choixNiveau, 400, 300);
        applyStyle(sceneNiveau);
        stage.setScene(sceneNiveau);
        stage.show();
    }
    
    // Afficher le Menu des niveaux classiques
    public static void showClassiqueScene(Stage stage) {
        VBox classiqueMenu = createClassiqueMenu(stage);
        Scene sceneClassique = new Scene(classiqueMenu, 400, 300);
        applyStyle(sceneClassique);
        stage.setScene(sceneClassique);
        stage.show();
    }
    
    // Afficher l'éditeur
    public static void showEditorScene(Stage stage) {
        EditorView editorView = new EditorView();
        Scene sceneEditor = new Scene(editorView, 600, 400);
        applyStyle(sceneEditor);
        stage.setScene(sceneEditor);
        stage.show();
    }

    // Crée le menu principal
    private static VBox createMainMenu(Stage stage) {
        VBox menu = new VBox(10);
        menu.setAlignment(Pos.CENTER);
        menu.getStyleClass().add("vbox-gameview");

        Button jouerButton = createButton("Jouer", event -> showNiveauScene(stage), null);
        Button scoresButton = createButton("Scores", event -> System.out.println("Affichage des scores à faire plus tard"), null);
        Button quitterButton = createButton("Quitter", event -> stage.close(), null);

        menu.getChildren().addAll(jouerButton, scoresButton, quitterButton);
        return menu;
    }

    // Crée le menu de sélection de niveau
    private static VBox createNiveauMenu(Stage stage) {
        VBox choixNiveau = new VBox(10);
        choixNiveau.setAlignment(Pos.CENTER);
        choixNiveau.getStyleClass().add("vbox-gameview");

        Button classiqueBtn = createButton("Niveau Classique", event -> showClassiqueScene(stage), "classique-button");
        Button personnaliseBtn = createButton("Map Editor", event -> showEditorScene(stage), "personnalise-button");
        Button retourBtn = createButton("Retour", event -> showMenuScene(stage), null);

        choixNiveau.getChildren().addAll(classiqueBtn, personnaliseBtn, retourBtn);
        return choixNiveau;
    }

    // Crée le menu des niveaux classiques
    private static VBox createClassiqueMenu(Stage stage) {
        VBox classiqueMenu = new VBox(10);
        classiqueMenu.setAlignment(Pos.CENTER);
        classiqueMenu.getStyleClass().add("vbox-gameview");

        Button niveau1Btn = createButton("Niv. 1 - Préhistoire", event -> MapManager.selectAndLoadMap(stage, LevelNames.PREHISTOIRE), "level-button");
        Button niveau2Btn = createButton("Niv. 2 - Égypte Antique", event -> MapManager.selectAndLoadMap(stage, LevelNames.EGYPTE_ANTIQUITE), "level-button");
        Button niveau3Btn = createButton("Niv. 3 - 2nde Guerre Mondiale", event -> MapManager.selectAndLoadMap(stage, LevelNames.SECONDE_GUERRE), "level-button");
        Button retourBtn = createButton("Retour", event -> showNiveauScene(stage), null);

        classiqueMenu.getChildren().addAll(niveau1Btn, niveau2Btn, niveau3Btn, retourBtn);
        return classiqueMenu;
    }

    // Crée un bouton avec un texte, un gestionnaire d'événements et une classe CSS optionnelle
    private static Button createButton(String text, EventHandler<ActionEvent> eventHandler, String styleClass) {
        Button button = new Button(text);
        button.setOnAction(eventHandler);
        if (styleClass != null) {
            button.getStyleClass().add(styleClass);
        }
        return button;
    }

    // Applique le fichier CSS à une scène
    private static void applyStyle(Scene scene) {
        scene.getStylesheets().add(StyleLoader.getStyleSheet());
    }
}
