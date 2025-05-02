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
    private static final Locale FALLBACK_LOCALE = Locale.ENGLISH;

    private TranslationManager() {
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
     * @return true si le changement a réussi, false sinon
     */
    public boolean setLocale(Locale locale) {
        try {
            ClassLoader classLoader = TranslationManager.class.getClassLoader();
            ResourceBundle newBundle = ResourceBundle.getBundle(BUNDLE_PATH, locale, classLoader);
            
            if (newBundle.keySet().isEmpty()) {
                return false;
            }
            
            bundle = newBundle;
            currentLocale.set(locale);
            return true;
        } catch (MissingResourceException e) {
            if (!locale.equals(FALLBACK_LOCALE)) {
                return setLocale(FALLBACK_LOCALE);
            }
            return false;
        }
    }

    /**
     * Obtient la traduction pour une clé donnée.
     *
     * @param key La clé de traduction
     * @return La traduction correspondante ou la clé si non trouvée
     */
    public String get(String key) {
        if (key == null) {
            return "";
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Vérifie si une clé de traduction existe.
     *
     * @param key La clé à vérifier
     * @return true si la clé existe, false sinon
     */
    public boolean hasTranslation(String key) {
        if (key == null || bundle == null) return false;
        return bundle.containsKey(key);
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