package fr.beyondtime.model.config;

import java.util.Locale;
import java.util.prefs.Preferences;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Manages game configuration settings including screen resolution and language.
 */
public class GameConfig {
    private static final String PREF_RESOLUTION = "resolution";
    private static final String PREF_LANGUAGE = "language";

    private static GameConfig instance;
    private final Preferences prefs;

    // Available resolutions (width x height)
    public static final Resolution[] AVAILABLE_RESOLUTIONS = {
        new Resolution(800, 600),
        new Resolution(1024, 768),
        new Resolution(1280, 720),
        new Resolution(1920, 1080)
    };

    // Available languages
    public static final Language[] AVAILABLE_LANGUAGES = {
        new Language("English", "en", Locale.ENGLISH),
        new Language("Français", "fr", Locale.FRENCH)
    };

    private Resolution currentResolution;
    private Language currentLanguage;

    private GameConfig() {
        prefs = Preferences.userNodeForPackage(GameConfig.class);
        loadConfig();
    }

    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    private void loadConfig() {
        String resString = prefs.get(PREF_RESOLUTION, "1280x720");
        String[] res = resString.split("x");
        currentResolution = new Resolution(
            Integer.parseInt(res[0]),
            Integer.parseInt(res[1])
        );

        String lang = prefs.get(PREF_LANGUAGE, "fr");
        currentLanguage = findLanguageByCode(lang);
    }

    public void saveConfig() {
        prefs.put(PREF_RESOLUTION, currentResolution.toString());
        prefs.put(PREF_LANGUAGE, currentLanguage.getCode());
    }

    public Resolution getCurrentResolution() {
        return currentResolution;
    }

    public void setCurrentResolution(Resolution resolution) {
        this.currentResolution = resolution;
        saveConfig();
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(Language language) {
        this.currentLanguage = language;
        saveConfig();
    }

    private Language findLanguageByCode(String code) {
        for (Language lang : AVAILABLE_LANGUAGES) {
            if (lang.getCode().equals(code)) {
                return lang;
            }
        }
        return AVAILABLE_LANGUAGES[0]; // Default to English
    }

    /**
     * Applies the current resolution to the given stage,
     * while ensuring it fits within the visible screen area.
     */
    public void applySafeResolutionToStage(Stage stage) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        int requestedWidth = currentResolution.getWidth();
        int requestedHeight = currentResolution.getHeight();

        double safeWidth = Math.min(requestedWidth, visualBounds.getWidth());
        double safeHeight = Math.min(requestedHeight, visualBounds.getHeight());

        stage.setWidth(safeWidth);
        stage.setHeight(safeHeight);
        stage.setX(visualBounds.getMinX() + (visualBounds.getWidth() - safeWidth) / 2);
        stage.setY(visualBounds.getMinY() + (visualBounds.getHeight() - safeHeight) / 2);
    }

    public static class Resolution {
        private final int width;
        private final int height;

        public Resolution(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() { return width; }
        public int getHeight() { return height; }

        @Override
        public String toString() {
            return width + "x" + height;
        }
    }

    public static class Language {
        private final String name;
        private final String code;
        private final Locale locale;

        public Language(String name, String code, Locale locale) {
            this.name = name;
            this.code = code;
            this.locale = locale;
        }

        public String getName() { return name; }
        public String getCode() { return code; }
        public Locale getLocale() { return locale; }

        @Override
        public String toString() {
            return name;
        }
    }
}
