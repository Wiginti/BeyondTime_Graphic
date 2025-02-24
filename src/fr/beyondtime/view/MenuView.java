package fr.beyondtime.view;

import fr.beyondtime.util.MapLoader;
import fr.beyondtime.view.editor.EditorView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MenuView {

    public static void showMenuScene(Stage stage) {
        VBox menuPrincipal = new VBox(10);
        menuPrincipal.setAlignment(Pos.CENTER);
        menuPrincipal.getStyleClass().add("vbox-gameview");

        Button jouerButton = new Button("Jouer");
        Button scoresButton = new Button("Scores");
        Button quitterButton = new Button("Quitter");

        // Lorsque l'utilisateur clique sur "Jouer", on affiche le choix des niveaux
        jouerButton.setOnAction(event -> showNiveauScene(stage));
        scoresButton.setOnAction(event -> {
            System.out.println("Affichage des scores à faire plus tard");
        });
        quitterButton.setOnAction(event -> stage.close());

        menuPrincipal.getChildren().addAll(jouerButton, scoresButton, quitterButton);

        Scene sceneMenu = new Scene(menuPrincipal, 400, 300);
        sceneMenu.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );
        stage.setScene(sceneMenu);
        stage.show();
    }

    public static void showNiveauScene(Stage stage) {
        VBox choixNiveau = new VBox(10);
        choixNiveau.setAlignment(Pos.CENTER);
        choixNiveau.getStyleClass().add("vbox-gameview");

        Button classiqueBtn = new Button("Niveau Classique");
        Button personnaliseBtn = new Button("Niveau Personnalisé");
        Button retourBtn = new Button("Retour");

        classiqueBtn.getStyleClass().add("classique-button");
        personnaliseBtn.getStyleClass().add("personnalise-button");

        classiqueBtn.setOnAction(event -> showClassiqueScene(stage));
        personnaliseBtn.setOnAction(event -> showEditorScene(stage));
        retourBtn.setOnAction(event -> showMenuScene(stage));

        choixNiveau.getChildren().addAll(classiqueBtn, personnaliseBtn, retourBtn);

        Scene sceneNiveau = new Scene(choixNiveau, 400, 300);
        sceneNiveau.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );
        stage.setScene(sceneNiveau);
        stage.show();
    }

    public static void showClassiqueScene(Stage stage) {
        VBox classiqueMenu = new VBox(10);
        classiqueMenu.setAlignment(Pos.CENTER);
        classiqueMenu.getStyleClass().add("vbox-gameview");

        Button niveau1Btn = new Button("Niv. 1 - Préhistoire");
        Button niveau2Btn = new Button("Niv. 2 - Égypte Antique");
        Button niveau3Btn = new Button("Niv. 3 - 2nde Guerre Mondiale");
        Button retourBtn = new Button("Retour");

        niveau1Btn.getStyleClass().add("level-button");
        niveau2Btn.getStyleClass().add("level-button");
        niveau3Btn.getStyleClass().add("level-button");

        niveau1Btn.setOnAction(event -> selectAndLoadMap(stage, "Niveau 1 - Préhistoire"));
        niveau2Btn.setOnAction(event -> selectAndLoadMap(stage, "Niveau 2 - Égypte Antique"));
        niveau3Btn.setOnAction(event -> selectAndLoadMap(stage, "Niveau 3 - 2nde Guerre Mondiale"));
        retourBtn.setOnAction(event -> showNiveauScene(stage));

        classiqueMenu.getChildren().addAll(niveau1Btn, niveau2Btn, niveau3Btn, retourBtn);

        Scene sceneClassique = new Scene(classiqueMenu, 400, 300);
        sceneClassique.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );
        stage.setScene(sceneClassique);
        stage.show();
    }

    public static void showEditorScene(Stage stage) {
        EditorView editorView = new EditorView();
        Scene sceneEditor = new Scene(editorView, 800, 600);
        sceneEditor.getStylesheets().add(
                Objects.requireNonNull(MenuView.class.getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
        );
        stage.setScene(sceneEditor);
        stage.show();
    }

    // Permet de sélectionner une map sauvegardée pour un niveau classique et de lancer le gameplay via GameView
    private static void selectAndLoadMap(Stage stage, String levelName) {
        File[] maps = MapLoader.getMapFilesForLevel(levelName);
        if (maps.length == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Chargement");
            alert.setHeaderText(null);
            alert.setContentText("Aucune map sauvegardée pour " + levelName);
            alert.showAndWait();
            return;
        }
        // Préparation de la liste des fichiers
        List<String> choices = new ArrayList<>();
        for (File file : maps) {
            choices.add(file.getName());
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Sélection de map");
        dialog.setHeaderText("Choisissez la map à ouvrir pour " + levelName);
        dialog.setContentText("Map :");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String selectedFileName = result.get();
            File selectedFile = null;
            for (File file : maps) {
                if (file.getName().equals(selectedFileName)) {
                    selectedFile = file;
                    break;
                }
            }
            if (selectedFile != null) {
                GridPane grid = MapLoader.loadMapFromFile(selectedFile);
                if (grid != null) {
                    // Lancement du gameplay en passant la map chargée à GameView
                    new GameView(stage, grid);
                }
            }
        }
    }
}