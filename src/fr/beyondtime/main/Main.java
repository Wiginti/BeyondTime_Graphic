package fr.beyondtime.main;

import fr.beyondtime.view.MenuView;
import fr.beyondtime.view.editor.EditorView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

	private void showMenuScene(Stage primaryStage) {
		MenuView menuPrincipal = new MenuView(false);
		Scene sceneMenu = new Scene(menuPrincipal);
		sceneMenu.getStylesheets().add(
				Objects.requireNonNull(getClass().getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
		);
		menuPrincipal.getJouerButton().setOnAction(event -> showNiveauScene(primaryStage));
		menuPrincipal.getScoresButton().setOnAction(event -> {
			System.out.println("affichage score a faire plus tard");
		});
		menuPrincipal.getQuitterButton().setOnAction(event -> primaryStage.close());

		primaryStage.setScene(sceneMenu);
		primaryStage.setFullScreen(true);
		primaryStage.show();
	}

	private void showNiveauScene(Stage primaryStage) {
		MenuView choixNiveau = new MenuView(true);
		Scene sceneNiveau = new Scene(choixNiveau);
		sceneNiveau.getStylesheets().add(
				Objects.requireNonNull(getClass().getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
		);

		// Bouton pour retourner au menu principal
		choixNiveau.getRetourBtn().setOnAction(event -> showMenuScene(primaryStage));

		// Ajout d'un écouteur sur le bouton "Niveau Personnalisé"
		choixNiveau.getPersonnaliseBtn().setOnAction(event -> showEditorScene(primaryStage));

		primaryStage.setScene(sceneNiveau);
		primaryStage.setFullScreen(true);
		primaryStage.show();
	}

	// Nouvelle méthode permettant d'afficher l'interface de l'éditeur (EditorView)
	private void showEditorScene(Stage primaryStage) {
		EditorView editorView = new EditorView();
		Scene sceneEditor = new Scene(editorView);
		sceneEditor.getStylesheets().add(
				Objects.requireNonNull(getClass().getResource("/fr/beyondtime/resources/style.css")).toExternalForm()
		);

		// (Optionnel) Vous pouvez prévoir un bouton ou un autre mécanisme dans EditorView pour revenir en arrière.

		primaryStage.setScene(sceneEditor);
		primaryStage.setFullScreen(true);
		primaryStage.show();
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Menu de Jeu");
		showMenuScene(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}