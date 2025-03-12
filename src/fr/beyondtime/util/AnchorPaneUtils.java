package fr.beyondtime.util;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class AnchorPaneUtils {
    public static void setCenterAnchor(Node node, double offset) {
        AnchorPane.setTopAnchor(node, offset);
        AnchorPane.setBottomAnchor(node, offset);
        AnchorPane.setLeftAnchor(node, offset);
        AnchorPane.setRightAnchor(node, offset);
    }
}
