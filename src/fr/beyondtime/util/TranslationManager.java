package fr.beyondtime.util;

import fr.beyondtime.model.config.GameConfig;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Manages translations and language changes in the application.
 */
public class TranslationManager {
    private static TranslationManager instance;
    private ResourceBundle bundle;
    private final ObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>();
    private static final String BUNDLE_PATH = "fr.beyondtime.resources.translations.messages";

    private TranslationManager() {
        // Initialiser avec la langue par défaut
        setLocale(GameConfig.getInstance().getCurrentLanguage().getLocale());
    }

    public static TranslationManager getInstance() {
        if (instance == null) {
            instance = new TranslationManager();
        }
        return instance;
    }

    /**
     * Change la langue courante et recharge les traductions.
     *
     * @param locale La nouvelle locale à utiliser
     */
    public void setLocale(Locale locale) {
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_PATH, locale);
            currentLocale.set(locale);
            System.out.println("Langue changée pour : " + locale.getDisplayLanguage());
        } catch (MissingResourceException e) {
            System.err.println("Erreur lors du chargement des traductions pour " + locale);
            e.printStackTrace();
            // Fallback to default locale
            bundle = ResourceBundle.getBundle(BUNDLE_PATH, Locale.ENGLISH);
            currentLocale.set(Locale.ENGLISH);
        }
    }

    /**
     * Obtient la traduction pour une clé donnée.
     *
     * @param key La clé de traduction
     * @return La traduction correspondante ou la clé si non trouvée
     */
    public String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            System.err.println("Clé de traduction manquante : " + key);
            return key;
        }
    }

    /**
     * Obtient la propriété de la locale courante pour les bindings.
     *
     * @return La propriété de la locale courante
     */
    public ObjectProperty<Locale> currentLocaleProperty() {
        return currentLocale;
    }

    /**
     * Obtient la locale courante.
     *
     * @return La locale courante
     */
    public Locale getCurrentLocale() {
        return currentLocale.get();
    }
} 