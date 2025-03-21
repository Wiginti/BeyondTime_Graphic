package fr.beyondtime.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fr.beyondtime.view.GameView;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MapHelper {
	public static void selectAndLoadMap(Stage stage, String levelName) {
        File[] maps = MapLoader.getMapFilesForLevel(levelName);
        if (maps.length == 0) {
            showAlert("Chargement", "Aucune map sauvegardée pour " + levelName);
            return;
        }

        List<String> choices = Arrays.stream(maps)
                                     .map(File::getName)
                                     .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Sélection de map");
        dialog.setHeaderText("Choisissez la map à ouvrir pour " + levelName);
        dialog.setContentText("Map :");

        dialog.showAndWait().ifPresent(selectedFileName -> {
            File selectedFile = Arrays.stream(maps)
                                      .filter(file -> file.getName().equals(selectedFileName))
                                      .findFirst()
                                      .orElse(null);
            if (selectedFile != null) {
                GridPane grid = MapLoader.loadMapFromFile(selectedFile);
                if (grid != null) {
                    new GameView(stage, grid);
                }
            }
        });
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
