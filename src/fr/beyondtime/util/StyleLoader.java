package fr.beyondtime.util;

import java.util.Objects;

public class StyleLoader {
	
    private static final String STYLE_PATH = "/fr/beyondtime/resources/style.css";

    public static String getStyleSheet() {
        return Objects.requireNonNull(
            StyleLoader.class.getResource(STYLE_PATH)
        ).toExternalForm();
    }
    
    public static String getStyleSheet(String path) {
    	return Objects.requireNonNull(
    		StyleLoader.class.getResource(path)
    	).toExternalForm();
    }
    
}
