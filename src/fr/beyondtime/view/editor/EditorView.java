package fr.beyondtime.view.editor;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class EditorView extends VBox {

    public EditorView() {
        // Initialisation de la taille de la VBox
        setPrefHeight(600.0);
        setPrefWidth(900.0);

        // Création du MenuBar
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem closeItem = new MenuItem("Close");
        fileMenu.getItems().add(closeItem);

        Menu editMenu = new Menu("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        editMenu.getItems().add(deleteItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        // Création du SplitPane
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.2505567928730512);

        // Panneau de gauche
        AnchorPane leftPane = new AnchorPane();
        TilePane tilePane = new TilePane();
        tilePane.setLayoutX(-26.0);
        tilePane.setPrefHeight(200.0);
        tilePane.setPrefWidth(200.0);
        leftPane.getChildren().add(tilePane);

        // Panneau central avec ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        AnchorPane contentPane = new AnchorPane();
        contentPane.setMinHeight(-1.0);
        contentPane.setMinWidth(-1.0);
        contentPane.setPrefHeight(545.0);
        contentPane.setPrefWidth(430.0);
        scrollPane.setContent(contentPane);

        splitPane.getItems().addAll(leftPane, scrollPane);

        // Ajouter tous les composants à la VBox
        getChildren().addAll(menuBar, splitPane);
    }
}
